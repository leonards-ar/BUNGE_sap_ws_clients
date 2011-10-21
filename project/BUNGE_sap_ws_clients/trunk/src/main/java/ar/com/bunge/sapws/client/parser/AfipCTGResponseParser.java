/*
 * File name: DefaultResponseParser.java
 * Creation date: 19/06/2010 19:04:09
 * Copyright Mindpool
 */
package ar.com.bunge.sapws.client.parser;

import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import ar.com.bunge.util.FileUtils;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since 1.0
 *
 */
public class AfipCTGResponseParser extends BaseResponseParser implements ResponseParser {
	private static final Logger LOG = Logger.getLogger(AfipCTGResponseParser.class);
	
	private static final String SUCCESS_RESULT = "OK";
	private static final String ERROR_RESULT = "ERROR";
	
	private static final String SOLICITAR_CTG_NODE = "ns1:solicitarCTGResponse";
	private static final String CONFIRMAR_CTG_NODE = "ns1:confirmarCTGResponse";
	
	// Tokens
	private static final String RESULT_MESSAGE_SEPARATOR = FileUtils.getNewLine();

	/**
	 * 
	 */
	public AfipCTGResponseParser() {
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
		Document doc = getXmlDocumentFromString(rawResponse);
		
		Node responseNode = getNode(doc, SOLICITAR_CTG_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing " + SOLICITAR_CTG_NODE + " response");
			return parseSolicitarCTG(responseNode);
		}
		
		responseNode = getNode(doc, CONFIRMAR_CTG_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing " + CONFIRMAR_CTG_NODE + " response");
			return parseConfirmarCTG(responseNode);
		}
		
		return rawResponse;
	}

	/**
	 * 
	 * @param responseNode
	 * @return
	 * @throws Exception
	 */
	private String parseSolicitarCTG(Node responseNode) throws Exception {
		StringBuffer response = new StringBuffer(SUCCESS_RESULT + RESULT_MESSAGE_SEPARATOR);
		
		String value = getNodeText(responseNode, "numeroCTG");
		if(value != null) {
			response.append(value.trim());
		}
		response.append(SUCCESS_MESSAGE_SEPARATOR);
		
		value = getNodeText(responseNode, "numeroCartaDePorte");
		if(value != null) {
			response.append(value.trim());
		}
		
		return response.toString();
	}

	/**
	 * 
	 * @param responseNode
	 * @return
	 * @throws Exception
	 */
	private String parseConfirmarCTG(Node responseNode) throws Exception {
		StringBuffer response = new StringBuffer(SUCCESS_RESULT + RESULT_MESSAGE_SEPARATOR);
		
		String value = getNodeText(responseNode, "codigoTransaccion");
		if(value != null) {
			response.append(value.trim());
		}
		response.append(SUCCESS_MESSAGE_SEPARATOR);
		
		value = getNodeText(responseNode, "observaciones");
		if(value != null) {
			response.append(value.trim());
		}
		
		return response.toString();
	}
	
	/**
	 * @param errorNumber
	 * @param message
	 * @param context
	 * @return
	 * @throws Exception
	 * @see ar.com.bunge.sapws.client.parser.BaseResponseParser#parseError(java.lang.Long, java.lang.String, java.util.Map)
	 */
	public String parseError(Long errorNumber, String message, Map<String, Object> context) throws Exception {
		return ERROR_RESULT + RESULT_MESSAGE_SEPARATOR + message;
	}
	
	public static void main(String a[]) {
		try {
			AfipCTGResponseParser r = new AfipCTGResponseParser();
			System.out.println(r.parseResponse(FileUtils.readFile("C:\\file.txt"), null));
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
