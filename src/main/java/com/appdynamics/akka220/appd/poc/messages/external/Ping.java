package com.appdynamics.akka220.appd.poc.messages.external;

import java.io.Serializable;

public class Ping implements Serializable {
	
	private String requestId;

	public Ping() {
	}
	
	public Ping(String requestId) {
		this.requestId = requestId;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
}
