/*
 * File name: InterfacturaWSFacturaResponseParser.java
 * Creation date: 03/06/2010 21:10:15
 * Copyright Mindpool
 */
package ar.com.bunge.sapws.client.parser;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since SPM 1.0
 *
 */
public class InterfacturaWSFacturaResponseParser implements ResponseParser {
	private static final Logger LOG = Logger.getLogger(InterfacturaWSFacturaResponseParser.class);
	
	// Receive Factura keys
	private static final String RECEIVE_FACTURAS_NODE = "m:receiveFacturasOutput";
	private static final String GET_LOTES_FACTURAS_NODE = "m:getLoteFacturasOutput";
	
	private static final String RECEIVE_FACTURAS_STATUS_NODE = "estado";
	private static final String SUCCESS_STATUS = "OK";
	private static final String CERROR_STATUS = "EC";
	private static final String LERROR_STATUS = "EL";
	
	private static final String RECEIVE_FACTURAS_CERRORS_NODE = "errores_comprobante";
	private static final String RECEIVE_FACTURAS_LERRORS_NODE = "errores_lote";
	private static final String RECEIVE_FACTURAS_SUCCESS_RESULT = "A";
	private static final String RECEIVE_FACTURAS_RESULT_NODE = "resultado";

	private static final String GET_LOTES_FACTURAS_ERRORS_NODE = "errores_consulta";
	private static final String GET_LOTES_FACTURAS_INFO_NODE = "informacion_comprobante";
	private static final String GET_LOTES_FACTURAS_RESULT_NODE = "resultado";
	private static final String GET_LOTES_FACTURAS_SUCCESS_RESULT = "A";
	private static final String GET_LOTES_FACTURAS_CAE_NODE = "cae";
	private static final String GET_LOTES_FACTURAS_EXP_CAE_DATE_NODE = "fecha_vencimiento_cae";
	private static final String GET_LOTES_FACTURAS_GET_CAE_DATE_NODE = "fecha_obtencion_cae";
	
	// Common error nodes
	private static final String ERROR_NODE = "error";
	private static final String ERROR_CODE_NODE = "codigo_error";
	private static final String ERROR_MESSAGE_NODE = "descripcion_error";
	private static final String REASON_NODE = "motivo";
	
	// Result codes
	private static final String NO_STATUS_RESULT = "UNKNOWN";
	private static final String SUCCESS_RESULT = "OK";
	private static final String ERROR_RESULT = "ERROR";
	private static final String PARTIAL_SUCCESS_RESULT = "OERROR";
	// Tokens
	private static final String ERROR_MESSAGE_SEPARATOR = "\n";
	private static final String ERROR_MESSAGE_TOKEN = "=";
	private static final String SUCCESS_MESSAGE_SEPARATOR = ";";
	
	/**
	 * 
	 */
	public InterfacturaWSFacturaResponseParser() {
	}

	/**
	 * @param rawResponse
	 * @return
	 * @throws Exception
	 * @see ar.com.bunge.sapws.client.parser.ResponseParser#parseResponse(java.lang.String)
	 */
	public String parseResponse(String rawResponse) throws Exception {
		Document doc = getXmlDocumentFromString(rawResponse);

		Node responseNode = getNode(doc, RECEIVE_FACTURAS_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing " + RECEIVE_FACTURAS_NODE + " response");
			return parseReceiveFacturas(responseNode);
		}
		
		responseNode = getNode(doc, GET_LOTES_FACTURAS_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing " + GET_LOTES_FACTURAS_NODE + " response");
			return parseGetLotesFacturas(responseNode);
		}
		
		return rawResponse;
	}

