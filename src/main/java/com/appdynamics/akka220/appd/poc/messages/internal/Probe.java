package com.appdynamics.akka220.appd.poc.messages.internal;

import java.io.Serializable;

import akka.actor.ActorRef;

public class Probe implements Serializable {
	
	private ActorRef target;

	public Probe(ActorRef target) {
		this.target = target;
	}

	public ActorRef getTarget() {
		return target;
	}

	public void setTarget(ActorRef target) {
		this.target = target;
	}
}
