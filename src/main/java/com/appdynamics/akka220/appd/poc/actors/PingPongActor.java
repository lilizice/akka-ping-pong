package com.appdynamics.akka220.appd.poc.actors;

import java.util.UUID;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.appdynamics.akka220.appd.poc.messages.external.Ping;
import com.appdynamics.akka220.appd.poc.messages.external.Pong;
import com.appdynamics.akka220.appd.poc.messages.internal.Probe;


public class PingPongActor extends UntypedActor {
	
	private LoggingAdapter log = Logging.getLogger(context().system(), this);
	

	@Override
	public void onReceive(Object m) throws Exception {
		if (m instanceof Probe) {
			handleProbe(m);
		} else if (m instanceof Ping) {
			handlePing(m);
		} else if (m instanceof Pong) {
			handlePong(m);
		}
		
	}
	
	private void handleProbe(Object m)
	{
		Probe p = (Probe) m;
		Ping ping = new Ping(UUID.randomUUID().toString());
		
		log.debug("Initiating ping to '{}' with request id '{}'", p.getTarget(), ping.getRequestId());
		log.debug("Target class is '{}'", p.getTarget().getClass().getName());
		
		p.getTarget().tell(ping, self());
	}
	
	private void handlePing(Object m)
	{
		log.debug("Received a ping from '{}', request '{}'", sender(), ((Ping) m).getRequestId());
		System.out.println("Received a ping from " +  sender() + ", request " +  ((Ping) m).getRequestId());
		sender().tell(new Pong(((Ping) m).getRequestId()), self());
	}
	
	private void handlePong(Object m)
	{
		log.debug("Received a pong from '{}' for request '{}'", sender(), ((Pong) m).getRequestId());
		System.out.println("Received a pong from " +  sender() + ", request " +  ((Pong) m).getRequestId());
	}
}
