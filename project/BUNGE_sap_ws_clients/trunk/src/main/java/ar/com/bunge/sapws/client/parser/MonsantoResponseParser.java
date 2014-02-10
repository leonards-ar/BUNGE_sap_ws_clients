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
public class MonsantoResponseParser extends BaseResponseParser {
	private static final Logger LOG = Logger.getLogger(MonsantoResponseParser.class);
	
	private static final String REGISTER_WAYBILL_RESPONSE_NODE = "ns1:registerWaybillResponse";
	private static final String TIPOS_TEST_A_REALIZAR_RESPONSE_NODE = "ns1:typeOfTestResponse";
	private static final String STORE_WEIGHT_RESPONSE_NODE = "ns1:storeWeightResponse ";
	private static final String STORE_TEST_RESULT_RESPONSE_NODE = "ns1:storeTestResultResponse ";
	
	/**
	 * 
	 */
	public MonsantoResponseParser() {
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
		
		Node responseNode = getNode(doc, REGISTER_WAYBILL_RESPONSE_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + REGISTER_WAYBILL_RESPONSE_NODE + "] response");
			StringBuffer response = new StringBuffer();
			response.append(buildResultRow(responseNode, new String [] {"return"}));
			return response.toString();			
		}

		responseNode = getNode(doc, TIPOS_TEST_A_REALIZAR_RESPONSE_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + TIPOS_TEST_A_REALIZAR_RESPONSE_NODE + "] response");
			StringBuffer response = new StringBuffer();
			response.append("OK" + SUCCESS_MESSAGE_SEPARATOR + LINE_SEPARATOR);
			
			Node responseDataNode = getNode(responseNode, "return");
			if (responseDataNode != null && responseDataNode.getChildNodes() != null) {
				Node n;
				for (int i = 0; i < responseDataNode.getChildNodes().getLength(); i++) {
					n = responseDataNode.getChildNodes().item(i);
					if ("testRequests".equalsIgnoreCase(n.getNodeName())) {
						response.append(LINE_SEPARATOR);
						response.append(buildResultRow(n, new String [] {"technologyCode", "technologyDescription", "testType", "sampleCode"}));
					}
				}
			}			
			return response.toString();			
		}
		
		responseNode = getNode(doc, STORE_WEIGHT_RESPONSE_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + STORE_WEIGHT_RESPONSE_NODE + "] response");
			StringBuffer response = new StringBuffer();
			response.append(buildResultRow(responseNode, new String [] {"return"}));
			return response.toString();			
		}

		responseNode = getNode(doc, STORE_TEST_RESULT_RESPONSE_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + STORE_TEST_RESULT_RESPONSE_NODE + "] response");
			StringBuffer response = new StringBuffer();
			response.append(buildResultRow(responseNode, new String [] {"return"}));
			return response.toString();			
		}
		
		LOG.warn("Unexpected response: [" + rawResponse + "]");
		return rawResponse;
	}
}
