/*
 * File name: SAPClientXmlResponse.java
 * Creation date: Jul 28, 2009 5:15:47 PM
 * Copyright Mindpool
 */
package ar.com.bunge.sapws.client;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since SPM 1.0
 *
 */
public class SAPClientXmlResponse {
	private String response;
	
	/**
	 * 
	 */
	public SAPClientXmlResponse() {
		this(null);
	}

	/**
	 * 
	 * @param response
	 */
	public SAPClientXmlResponse(String response) {
		super();
		setResponse(response);
	}
	
	/**
	 * @return the response
	 */
	public String getResponse() {
		return response;
	}

	/**
	 * @param response the response to set
	 */
	public void setResponse(String response) {
		this.response = response;
	}

}
