package serializers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.IdStrategy;
import com.dyuproject.protostuff.runtime.IdStrategy.UnknownTypeException;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.google.common.base.Preconditions;

import akka.serialization.JSerializer;

/**
 * @author Alvin
 * @since 27/7/12 3:26 PM
 */
@SuppressWarnings({"unchecked"})
public class ExplicitIdStrategyRuntimeSerializer extends JSerializer {

	private static final Logger log = LoggerFactory.getLogger(ExplicitIdStrategyRuntimeSerializer.class);
	
	static {
		// must enable to support inheritance on non-abstract base types.
		System.setProperty("protostuff.runtime.morph_non_final_pojos", "true");
	}
	
	private static IdStrategy registry = getStrategy();
	
	private static synchronized IdStrategy getStrategy() {
		IdStrategy.Factory factory = new ExplicitIdStrategyFactory();
		IdStrategy idStrategy = factory.create();
		
		factory.postCreate();
		
		return idStrategy;
	}
	
	/**
	 * Completely unique value to identify this implementation of Serializer, used to optimize network traffic
	 * Values from 0 to 16 is reserved for Akka internal usage
	 */
	@Override
	public int identifier() {
		return 43;
	}

	/**
	 * Returns whether this serializer needs a manifest in the fromBinary method(Whether "fromBinary" requires a "clazz" or not)
	 */
	@Override
	public boolean includeManifest() {
		return true;
	}

	/**
	 * Serializes the given object to an Array of Bytes
	 */
	@Override
	public byte[] toBinary(Object o) {
		Preconditions.checkNotNull(o, "Object is null.");
		
		try {
		    @SuppressWarnings("rawtypes")
			Schema schema = RuntimeSchema.getSchema(o.getClass(), registry);
		    LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
		    return ProtostuffIOUtil.toByteArray(o, schema, buffer);
		} catch(UnknownTypeException e) {
			if(o == null) {
				log.error("Can not serialize null object, exception message: {}", e.getMessage());
			} else {
				log.error("Can not find the class: {} in class-mapping of remote.conf file while serializing an object, exception message: {}", o.getClass().getName(), e.getMessage());
			}
		} catch(Exception e) {
			if (o == null) {
				log.error("An error has occurred while serializing a 'null' object", e);
			} else {
				log.error("An error has occurred while serializing an object of type: " + o.getClass(), e);
			}
		}
		
		return null;
	}

	/**
	 * Deserializes the given array, using the type hint (if any, see "includeManifest" above)
	 */
	@Override
	public Object fromBinaryJava(byte[] bytes, Class<?> aClass) {
		Preconditions.checkNotNull(bytes, "Byte array is null.");
		Preconditions.checkNotNull(aClass, "Class type is null.");
		
		try {
			@SuppressWarnings("rawtypes")
			Schema schema = RuntimeSchema.getSchema(aClass, registry);
			Object loadedObject = aClass.newInstance();
			ProtostuffIOUtil.mergeFrom(bytes, loadedObject, schema);
			return loadedObject;
		} catch (Exception e) {
			log.error("An error has occurred while de-serializing an object", e);
		}
		
		return null;
	}
}
