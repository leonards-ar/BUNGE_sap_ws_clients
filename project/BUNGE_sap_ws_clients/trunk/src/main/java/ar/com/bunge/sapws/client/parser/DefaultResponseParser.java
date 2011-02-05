/*
 * File name: DefaultResponseParser.java
 * Creation date: 19/06/2010 19:04:09
 * Copyright Mindpool
 */
package ar.com.bunge.sapws.client.parser;

import java.util.Map;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since 1.0
 *
 */
public class DefaultResponseParser implements ResponseParser {

	/**
	 * 
	 */
	public DefaultResponseParser() {
	}

	/**
	 * 
	 * @param rawResponse
	 * @param context
	 * @return
	 * @throws Exception
	 * @see ar.com.bunge.sapws.client.parser.ResponseParser#parseResponse(java.lang.String, java.util.Map)
	 */
	public String parseResponse(String rawResponse, Map<String, Object> context) throws Exception {
		return rawResponse;
	}

	/**
	 * 
	 * @param errorNumber
	 * @param message
	 * @param context
	 * @return
	 * @throws Exception
	 * @see ar.com.bunge.sapws.client.parser.ResponseParser#parseError(java.lang.Long, java.lang.String, java.util.Map)
	 */
	public String parseError(Long errorNumber, String message, Map<String, Object> context) throws Exception {
		return null;
	}
}
