package serializers;

import com.dyuproject.protostuff.CollectionSchema;
import com.dyuproject.protostuff.MapSchema;
import com.dyuproject.protostuff.runtime.ExplicitIdStrategy;
import com.dyuproject.protostuff.runtime.IdStrategy;
import com.dyuproject.protostuff.runtime.RuntimeEnv;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * @author Alvin
 * @since 27/7/12 4:36 PM
 */
public class ExplicitIdStrategyFactory implements IdStrategy.Factory {

  private static final Logger log = LoggerFactory.getLogger(ExplicitIdStrategyFactory.class);

  ExplicitIdStrategy.Registry registry = new ExplicitIdStrategy.Registry();

  public ExplicitIdStrategyFactory() {
  }

  /**
   * Creates a new {@link IdStrategy} instance using ExplicitIdStrategyFactory.
   */
  public IdStrategy create() {
    return registry.strategy;
  }

  /**
   * Called after the method {@link #create()} has been called to register every enum and message class with explicit ID in remote.conf files.
   * This is used to prevent classloader issues.
   * RuntimeEnv's {@link RuntimeEnv#ID_STRATEGY} need to be set first.
   */
  public void postCreate() {

    // Interface and abstract class should not be registered
// 		registry.registerCollection(CollectionSchema.MessageFactories.Collection,	1);
// 		.registerCollection(CollectionSchema.MessageFactories.List,					2)
//		.registerCollection(CollectionSchema.MessageFactories.Set,					3)
//		.registerCollection(CollectionSchema.MessageFactories.SortedSet,			4)
//		.registerCollection(CollectionSchema.MessageFactories.NavigableSet,			5)
//		.registerCollection(CollectionSchema.MessageFactories.Queue,				6)
//		.registerCollection(CollectionSchema.MessageFactories.BlockingQueue,		7)
//		.registerCollection(CollectionSchema.MessageFactories.Deque,				8)
//		.registerCollection(CollectionSchema.MessageFactories.BlockingDeque,		9)

    registry.registerCollection(CollectionSchema.MessageFactories.ArrayList, 2)
        .registerCollection(CollectionSchema.MessageFactories.LinkedList, 3)
        .registerCollection(CollectionSchema.MessageFactories.CopyOnWriteArrayList, 4)
        .registerCollection(CollectionSchema.MessageFactories.Stack, 5)
        .registerCollection(CollectionSchema.MessageFactories.HashSet, 6)
        .registerCollection(CollectionSchema.MessageFactories.LinkedHashSet, 7)
        .registerCollection(CollectionSchema.MessageFactories.TreeSet, 8)
        .registerCollection(CollectionSchema.MessageFactories.ConcurrentSkipListSet, 9)
        .registerCollection(CollectionSchema.MessageFactories.CopyOnWriteArraySet, 10)
        .registerCollection(CollectionSchema.MessageFactories.LinkedBlockingQueue, 11)
        .registerCollection(CollectionSchema.MessageFactories.LinkedBlockingDeque, 12)
        .registerCollection(CollectionSchema.MessageFactories.ArrayBlockingQueue, 13)
        .registerCollection(CollectionSchema.MessageFactories.ArrayDeque, 14)
        .registerCollection(CollectionSchema.MessageFactories.ConcurrentLinkedQueue, 15)
        .registerCollection(CollectionSchema.MessageFactories.PriorityBlockingQueue, 16)
        .registerCollection(CollectionSchema.MessageFactories.PriorityQueue, 17);
    //java.util.HashMap$Values
//			.registerCollection(CustomArrayList.MESSAGE_FACTORY, 5)

//		registry.registerMap(MapSchema.MessageFactories.Map,				1)
//			.registerMap(MapSchema.MessageFactories.SortedMap,				2)
//			.registerMap(MapSchema.MessageFactories.NavigableMap,			3)
//			.registerMap(MapSchema.MessageFactories.ConcurrentMap,			4)
//			.registerMap(MapSchema.MessageFactories.ConcurrentNavigableMap, 5)

    registry.registerMap(MapSchema.MessageFactories.HashMap, 2)
        .registerMap(MapSchema.MessageFactories.LinkedHashMap, 3)
        .registerMap(MapSchema.MessageFactories.TreeMap, 4)
        .registerMap(MapSchema.MessageFactories.WeakHashMap, 5)
        .registerMap(MapSchema.MessageFactories.IdentityHashMap, 6)
        .registerMap(MapSchema.MessageFactories.Hashtable, 7)
        .registerMap(MapSchema.MessageFactories.ConcurrentHashMap, 8)
        .registerMap(MapSchema.MessageFactories.ConcurrentSkipListMap, 9);
//			.registerMap(CustomHashMap.MESSAGE_FACTORY, 4)

    // Enumerate every remote.conf file
    Collection<Config> configs = getRemoteConfigs();

    List<String> errors = Lists.newLinkedList();

    Set<Class<?>> pojoClasses = Sets.newHashSet();

    for (Config config : configs) {
      log.debug("Loading remote configuration from {}", config.origin());

      Config configMapping = config.getConfig("class-mapping");
      if (configMapping == null) {
        log.error("remote file:{} has not class-mapping node.", config.origin());
        continue;
      }

      // Register every enum class with unique identifier
      if (configMapping.hasPath("enums")) {
        Config configEnums = configMapping.getConfig("enums");
        Set<Map.Entry<String, ConfigValue>> setEnums = configEnums.entrySet();
        for (Map.Entry<String, ConfigValue> entryEnum : setEnums) {
          String keyEnum = entryEnum.getKey().replace("\"", "");
          Class<?> classEnum = null;
          try {
            classEnum = Class.forName(keyEnum);
          } catch (ClassNotFoundException e) {
            errors.add("Enum: " + keyEnum + " in config file: " + config.origin() + " is not found.");
            continue;
          }

          if (!classEnum.isEnum()) {
            errors.add("Enum: " + keyEnum + " in config file: " + config.origin() + " is not a enum.");
            continue;
          }

          Object indexID = entryEnum.getValue().unwrapped();
          if (!(indexID instanceof Integer)) {
            errors.add("Enum: " + keyEnum + " in config file: " + config.origin() + " has not correct ID.");
            continue;
          }

          registry.registerEnum((Class) classEnum, (Integer) indexID);
        }
      }

      // Register every message class with unique identifier
      if (configMapping.hasPath("pojos")) {
        Config configPojos = configMapping.getConfig("pojos");
        Set<Map.Entry<String, ConfigValue>> setPojos = configPojos.entrySet();
        for (Map.Entry<String, ConfigValue> entryPojo : setPojos) {
          String keyPojo = entryPojo.getKey().replace("\"", "");
          Class<?> classPojo = null;
          try {
            classPojo = Class.forName(keyPojo);
          } catch (ClassNotFoundException e) {
            errors.add("Message class: " + keyPojo + " in config file: " + config.origin() + " is not found.");
            continue;
          }

          Object indexID = entryPojo.getValue().unwrapped();
          if (!(indexID instanceof Integer)) {
            errors.add("Message class: " + keyPojo + " in config file: " + config.origin() + " has not correct ID.");
            continue;
          }

          registry.registerPojo(classPojo, (Integer) (indexID));

          pojoClasses.add(classPojo);
        }
      }
    }

    if (!errors.isEmpty()) {
      throw new ExplicitIdStrategyException(Joiner.on("\n").join(errors));
    }
  }

  protected Collection<Config> getRemoteConfigs() {

    Set<URL> urlIncluded = Sets.newHashSet();

    Enumeration<URL> resources = null;
    try {
      resources = getClass().getClassLoader().getResources("remote.conf");
    } catch (IOException e) {
      throw new ExplicitIdStrategyException(e);
    }

    List<Config> configs = Lists.newLinkedList();

    while (resources.hasMoreElements()) {
      URL url = resources.nextElement();

      if (urlIncluded.add(url)) {
        Config config = ConfigFactory.parseURL(url);

        configs.add(config);
      } else {
        log.warn("Ignored duplicate URL enumerated: {}", url);
      }
    }

    return configs;
  }
}
