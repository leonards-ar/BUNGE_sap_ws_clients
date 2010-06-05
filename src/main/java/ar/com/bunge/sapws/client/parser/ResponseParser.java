/*
 * File name: ResponseParser.java
 * Creation date: 03/06/2010 21:08:03
 * Copyright Mindpool
 */
package ar.com.bunge.sapws.client.parser;

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
	 * @return
	 * @throws Exception
	 */
	String parseResponse(String rawResponse) throws Exception;
}
