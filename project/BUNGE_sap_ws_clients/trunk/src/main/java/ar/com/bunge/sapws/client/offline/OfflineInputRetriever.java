/*
 * File name: OfflineInputRetriever.java
 * Creation date: 29/10/2011 08:27:59
 * Copyright Mindpool
 */
package ar.com.bunge.sapws.client.offline;

import java.util.Map;

import ar.com.bunge.sapws.client.ClientXmlResponse;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since 1.0
 *
 */
public interface OfflineInputRetriever {

	ClientXmlResponse retrieveInput(Map<String, Object> context) throws Exception;
	
	boolean changeResponseFile();
	
	String getResponseFile(String cmdLineResponseFile, Map<String, Object> context) throws Exception;
}
