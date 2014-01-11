/*
 * File name: SAPComDeuResponseParser.java
 * Creation date: 05/02/2011 08:59:07
 * Copyright Mindpool
 */
package ar.com.bunge.sapws.client.parser;

import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since 1.0
 *
 */
public class GenexusResponseParser extends BaseResponseParser {
	private static final Logger LOG = Logger.getLogger(GenexusResponseParser.class);
	
	private static final String ESTADO_TURNO_RESPONSE_NODE = "Interfaz_EstadoTurno.ExecuteResponse";
	
	/**
	 * 
	 */
	public GenexusResponseParser() {
	}

	/**
	 * @param rawResponse
	 * @param context
	 * @return
	 * @throws Exception
	 * @see ar.com.bunge.sapws.client.parser.ResponseParser#parseResponse(java.lang.String, java.util.Map)
	 */
	public String parseResponse(String rawResponse, Map<String, Object> context) throws Exception {
		Document doc = getXmlDocumentFromString(rawResponse);
		
		Node responseNode = getNode(doc, ESTADO_TURNO_RESPONSE_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + ESTADO_TURNO_RESPONSE_NODE + "] response");
			
			StringBuffer response = new StringBuffer();
			String error = getNodeText(responseNode, "Error");
			String status = error != null && "false".equalsIgnoreCase(error) ? "OK" : "ERROR";
			response.append(buildResultRow(status, responseNode, new String [] {"Cuponegocionrocupo", "Turnoid", "Turnoestado", "Error", "Descripcion"}));

			return response.toString();			
		}
		
		LOG.warn("Unexpected response: [" + rawResponse + "]");
		
		return rawResponse;
	}
}
