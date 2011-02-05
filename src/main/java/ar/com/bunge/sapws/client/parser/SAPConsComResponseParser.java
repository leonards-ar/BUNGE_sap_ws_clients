/*
 * File name: SAPConsComResponseParser.java
 * Creation date: 05/02/2011 07:58:33
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
public class SAPConsComResponseParser extends SAPBaseResponseParser {
	private static final Logger LOG = Logger.getLogger(SAPConsComResponseParser.class);
	private static final String RESULT_ROOT_TAG = "XXX";
	private static final String RECORD_TYPE_TAG = "tiporeg";
	/**
	 * 
	 */
	public SAPConsComResponseParser() {
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
		Document resp = getXmlDocumentFromString(getNodeText(doc, RESULT_ROOT_TAG));
		
		
		if (resp != null && resp.getChildNodes() != null) {
			StringBuilder sb = new StringBuilder();
			Node n;
			String recType;
			sb.append(buildSuccessLine());
			
			for (int i = 0; i < resp.getChildNodes().getLength(); i++) {
				n = resp.getChildNodes().item(i);
				recType = getNodeText(n, RECORD_TYPE_TAG);
				if("01".equals(recType)) {
					sb.append(buildRecordType1Line(recType, n));
				} else if("02".equals(recType)) {
					sb.append(buildRecordType2Line(recType, n));
				} else {
					String unknownRecordTypeLine = handleUnknownRecordType(recType, n);
					if(unknownRecordTypeLine != null) {
						sb.append(unknownRecordTypeLine);
					}
				}
			}
			
			return sb.toString();
		} else {
			LOG.warn("No node " + RESULT_ROOT_TAG + " found or node has no children. Returning [" + ERROR_STATUS + "]");
			return buildErrorLine("No node " + RESULT_ROOT_TAG + " found or node has no children");
		}
	}

	/**
	 * 
	 * @param recordType
	 * @param type1
	 * @return
	 * @throws Exception
	 */
	private String buildRecordType1Line(String recordType, Node type1) throws Exception {
		StringBuilder line = new StringBuilder(recordType + LINE_TOKEN);
		
		line.append(getNodeText(type1, "status") + LINE_TOKEN);
		line.append(getNodeText(type1, "descripcion-error") + LINE_TOKEN);
		line.append(getNodeText(type1, "status-anu") + LINE_TOKEN);
		line.append(getNodeText(type1, "saldo-monloc") + LINE_TOKEN);
		line.append(getNodeText(type1, "saldo-monfue") + LINE_TOKEN);
		line.append(getNodeText(type1, "cancel-monloc") + LINE_TOKEN);
		line.append(getNodeText(type1, "cancel-monfue") + LINE_TOKEN);
		line.append(getNodeText(type1, "encanje") + LINE_TOKEN);
		line.append(getNodeText(type1, "enpropuesta") + LINE_TOKEN);
		line.append(getNodeText(type1, "cancel-op") + LINE_TOKEN);
		line.append(getNodeText(type1, "fecha-op") + LINE_TOKEN);
		line.append(getNodeText(type1, "contrato") + LINE_TOKEN);
		line.append(getNodeText(type1, "texto-comodin-01"));
		
		line.append(LINE_SEPARATOR);
		
		return line.toString();
	}
	
	/**
	 * 
	 * @param recordType
	 * @param type2
	 * @return
	 * @throws Exception
	 */
	private String buildRecordType2Line(String recordType, Node type2) throws Exception {
		StringBuilder line = new StringBuilder(recordType + LINE_TOKEN);
		
		line.append(getNodeText(type2, "division") + LINE_TOKEN);
		line.append(getNodeText(type2, "fecha-mov") + LINE_TOKEN);
		line.append(getNodeText(type2, "prefijo-mov") + LINE_TOKEN);
		line.append(getNodeText(type2, "numero-mov") + LINE_TOKEN);
		line.append(getNodeText(type2, "ndp-mov") + LINE_TOKEN);
		line.append(getNodeText(type2, "contrato-mov") + LINE_TOKEN);
		line.append(getNodeText(type2, "cveccm-mov") + LINE_TOKEN);
		line.append(getNodeText(type2, "impccm-monloc") + LINE_TOKEN);
		line.append(getNodeText(type2, "impccm-monfue") + LINE_TOKEN);
		line.append(getNodeText(type2, "pcaccm-monloc") + LINE_TOKEN);
		line.append(getNodeText(type2, "pcaccm-monfue") + LINE_TOKEN);
		line.append(getNodeText(type2, "moneda") + LINE_TOKEN);
		line.append(getNodeText(type2, "fecha-vencimiento") + LINE_TOKEN);
		line.append(getNodeText(type2, "impapl-monloc") + LINE_TOKEN);
		line.append(getNodeText(type2, "impapl-monfue") + LINE_TOKEN);
		line.append(getNodeText(type2, "fecapl") + LINE_TOKEN);
		line.append(getNodeText(type2, "tipdgi") + LINE_TOKEN);
		line.append(getNodeText(type2, "cladoc") + LINE_TOKEN);
		line.append(getNodeText(type2, "texto-comodin-02"));
		
		line.append(LINE_SEPARATOR);
		
		return line.toString();
	}	
	
	/**
	 * 
	 * @param recordType
	 * @param type2
	 * @return
	 * @throws Exception
	 */
	private String handleUnknownRecordType(String recordType, Node type2) throws Exception {
		LOG.warn("Unexpected value " + recordType + " for " + RECORD_TYPE_TAG + ". Ignoring record.");
		return null;
	}
}
