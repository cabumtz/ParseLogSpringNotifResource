package com.wico.parselog;

import java.io.Serializable;

public class ParseResult implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5134456254396201982L;
	
	private int status;
	private String body;

	
	/**
	 * @param status
	 * @param body
	 */
	public ParseResult(int status, String body) {
		super();
		this.status = status;
		this.body = body;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

}
