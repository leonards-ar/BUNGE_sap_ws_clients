/*
 * File name: SAPComDeuResponseParser.java
 * Creation date: 05/02/2011 08:59:07
 * Copyright Mindpool
 */
package ar.com.bunge.sapws.client.parser;

import java.util.Map;

import org.apache.log4j.Logger;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since 1.0
 *
 */
public class CupoCabeceraResponseParser extends SAPBaseResponseParser {
	private static final Logger LOG = Logger.getLogger(CupoCabeceraResponseParser.class);
	
	/**
	 * 
	 */
	public CupoCabeceraResponseParser() {
	}

	/**
	 * @param rawResponse
	 * @param context
	 * @return
	 * @throws Exception
	 * @see ar.com.bunge.sapws.client.parser.ResponseParser#parseResponse(java.lang.String, java.util.Map)
	 */
	public String parseResponse(String rawResponse, Map<String, Object> context) throws Exception {
		LOG.debug(rawResponse);
		return buildSuccessLine();
	}
}
