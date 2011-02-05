/*
 * File name: SAPBaseResponseParser.java
 * Creation date: 05/02/2011 07:55:51
 * Copyright Mindpool
 */
package ar.com.bunge.sapws.client.parser;


/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since 1.0
 *
 */
public abstract class SAPBaseResponseParser extends BaseResponseParser {
	// Tokens
	protected static final String LINE_SEPARATOR = "\n";
	protected static final String LINE_TOKEN = ";";
	protected static final String ERROR_STATUS = "ERROR";
	protected static final String SUCCESS_STATUS = "OK";
	
	/**
	 * 
	 */
	public SAPBaseResponseParser() {
	}
	
	/**
	 * 
	 * @param message
	 * @return
	 */
	protected String buildErrorLine(String message) {
		return ERROR_STATUS + LINE_TOKEN + message + LINE_SEPARATOR;
	}
	
	/**
	 * 
	 * @return
	 */
	protected String buildSuccessLine() {
		return SUCCESS_STATUS + LINE_SEPARATOR;
	}
}
