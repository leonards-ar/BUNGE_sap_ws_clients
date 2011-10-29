/*
 * File name: TransporteBienesResponseParser.java
 * Creation date: 29/10/2011 09:19:44
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
public class TransporteBienesResponseParser extends BaseResponseParser {
	private static final Logger LOG = Logger.getLogger(TransporteBienesResponseParser.class);

	private static final String AR_BR_RESPONSE_NODE = "TBCOMPROBANTE";
	private static final String CR_RESPONSE_NODE = "TBError";
	private static final String BR_ERRORES_NODE = "errores";
	private static final String BR_ERROR_NODE = "error";
	
	private static final String REMITOS_CONTAINER_NODE = "validacionesRemitos";
	private static final String REMITO_NODE = "remito";
	
	
	private static final String AR_RESULT = "AR";
	private static final String BR_RESULT = "BR";
	private static final String CR_RESULT = "CR";
	
	private static final String AR_BR_HEADER_NODES[] = {"cuitEmpresa", "numeroComprobante", "nombreArchivo", "codigoIntegridad"};
	private static final String AR_BR_DETAIL_NODES[] = {"numeroUnico", "procesado"};
	private static final String CR_ERROR_NODES[] = {"tipoError", "codigoError", "mensajeError"};
	
	// Tokens
	private static final String RESULT_MESSAGE_SEPARATOR = FileUtils.getNewLine();

	
	/**
	 * 
	 */
	public TransporteBienesResponseParser() {
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

		if(isAR(doc, context)) {
			return parseArResponse(doc, context);
		} else if(isBR(doc, context)) {
			return parseBrResponse(doc, context);
		} else if(isCR(doc, context)) {
			return parseCrResponse(doc, context);
		} else {
			LOG.warn("Input is not AR, BR or CR XML. Returning original XML");
			return rawResponse;
		}
	}
	
	private String parseArResponse(Document doc, Map<String, Object> context) throws Exception {
		StringBuffer response = new StringBuffer(AR_RESULT + RESULT_MESSAGE_SEPARATOR);
		Node responseNode = getNode(doc, AR_BR_RESPONSE_NODE);

		String value;
		for(int i=0; i < AR_BR_HEADER_NODES.length; i++) {
			value = getNodeText(responseNode, AR_BR_HEADER_NODES[i]);
			if(value != null) {
				response.append(value.trim());
			}
			// Jose wants a ; at the end of each row
			if(i < AR_BR_HEADER_NODES.length) {
				response.append(LINE_TOKEN);
			}
		}
		
		response.append(RESULT_MESSAGE_SEPARATOR);
		
		Node remitos = getNode(responseNode, REMITOS_CONTAINER_NODE);
		if (remitos != null && remitos.getChildNodes() != null) {
			Node n;
			for (int i = 0; i < remitos.getChildNodes().getLength(); i++) {
				n = remitos.getChildNodes().item(i);
				if (REMITO_NODE.equalsIgnoreCase(n.getNodeName())) {
					response.append(parseRemitoLine(n));
					response.append(RESULT_MESSAGE_SEPARATOR);
				}
			}
		}
		
		return response.toString();
	}

	private String parseRemitoLine(Node remitoNode) throws Exception {
		StringBuffer response = new StringBuffer();
		String value;
		for(int i=0; i < AR_BR_DETAIL_NODES.length; i++) {
			value = getNodeText(remitoNode, AR_BR_DETAIL_NODES[i]);
			if(value != null) {
				response.append(value.trim());
			}
			// Jose wants a ; at the end of each row
			if(i < AR_BR_DETAIL_NODES.length) {
				response.append(LINE_TOKEN);
			}
		}
		return response.toString();
	}
	
	private String parseBrResponse(Document doc, Map<String, Object> context) throws Exception {
		StringBuffer response = new StringBuffer(BR_RESULT + RESULT_MESSAGE_SEPARATOR);
		Node responseNode = getNode(doc, AR_BR_RESPONSE_NODE);

		String value;
		for(int i=0; i < AR_BR_HEADER_NODES.length; i++) {
			value = getNodeText(responseNode, AR_BR_HEADER_NODES[i]);
			if(value != null) {
				response.append(value.trim());
			}
			// Jose wants a ; at the end of each row
			if(i < AR_BR_HEADER_NODES.length) {
				response.append(LINE_TOKEN);
			}
		}
		
		response.append(RESULT_MESSAGE_SEPARATOR);
		
		Node remitos = getNode(responseNode, REMITOS_CONTAINER_NODE);
		if (remitos != null && remitos.getChildNodes() != null) {
			Node n;
			for (int i = 0; i < remitos.getChildNodes().getLength(); i++) {
				n = remitos.getChildNodes().item(i);
				if (REMITO_NODE.equalsIgnoreCase(n.getNodeName())) {
					response.append(parseRemitoLine(n));
					response.append(RESULT_MESSAGE_SEPARATOR);
					response.append(parseRemitoErroresLines(getNode(n, BR_ERRORES_NODE)));
				}
			}
		}
		
		return response.toString();		
	}

	private String parseRemitoErroresLines(Node remitoErroresNode) throws Exception {
		StringBuffer response = new StringBuffer();

		if (remitoErroresNode != null && remitoErroresNode.getChildNodes() != null) {
			Node n;
			for (int i = 0; i < remitoErroresNode.getChildNodes().getLength(); i++) {
				n = remitoErroresNode.getChildNodes().item(i);
				if(BR_ERROR_NODE.equalsIgnoreCase(n.getNodeName())) {
					response.append("ERROR" + LINE_TOKEN + getNodeText(n, "codigo") + LINE_TOKEN + getNodeText(n, "descripcion") + LINE_TOKEN);
					response.append(RESULT_MESSAGE_SEPARATOR);
				}
			}
		}
		return response.toString();
	}
	
	private String parseCrResponse(Document doc, Map<String, Object> context) throws Exception {
		StringBuffer response = new StringBuffer(CR_RESULT + RESULT_MESSAGE_SEPARATOR);
		Node responseNode = getNode(doc, CR_RESPONSE_NODE);

		String value;
		for(int i=0; i < CR_ERROR_NODES.length; i++) {
			value = getNodeText(responseNode, CR_ERROR_NODES[i]);
			if(value != null) {
				response.append(value.trim());
			}
			// Jose wants a ; at the end of each row
			if(i < CR_ERROR_NODES.length) {
				response.append(LINE_TOKEN);
			}
		}
		return response.toString();
	}

	private boolean isAR(Document doc, Map<String, Object> context) throws Exception {
		Node responseNode = getNode(doc, AR_BR_RESPONSE_NODE); 
		
		return responseNode != null && !isBR(doc, context);
	}
	
	private boolean isBR(Document doc, Map<String, Object> context) throws Exception {
		Node responseNode = getNode(doc, AR_BR_RESPONSE_NODE); 
		Node errors = getNode(responseNode, BR_ERRORES_NODE);
		
		return responseNode != null && errors != null;
	}
	
	private boolean isCR(Document doc, Map<String, Object> context) throws Exception {
		Node responseNode = getNode(doc, CR_RESPONSE_NODE); 
		
		return responseNode != null;
	}	
}