	/**
	 * 
	 * @param responseNode
	 * @return
	 * @throws Exception
	 */
	private String parseGetLotesFacturas(Node responseNode) throws Exception {
		Document response = getXmlDocumentFromString(responseNode.getTextContent());
		
		Node errors = getNode(response, GET_LOTES_FACTURAS_ERRORS_NODE);
		if(errors != null) {
			String errorResult = buildErrorResult(errors);
			LOG.debug("Found errors node [" + GET_LOTES_FACTURAS_ERRORS_NODE + "]. Returning [" + errorResult + "]");
			return errorResult;
		}
		
		
		Node info = getNode(response, GET_LOTES_FACTURAS_INFO_NODE);
		if(info != null) {
			String result = getNodeText(response, GET_LOTES_FACTURAS_RESULT_NODE);
			if(GET_LOTES_FACTURAS_SUCCESS_RESULT.equalsIgnoreCase(result)) {
				String successResult = buildGetLotesFacturasSuccessResult(info);
				LOG.debug("Found node [" + GET_LOTES_FACTURAS_INFO_NODE + "] and result node [" + GET_LOTES_FACTURAS_RESULT_NODE + "] has value [" + result + "]. Returning [" + successResult + "]");
				return successResult;
			} else {
				String errorResult = buildStatusErrorResult(response);
				LOG.debug("Found node [" + GET_LOTES_FACTURAS_INFO_NODE + "] and result node [" + GET_LOTES_FACTURAS_RESULT_NODE + "] has value [" + result + "]. Returning [" + errorResult + "]");
				return errorResult;
			}
		}

		LOG.warn("No node " + GET_LOTES_FACTURAS_INFO_NODE + " or " + GET_LOTES_FACTURAS_ERRORS_NODE + " found. Returning [" + NO_STATUS_RESULT + "]");

		return NO_STATUS_RESULT;
	}

	/**
	 * 
	 * @param responseNode
	 * @return
	 * @throws Exception
	 */
	private String parseReceiveFacturas(Node responseNode) throws Exception {
		Document response = getXmlDocumentFromString(responseNode.getTextContent());
		
		Node status = getNode(response, RECEIVE_FACTURAS_STATUS_NODE);
		if(status != null) {
			String statusText = status.getTextContent();
			LOG.debug("Status node " + RECEIVE_FACTURAS_STATUS_NODE + " found. Value is: [" + statusText + "]");
			
			if(SUCCESS_STATUS.equalsIgnoreCase(statusText)) {
				String result = getNodeText(response, RECEIVE_FACTURAS_RESULT_NODE);
				if(result == null || RECEIVE_FACTURAS_SUCCESS_RESULT.equalsIgnoreCase(result)) {
					LOG.debug("Result status [" + statusText + "] matches success text [" + SUCCESS_STATUS + "] and result node [" + RECEIVE_FACTURAS_RESULT_NODE + "] has value [" + result + "]. Returning [" + SUCCESS_RESULT + "]");
					return SUCCESS_RESULT;
				} else {
					String errorResult = buildStatusErrorResult(response);
					LOG.debug("Found node [" + GET_LOTES_FACTURAS_INFO_NODE + "] and result node [" + GET_LOTES_FACTURAS_RESULT_NODE + "] has value [" + result + "]. Returning [" + errorResult + "]");
					return errorResult;
				}
			} else if(CERROR_STATUS.equalsIgnoreCase(statusText)) {
				String errorResult = buildErrorResult(getNode(response, RECEIVE_FACTURAS_CERRORS_NODE));
				LOG.debug("Result status [" + statusText + "] does not match success text [" + SUCCESS_STATUS + "]. Returning [" + errorResult + "]");
				return errorResult;
			} else if(LERROR_STATUS.equalsIgnoreCase(statusText)) {
				String errorResult = buildErrorResult(getNode(response, RECEIVE_FACTURAS_LERRORS_NODE));
				LOG.debug("Result status [" + statusText + "] does not match success text [" + SUCCESS_STATUS + "]. Returning [" + errorResult + "]");
				return errorResult;
			} else {
				LOG.warn("Result status [" + statusText + "] does not match expected status [" + SUCCESS_STATUS + ", " + CERROR_STATUS + ", " + LERROR_STATUS + "]. Returning [" + ERROR_RESULT + "]");
				return ERROR_RESULT;
			}
		} else {
			LOG.warn("No node " + RECEIVE_FACTURAS_STATUS_NODE + " found. Returning [" + NO_STATUS_RESULT + "]");
			return NO_STATUS_RESULT;
		}
	}

