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
public class ZPamr1Sce018ResponseParser extends SAPBaseResponseParser {
	private static final Logger LOG = Logger.getLogger(ZPamr1Sce018ResponseParser.class);
	private static final String RESULT_ROOT_TAG = "n0:ZPamr1Sce018Response";
	private static final String RESULT_TYPE0_ROOT_TAG = "EsCuposSap";
	private static final String RESULT_TYPE1_ROOT_TAG = "TBultosSap";
	private static final String RESULT_TYPE2_ROOT_TAG = "TPresentacionesSap";
	private static final String RESULT_TYPE3_ROOT_TAG = "TProductosCobol";
	private static final String RESULT_TYPE4_ROOT_TAG = "TProductosSap";
	private static final String RESULT_ITEM_TAG = "item";
	
	/**
	 * 
	 */
	public ZPamr1Sce018ResponseParser() {
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
		Node resp = getNode(doc, RESULT_ROOT_TAG);
		
		
		if (resp != null && resp.getChildNodes() != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(buildSuccessLine());

			Node n;

			Node type0 = getNode(resp, RESULT_TYPE0_ROOT_TAG);
			if(type0 != null) {
				sb.append(buildRecordType0Line(type0));
			}
			
			Node type1 = getNode(resp, RESULT_TYPE1_ROOT_TAG);
			if(type1 != null) {
				for (int i = 0; i < type1.getChildNodes().getLength(); i++) {
					n = type1.getChildNodes().item(i);
					if(RESULT_ITEM_TAG.equals(n.getNodeName())) {
						sb.append(buildRecordType1Line(n));
					}
				}
			}

			Node type2 = getNode(resp, RESULT_TYPE2_ROOT_TAG);
			if(type2 != null) {
				for (int i = 0; i < type2.getChildNodes().getLength(); i++) {
					n = type2.getChildNodes().item(i);
					if(RESULT_ITEM_TAG.equals(n.getNodeName())) {
						sb.append(buildRecordType2Line(n));
					}
				}
			}

			Node type3 = getNode(resp, RESULT_TYPE3_ROOT_TAG);
			if(type3 != null) {
				for (int i = 0; i < type3.getChildNodes().getLength(); i++) {
					n = type3.getChildNodes().item(i);
					if(RESULT_ITEM_TAG.equals(n.getNodeName())) {
						sb.append(buildRecordType3Line(n));
					}
				}
			}

			Node type4 = getNode(resp, RESULT_TYPE4_ROOT_TAG);
			if(type4 != null) {
				for (int i = 0; i < type4.getChildNodes().getLength(); i++) {
					n = type4.getChildNodes().item(i);
					if(RESULT_ITEM_TAG.equals(n.getNodeName())) {
						sb.append(buildRecordType4Line(n));
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
	 * @param type0
	 * @return
	 * @throws Exception
	 */
	private String buildRecordType0Line(Node type0) throws Exception {
		StringBuilder line = new StringBuilder();
		line.append(RESULT_TYPE0_ROOT_TAG + LINE_TOKEN);

		line.append(getNodeText(type0, "PlantaOrigen") + LINE_TOKEN);
		line.append(getNodeText(type0, "AlmacenOrigen") + LINE_TOKEN);
		line.append(getNodeText(type0, "PlantaDestino") + LINE_TOKEN);
		line.append(getNodeText(type0, "AlmacenDestino") + LINE_TOKEN);
		line.append(getNodeText(type0, "ClienteCodigo") + LINE_TOKEN);
		line.append(getNodeText(type0, "ClienteRazonSoc") + LINE_TOKEN);
		line.append(getNodeText(type0, "ClienteDomicilio") + LINE_TOKEN);
		line.append(getNodeText(type0, "ClienteCodPostal") + LINE_TOKEN);
		line.append(getNodeText(type0, "ClienteLocalidad") + LINE_TOKEN);
		line.append(getNodeText(type0, "ClientePcia") + LINE_TOKEN);
		line.append(getNodeText(type0, "ClientePais") + LINE_TOKEN);
		line.append(getNodeText(type0, "ClienteCondIva") + LINE_TOKEN);
		line.append(getNodeText(type0, "ClienteIibb") + LINE_TOKEN);
		
		line.append(LINE_SEPARATOR);
		
		return line.toString();
	}

	/**
	 * 
	 * @param type1
	 * @return
	 * @throws Exception
	 */
	private String buildRecordType1Line(Node type1) throws Exception {
		StringBuilder line = new StringBuilder();
		line.append(RESULT_TYPE1_ROOT_TAG + LINE_TOKEN);

		line.append(getNodeText(type1, "BultoCodigo") + LINE_TOKEN);
		line.append(getNodeText(type1, "BultoDescri") + LINE_TOKEN);
		line.append(getNodeText(type1, "BultoCapacidad") + LINE_TOKEN);
		line.append(getNodeText(type1, "BultoUniMedida") + LINE_TOKEN);
		line.append(getNodeText(type1, "BultoPeso") + LINE_TOKEN);
		line.append(getNodeText(type1, "BultoPesoUniMedida") + LINE_TOKEN);
		
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
		line.append(RESULT_TYPE2_ROOT_TAG + LINE_TOKEN);

		line.append(getNodeText(type2, "PresentacionCodigo") + LINE_TOKEN);
		line.append(getNodeText(type2, "PresentacionDescri") + LINE_TOKEN);
		line.append(getNodeText(type2, "PresentacionBultoCodigo") + LINE_TOKEN);
		line.append(getNodeText(type2, "PresentacionNivel") + LINE_TOKEN);
		line.append(getNodeText(type2, "PresentacionBultoCantidad") + LINE_TOKEN);

		line.append(LINE_SEPARATOR);
		
		return line.toString();
	}	
	
	/**
	 * 
	 * @param type3
	 * @return
	 * @throws Exception
	 */
	private String buildRecordType3Line(Node type3) throws Exception {
		StringBuilder line = new StringBuilder();
		line.append(RESULT_TYPE3_ROOT_TAG + LINE_TOKEN);

		line.append(getNodeText(type3, "ProductoCodigo") + LINE_TOKEN);
		line.append(getNodeText(type3, "ProductoTercero") + LINE_TOKEN);

		line.append(LINE_SEPARATOR);
		
		return line.toString();
	}
	
	/**
	 * 
	 * @param type4
	 * @return
	 * @throws Exception
	 */
	private String buildRecordType4Line(Node type4) throws Exception {
		StringBuilder line = new StringBuilder();
		line.append(RESULT_TYPE4_ROOT_TAG + LINE_TOKEN);

		line.append(getNodeText(type4, "ProductoCodigoCobol") + LINE_TOKEN);
		line.append(getNodeText(type4, "ProductoTerceroCobol") + LINE_TOKEN);
		line.append(getNodeText(type4, "ProductoCodigo") + LINE_TOKEN);
		line.append(getNodeText(type4, "ProductoClaseVal") + LINE_TOKEN);
		line.append(getNodeText(type4, "ProductoDescripcion") + LINE_TOKEN);
		line.append(getNodeText(type4, "ProductoCodigoTipo") + LINE_TOKEN);
		line.append(getNodeText(type4, "ProductoUnidadMedida") + LINE_TOKEN);
		line.append(getNodeText(type4, "ProductoEstado") + LINE_TOKEN);
		line.append(getNodeText(type4, "ProductoPuroMezcla") + LINE_TOKEN);
		line.append(getNodeText(type4, "ProductoMatPeligroso") + LINE_TOKEN);
		line.append(getNodeText(type4, "ProductoStokeable") + LINE_TOKEN);
		line.append(getNodeText(type4, "ProductoNitrogeno") + LINE_TOKEN);
		line.append(getNodeText(type4, "ProductoFosforo") + LINE_TOKEN);
		line.append(getNodeText(type4, "ProductoPotasio") + LINE_TOKEN);
		line.append(getNodeText(type4, "ProductoAzufre") + LINE_TOKEN);
		line.append(getNodeText(type4, "ProductoCalcio") + LINE_TOKEN);
		line.append(getNodeText(type4, "ProductoMagnesio") + LINE_TOKEN);
		line.append(getNodeText(type4, "ProductoReqPrecinto") + LINE_TOKEN);
		line.append(getNodeText(type4, "ProductoReqTapas") + LINE_TOKEN);
		line.append(getNodeText(type4, "ProductoReqDensidad") + LINE_TOKEN);
		line.append(getNodeText(type4, "ProductoImpCasillaSeg") + LINE_TOKEN);
		line.append(getNodeText(type4, "ProductoChklst") + LINE_TOKEN);
		line.append(getNodeText(type4, "ProductoCtrolSat") + LINE_TOKEN);
		line.append(getNodeText(type4, "ProductoRelRucca") + LINE_TOKEN);
		line.append(getNodeText(type4, "ProductoCodUnicoNomenclador") + LINE_TOKEN);
		line.append(getNodeText(type4, "ProductoCodLeyenda") + LINE_TOKEN);
		line.append(getNodeText(type4, "ProductoMermaCalidad") + LINE_TOKEN);
		line.append(getNodeText(type4, "ProductoConsumeCapPlanta") + LINE_TOKEN);
		line.append(getNodeText(type4, "ProductoCodigoArba") + LINE_TOKEN);
		line.append(getNodeText(type4, "ProductoEsEnvase") + LINE_TOKEN);
		line.append(getNodeText(type4, "ProductoCodigoPresentacion") + LINE_TOKEN);

		line.append(LINE_SEPARATOR);
		
		return line.toString();
	}			
	
	public static void main(String a[]) {
		try {
			ZPamr1Sce018ResponseParser r = new ZPamr1Sce018ResponseParser();
			System.out.println(r.parseResponse(FileUtils.readFile("C:\\a.xml"), null));
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}	
}
