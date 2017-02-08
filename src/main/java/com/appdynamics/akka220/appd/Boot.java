package com.appdynamics.akka220.appd;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.kernel.Bootable;

import com.appdynamics.akka220.appd.poc.PocModule;
import com.appdynamics.akka220.appd.poc.messages.internal.Probe;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Boot implements Bootable {

	private static final String ITER_PROP_NAME = "pingpong.nIterations";
	private static final int DEFAULT_ITERATIONS = Integer.MAX_VALUE;

	public static void main(String... args) {}
	
	public void startup() {
		Config config = ConfigFactory.load();

		final ActorSystem system = ActorSystem.create("AppD-PoC", config);

		Guice.createInjector(new AbstractModule() {
				@Override
				protected void configure() {
					bind(ActorSystem.class).toInstance(system);
				}
			},
			new PocModule()
		);

		if (config.hasPath("pingpong.peer")) {
			ActorRef sourceActor = system.actorFor("/user/pingpong");
			ActorRef targetActor = system.actorFor(config.getString("pingpong.peer"));

			Probe msg = new Probe(targetActor);

			String iterVal = System.getProperty(ITER_PROP_NAME);
			int nIterations = DEFAULT_ITERATIONS;
			if (iterVal != null && !("".equals(iterVal))) {
				try {
					nIterations = Integer.parseInt(iterVal);
				} catch (NumberFormatException nfe) {
					System.out.println("Non-numeric value for " + ITER_PROP_NAME + ", falling back to " + nIterations);
				}
			}

			for (int i = 0; i < nIterations; ++i) {
				sourceActor.tell(msg, null);

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {

                }
			}
		}
	}

	public void shutdown() {
		
	}
}