	/**
	 * 
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private String buildErrorResult(Node errors) throws Exception {
		StringBuffer errorResult = new StringBuffer(ERROR_RESULT);
		
		if (errors != null && errors.getChildNodes() != null) {
			Node n;
			for (int i = 0; i < errors.getChildNodes().getLength(); i++) {
				n = errors.getChildNodes().item(i);
				if (ERROR_NODE.equalsIgnoreCase(n.getNodeName())) {
					errorResult.append(ERROR_MESSAGE_SEPARATOR + getNodeText(n, ERROR_CODE_NODE) + ERROR_MESSAGE_TOKEN + getNodeText(n, ERROR_MESSAGE_NODE));
				}
			}
		}
		
		return errorResult.toString();
	}
	
	

	/**
	 * 
	 * @param info
	 * @return
	 * @throws Exception
	 */
	private String buildStatusErrorResult(Node node) throws Exception {
		StringBuffer result = new StringBuffer(PARTIAL_SUCCESS_RESULT);
		
		if (node != null) {
			Node n = getNode(node, REASON_NODE);
			result.append(n != null ? ERROR_MESSAGE_SEPARATOR + n.getTextContent() : "");
			
		}
		
		return result.toString();
	}	
	
	/**
	 * 
	 * @param info
	 * @return
	 * @throws Exception
	 */
	private String buildGetLotesFacturasSuccessResult(Node info) throws Exception {
		StringBuffer result = new StringBuffer(SUCCESS_RESULT);
		
		if (info != null) {
			result.append(SUCCESS_MESSAGE_SEPARATOR);

			Node n = getNode(info, GET_LOTES_FACTURAS_CAE_NODE);
			result.append(n != null ? n.getTextContent() : "");
			
			result.append(SUCCESS_MESSAGE_SEPARATOR);

			n = getNode(info, GET_LOTES_FACTURAS_EXP_CAE_DATE_NODE);
			result.append(n != null ? n.getTextContent() : "");

			result.append(SUCCESS_MESSAGE_SEPARATOR);

			n = getNode(info, GET_LOTES_FACTURAS_GET_CAE_DATE_NODE);
			result.append(n != null ? n.getTextContent() : "");
			
		}
		
		return result.toString();
	}
	
	/**
	 * 
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	private Document getXmlDocumentFromString(String xml) throws Exception {
		XmlObject xmlObject = XmlObject.Factory.parse(xml);
		
    	Document doc1 = (Document) xmlObject.getDomNode();
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc2 = builder.newDocument();

		Element newRoot = (Element) doc2.importNode(doc1.getDocumentElement(), true);
		doc2.appendChild(newRoot);		
		
		return doc2;
	}
    /**
	 * 
	 * @param parent
	 * @return
	 * @throws Exception
	 */
    private Node getNode(Node node, String nodeName) throws Exception {
    	if(nodeName == null) {
    		return null;
    	} else if(nodeName.equalsIgnoreCase(node.getNodeName())) {
    		return node;
    	} else if(node == null || node.getChildNodes() == null || node.getChildNodes().getLength() <= 0) {
    		return null;
    	} else {
    		for(int i = 0; i < node.getChildNodes().getLength(); i++) {
    			Node n = getNode(node.getChildNodes().item(i), nodeName);
    			if(n != null) {
    				return n;
    			}
    		}
    		return null;
    	}
    }	
    
    /**
     * 
     * @param node
     * @param nodeName
     * @return
     * @throws Exception
     */
    private String getNodeText(Node node, String nodeName) throws Exception {
    	Node childNode = getNode(node, nodeName);
    	return childNode != null ? childNode.getTextContent() : null;
    }
}
