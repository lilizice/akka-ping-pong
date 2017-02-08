package com.appdynamics.akka220.appd.poc;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;

import com.appdynamics.akka220.appd.poc.actors.PingPongActor;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.name.Names;

public class PocModule extends AbstractModule {
	
	@Override
	protected void configure() {
		bind(ActorRef.class).annotatedWith(Names.named("pingpong"))
			.toProvider(new Provider<ActorRef>(){
				@Inject
				private Injector injector;
				
				@Inject
				private ActorSystem system;

				public ActorRef get() {
					return system.actorOf(Props.create(PingPongActor.class), "pingpong");
				}
			})
			.asEagerSingleton();
	}

}
