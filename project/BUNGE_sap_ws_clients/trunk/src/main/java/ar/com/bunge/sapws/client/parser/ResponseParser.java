/*
 * File name: ResponseParser.java
 * Creation date: 03/06/2010 21:08:03
 * Copyright Mindpool
 */
package ar.com.bunge.sapws.client.parser;

import java.util.Map;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since 1.0
 */
public interface ResponseParser {

	/**
	 * 
	 * @param rawResponse
	 * @param context
	 * @return
	 * @throws Exception
	 */
	String parseResponse(String rawResponse, Map<String, Object> context) throws Exception;

	/**
	 * 
	 * @param errorNumber
	 * @param message
	 * @param context
	 * @return
	 * @throws Exception
	 */
	String parseError(Long errorNumber, String message, Map<String, Object> context) throws Exception;
}
