package com.appdynamics.akka220.appd.poc.messages.external;

import java.io.Serializable;

public class Pong implements Serializable {
	
	private String requestId;

	public Pong() {
	}
	
	public Pong(String requestId) {
		this.requestId = requestId;
	}
	
	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
}
