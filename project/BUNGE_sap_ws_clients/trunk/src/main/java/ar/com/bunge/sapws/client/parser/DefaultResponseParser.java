/*
 * File name: DefaultResponseParser.java
 * Creation date: 19/06/2010 19:04:09
 * Copyright Mindpool
 */
package ar.com.bunge.sapws.client.parser;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since SPM 1.0
 *
 */
public class DefaultResponseParser implements ResponseParser {

	/**
	 * 
	 */
	public DefaultResponseParser() {
	}

	/**
	 * @param rawResponse
	 * @return
	 * @throws Exception
	 * @see ar.com.bunge.sapws.client.parser.ResponseParser#parseResponse(java.lang.String)
	 */
	public String parseResponse(String rawResponse) throws Exception {
		return rawResponse;
	}

}
