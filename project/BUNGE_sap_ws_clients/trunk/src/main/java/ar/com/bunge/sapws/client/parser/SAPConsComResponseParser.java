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
	private static final String RESULT_ROOT_TAG = "n0:ZaatFiComprobantesCompResponse";
	private static final String RESULT_TYPE1_ROOT_TAG = "TSalidaOp01";
	private static final String RESULT_TYPE2_ROOT_TAG = "TSalidaOp02";
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
			sb.append(buildSuccessLine());

			Node n;
			Node type1 = getNode(resp, RESULT_TYPE1_ROOT_TAG);
			if(type1 != null) {
				for (int i = 0; i < type1.getChildNodes().getLength(); i++) {
					n = resp.getChildNodes().item(i);
					sb.append(buildRecordType1Line(n));
				}
			}

			Node type2 = getNode(resp, RESULT_TYPE2_ROOT_TAG);
			if(type2 != null) {
				for (int i = 0; i < type2.getChildNodes().getLength(); i++) {
					n = resp.getChildNodes().item(i);
					sb.append(buildRecordType2Line(n));
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
	 * @param type1
	 * @return
	 * @throws Exception
	 */
	private String buildRecordType1Line(Node type1) throws Exception {
		StringBuilder line = new StringBuilder();

		line.append(getNodeText(type1, "Tiporeg") + LINE_TOKEN);
		line.append(getNodeText(type1, "StatusWs") + LINE_TOKEN);
		line.append(getNodeText(type1, "Descripcion") + LINE_TOKEN);
		line.append(getNodeText(type1, "StatusAnul") + LINE_TOKEN);
		line.append(getNodeText(type1, "SaldoLcl") + LINE_TOKEN);
		line.append(getNodeText(type1, "SaldoExt") + LINE_TOKEN);
		line.append(getNodeText(type1, "ImporteCanceladoLcl") + LINE_TOKEN);
		line.append(getNodeText(type1, "ImporteCanceladoExt") + LINE_TOKEN);
		line.append(getNodeText(type1, "OperacionCanje") + LINE_TOKEN);
		line.append(getNodeText(type1, "PropuestaPago") + LINE_TOKEN);
		line.append(getNodeText(type1, "OrdenDePago") + LINE_TOKEN);
		line.append(getNodeText(type1, "fecha-op") + LINE_TOKEN);
		line.append(getNodeText(type1, "FechaOp") + LINE_TOKEN);
		line.append(getNodeText(type1, "NumeroContrato") + LINE_TOKEN);
		line.append(getNodeText(type1, "Texto"));
		
		line.append(LINE_SEPARATOR);
		
		return line.toString();
	}
	
	/**
	 * 
	 * @param type2
	 * @return
	 * @throws Exception
	 */
	private String buildRecordType2Line(Node type2) throws Exception {
		StringBuilder line = new StringBuilder();
	
		line.append(getNodeText(type2, "Tiporeg") + LINE_TOKEN);
		line.append(getNodeText(type2, "Division") + LINE_TOKEN);
		line.append(getNodeText(type2, "FechaMv") + LINE_TOKEN);
		line.append(getNodeText(type2, "PrefijoComprobante") + LINE_TOKEN);
		line.append(getNodeText(type2, "NumeroComprobante") + LINE_TOKEN);
		line.append(getNodeText(type2, "NotaPedido") + LINE_TOKEN);
		line.append(getNodeText(type2, "ContratoGrano") + LINE_TOKEN);
		line.append(getNodeText(type2, "ClaveCobol") + LINE_TOKEN);
		line.append(getNodeText(type2, "ImporteLcl") + LINE_TOKEN);
		line.append(getNodeText(type2, "ImporteExt") + LINE_TOKEN);
		line.append(getNodeText(type2, "ImporteCanceladoLcl") + LINE_TOKEN);
		line.append(getNodeText(type2, "ImporteCanceladoExt") + LINE_TOKEN);
		line.append(getNodeText(type2, "MonedaDocumento") + LINE_TOKEN);
		line.append(getNodeText(type2, "FechaVencimiento") + LINE_TOKEN);
		line.append(getNodeText(type2, "ImporteAplicadoLcl") + LINE_TOKEN);
		line.append(getNodeText(type2, "ImporteAplicadoExt") + LINE_TOKEN);
		line.append(getNodeText(type2, "FechaCompensacion") + LINE_TOKEN);
		line.append(getNodeText(type2, "TipoDocumento") + LINE_TOKEN);
		line.append(getNodeText(type2, "ClaseDocumento") + LINE_TOKEN);
		line.append(getNodeText(type2, "Texto"));
		
		line.append(LINE_SEPARATOR);
		
		return line.toString();
	}	

}
