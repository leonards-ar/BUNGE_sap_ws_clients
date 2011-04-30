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

import ar.com.bunge.util.FileUtils;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since 1.0
 *
 */
public class SAPComDeuResponseParser extends SAPBaseResponseParser {
	private static final Logger LOG = Logger.getLogger(SAPComDeuResponseParser.class);
	private static final String RESULT_ROOT_TAG = "n0:ZaatFiWsSaldosProvCliResponse";
	private static final String RESULT_TYPE1_ROOT_TAG = "TDetalle_01";
	private static final String RESULT_TYPE2_ROOT_TAG = "TDetalle_02";
	
	/**
	 * 
	 */
	public SAPComDeuResponseParser() {
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
	 * @param recordType
	 * @param type1
	 * @return
	 * @throws Exception
	 */
	private String buildRecordType1Line(Node type1) throws Exception {
		StringBuilder line = new StringBuilder();

		line.append(getNodeText(type1, "TipoReg") + LINE_TOKEN);
		line.append(getNodeText(type1, "StatusWs") + LINE_TOKEN);
		line.append(getNodeText(type1, "DesErrorWs") + LINE_TOKEN);
		line.append(getNodeText(type1, "SaldoNormalL") + LINE_TOKEN);
		line.append(getNodeText(type1, "SaldoNormalF") + LINE_TOKEN);
		line.append(getNodeText(type1, "SaldoVencL") + LINE_TOKEN);
		line.append(getNodeText(type1, "SaldoVencF") + LINE_TOKEN);
		line.append(getNodeText(type1, "SaldoCartL") + LINE_TOKEN);
		line.append(getNodeText(type1, "SaldoCartF") + LINE_TOKEN);
		line.append(getNodeText(type1, "UltimoMov") + LINE_TOKEN);
		line.append(getNodeText(type1, "Contrato") + LINE_TOKEN);
		line.append(getNodeText(type1, "ImporteLimCre") + LINE_TOKEN);
		line.append(getNodeText(type1, "Moneda") + LINE_TOKEN);
		line.append(getNodeText(type1, "Texto"));
		
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
	private String buildRecordType2Line(Node type2) throws Exception {
		StringBuilder line = new StringBuilder();

		line.append(getNodeText(type2, "TipoReg") + LINE_TOKEN);
		line.append(getNodeText(type2, "Division") + LINE_TOKEN);
		line.append(getNodeText(type2, "FechaMov") + LINE_TOKEN);
		line.append(getNodeText(type2, "PrefijoComp") + LINE_TOKEN);
		line.append(getNodeText(type2, "NumComprobante") + LINE_TOKEN);
		line.append(getNodeText(type2, "NotaPedido") + LINE_TOKEN);
		line.append(getNodeText(type2, "Contrato") + LINE_TOKEN);
		line.append(getNodeText(type2, "ClaveCobol") + LINE_TOKEN);
		line.append(getNodeText(type2, "ImporteLocal") + LINE_TOKEN);
		line.append(getNodeText(type2, "ImporteFuerte") + LINE_TOKEN);
		line.append(getNodeText(type2, "ImporteCanL") + LINE_TOKEN);
		line.append(getNodeText(type2, "ImporteCanF") + LINE_TOKEN);
		line.append(getNodeText(type2, "MonedaDoc") + LINE_TOKEN);
		line.append(getNodeText(type2, "FechaVenc") + LINE_TOKEN);
		line.append(getNodeText(type2, "Tipdgi") + LINE_TOKEN);
		line.append(getNodeText(type2, "Blart") + LINE_TOKEN);
		line.append(getNodeText(type2, "Texto"));
		
		line.append(LINE_SEPARATOR);
		
		return line.toString();
	}	
	
	public static void main(String a[]) {
		try {
			SAPComDeuResponseParser r = new SAPComDeuResponseParser();
			System.out.println(r.parseResponse(FileUtils.readFile("C:\\file.txt"), null));
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}	
}
