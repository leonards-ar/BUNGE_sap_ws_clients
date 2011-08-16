/*
 * File name: InterfacturaWSFacturaResponseParser.java
 * Creation date: 03/06/2010 21:10:15
 * Copyright Mindpool
 */
package ar.com.bunge.sapws.client.parser;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import ar.com.bunge.util.FileUtils;
import ar.com.ib.cfe.render.GenerarComprobanteFacade;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since 1.0
 *
 */
public class InterfacturaWSFacturaResponseParser extends BaseResponseParser {
	private static final Logger LOG = Logger.getLogger(InterfacturaWSFacturaResponseParser.class);
	
	// Print PDF
	private static final String SAVE_PDF_PARAMETER = "pdf";
	private static final String PDF_TYPE = "tipo_ejemplar";
	private static final String PDF_FILE_PREFIX = "prefijo_pdf";
	private static final String DEFAULT_PDF_TYPE = "ORIGINAL";
	private static final String LOTE_COMPROBANTES_NODE = "lote_comprobantes";
	private static final String COMPROBANTE_NODE = "comprobante";
	
	// Receive Factura keys
	private static final String RECEIVE_FACTURAS_NODE = "m:receiveFacturasOutput";
	private static final String GET_LOTES_FACTURAS_NODE = "m:getLoteFacturasOutput";
	private static final String SOLICITA_CAEA_NODE = "m:solicitaCaeaOutput";
	private static final String CONSULTA_CAEA_NODE = "m:consultaDetalleCaeaResponse";
	private static final String INFORMA_CAEA_NO_UTILIZADO_NODE = "m:informarCaeaNoUtilizadoResponse";
	
	
	private static final String RECEIVE_FACTURAS_STATUS_NODE = "estado";
	private static final String SUCCESS_STATUS = "OK";
	private static final String CERROR_STATUS = "EC";
	private static final String LERROR_STATUS = "EL";

	private static final String CAEA_ERRORS_NODES[] = {"errores_caea", "errores_response", "errores"};
	
	private static final String SOLICITA_CAEA_RESULT_NODE = "caea_response";
	private static final String SOLICITA_CAEA_SUCCESS_NODES[] = {"caea", "fecha_proceso_caea", "periodo", "orden", "fecha_vigencia_desde", "fecha_vigencia_hasta", "fecha_tope_informar_no_utilizado"};

	private static final String CONSULTA_CAEA_RESULT_NODE = "detalle_caea_response";
	private static final String CONSULTA_CAEA_SUCCESS_NODES[] = {"caea", "fecha_proceso_caea", "periodo", "orden", "fecha_vigencia_desde", "fecha_vigencia_hasta", "fecha_tope_informar_no_utilizado", "punto_de_venta", "estado"};
	
	private static final String INFORMA_CAEA_NO_UTILIZADO_RESULT_NODE = "caea_no_utilizado_por_punto_de_venta_response";
	private static final String INFORMA_CAEA_NO_UTILIZADO_NODES[] = {"caea", "resultado", "fecha_proceso", "punto_de_venta"};

	private static final String RECEIVE_FACTURAS_RERRORS_NODE = "errores_response";
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
	private static final String AFIP_REASON_NODES = "observaciones_respuesta_afip";
	private static final String AFIP_REASON_NODE = "observacion_afip";
	private static final String AFIP_REASON_CODE = "codigo";
	private static final String AFIP_REASON_DESCRIPTION = "descripcion";
	
	// Result codes
	private static final String NO_STATUS_RESULT = "UNKNOWN";
	private static final String SUCCESS_RESULT = "OK";
	private static final String ERROR_RESULT = "ERROR";
	private static final String PARTIAL_SUCCESS_RESULT = "OERROR";
	// Tokens
	private static final String ERROR_MESSAGE_SEPARATOR = FileUtils.getNewLine();
	private static final String ERROR_MESSAGE_TOKEN = "=";
	
	/**
	 * 
	 */
	public InterfacturaWSFacturaResponseParser() {
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

		Node responseNode = getNode(doc, RECEIVE_FACTURAS_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing " + RECEIVE_FACTURAS_NODE + " response");
			return parseReceiveFacturas(responseNode);
		}
		
		responseNode = getNode(doc, GET_LOTES_FACTURAS_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing " + GET_LOTES_FACTURAS_NODE + " response");
			if(isGeneratePdf(context)) {
				generatePdf(responseNode, context);
			}
			return parseGetLotesFacturas(responseNode);
		}

		responseNode = getNode(doc, SOLICITA_CAEA_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing " + SOLICITA_CAEA_NODE + " response");
			return parseGetCaea(responseNode);
		}

		responseNode = getNode(doc, CONSULTA_CAEA_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing " + CONSULTA_CAEA_NODE + " response");
			return parseConsultaCaea(responseNode);
		}

		responseNode = getNode(doc, INFORMA_CAEA_NO_UTILIZADO_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing " + INFORMA_CAEA_NO_UTILIZADO_NODE + " response");
			return parseInformaCaeaNoUtilizado(responseNode);
		}
		
		return rawResponse;
	}

	private boolean isGeneratePdf(Map<String, Object> context) {
		if(context != null && context.containsKey(SAVE_PDF_PARAMETER)) {
			String value = context.get(SAVE_PDF_PARAMETER) != null ? String.valueOf(context.get(SAVE_PDF_PARAMETER)) : null;
			return ("yes".equalsIgnoreCase(value) || "si".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value) || "x".equalsIgnoreCase(value));
		}
		return false;
	}

	private void generatePdf(Node responseNode, Map<String, Object> context) {
		String type =  context.containsKey(PDF_TYPE) && context.get(PDF_TYPE) != null ? String.valueOf(context.get(PDF_TYPE)) : DEFAULT_PDF_TYPE;
		String filePrefix = context.containsKey(PDF_FILE_PREFIX) && context.get(PDF_FILE_PREFIX) != null ? String.valueOf(context.get(PDF_FILE_PREFIX)) : "";
		try {
			Document response = getXmlDocumentFromString(responseNode.getTextContent());
			
			Node docs = getNode(response, LOTE_COMPROBANTES_NODE);
			
			if (docs != null && docs.getChildNodes() != null) {
				Node n;
				String file;
				String xml;
				GenerarComprobanteFacade pdfFacade = new GenerarComprobanteFacade();
				int j = 0;
				for (int i = 0; i < docs.getChildNodes().getLength(); i++) {
					n = docs.getChildNodes().item(i);
					if(COMPROBANTE_NODE.equalsIgnoreCase(n.getNodeName())) {
						file = (j == 0) ? filePrefix + ".pdf" : filePrefix + "_" + j + ".pdf";
						xml = nodeToXmlString(n);
						if(LOG.isDebugEnabled()) {
							LOG.debug("About to convert to PDF [" + file + "]: " + xml);
						}
						FileUtils.writeFile(file, pdfFacade.generatePdf(xml, type));
						j++;
					}
				}
			}			
		} catch(Exception ex) {
			LOG.error("Could not generate PDF files type [" + type + "] for prefix [" + filePrefix + "]", ex);
		}
		
	}
	
	/**
	 * 
	 * @param responseNode
	 * @return
	 * @throws Exception
	 */
	private String parseCaeaErrors(Document response) throws Exception {
		for(int i=0; i < CAEA_ERRORS_NODES.length; i++) {
			Node errors = getNode(response, CAEA_ERRORS_NODES[i]);
			if(errors != null) {
				String errorResult = buildErrorResult(errors);
				LOG.debug("Found errors node [" + CAEA_ERRORS_NODES[i] + "]. Returning [" + errorResult + "]");
				return errorResult;
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param responseNode
	 * @return
	 * @throws Exception
	 */
	private String parseInformaCaeaNoUtilizado(Node responseNode) throws Exception {
		Document response = getXmlDocumentFromString(responseNode.getTextContent());
		
		String errorResult = parseCaeaErrors(response);
		if(errorResult != null) {
			return errorResult;
		}
		
		Node caeaResponse = getNode(response, INFORMA_CAEA_NO_UTILIZADO_RESULT_NODE);
		if(caeaResponse != null) {
			String successResult = buildInformaCaeaNoUtilizadoSuccessResult(caeaResponse);
			LOG.debug("Found node [" + INFORMA_CAEA_NO_UTILIZADO_RESULT_NODE + "]. Returning [" + successResult + "]");
			return successResult;
		}

		LOG.warn("No node " + INFORMA_CAEA_NO_UTILIZADO_RESULT_NODE + " found. Returning [" + NO_STATUS_RESULT + "]");

		return NO_STATUS_RESULT;
		
	}
	
	/**
	 * 
	 * @param responseNode
	 * @return
	 * @throws Exception
	 */
	private String parseConsultaCaea(Node responseNode) throws Exception {
		Document response = getXmlDocumentFromString(responseNode.getTextContent());
		
		String errorResult = parseCaeaErrors(response);
		if(errorResult != null) {
			return errorResult;
		}
		
		Node caeaResponse = getNode(response, CONSULTA_CAEA_RESULT_NODE);
		if(caeaResponse != null) {
			String successResult = buildConsultaCaeaSuccessResult(caeaResponse);
			LOG.debug("Found node [" + CONSULTA_CAEA_RESULT_NODE + "]. Returning [" + successResult + "]");
			return successResult;
		}

		LOG.warn("No node " + CONSULTA_CAEA_RESULT_NODE + " found. Returning [" + NO_STATUS_RESULT + "]");

		return NO_STATUS_RESULT;
		
	}
	
	/**
	 * 
	 * @param responseNode
	 * @return
	 * @throws Exception
	 */
	private String parseGetCaea(Node responseNode) throws Exception {
		Document response = getXmlDocumentFromString(responseNode.getTextContent());
		
		String errorResult = parseCaeaErrors(response);
		if(errorResult != null) {
			return errorResult;
		}
		
		Node caeaResponse = getNode(response, SOLICITA_CAEA_RESULT_NODE);
		if(caeaResponse != null) {
			String successResult = buildSolicitaCaeaSuccessResult(caeaResponse);
			LOG.debug("Found node [" + SOLICITA_CAEA_RESULT_NODE + "]. Returning [" + successResult + "]");
			return successResult;
		}

		LOG.warn("No node " + SOLICITA_CAEA_RESULT_NODE + " found. Returning [" + NO_STATUS_RESULT + "]");

		return NO_STATUS_RESULT;
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
			LOG.debug("Status node [" + RECEIVE_FACTURAS_STATUS_NODE + "] found. Value is: [" + statusText + "]");

			if(SUCCESS_STATUS.equalsIgnoreCase(statusText)) {
				String result = getNodeText(response, RECEIVE_FACTURAS_RESULT_NODE);
				if(StringUtils.isBlank(result) || RECEIVE_FACTURAS_SUCCESS_RESULT.equalsIgnoreCase(result)) {
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
			Node responseErrors = getNode(response, RECEIVE_FACTURAS_RERRORS_NODE);
			if(responseErrors != null) {
				String errorResult = buildErrorResult(responseErrors);
				LOG.debug("Receive response error node [" + RECEIVE_FACTURAS_RERRORS_NODE + "]. Returning [" + errorResult + "]");
				return errorResult;
			} else {
				LOG.warn("No node " + RECEIVE_FACTURAS_STATUS_NODE + " found. Returning [" + NO_STATUS_RESULT + "]");
				return NO_STATUS_RESULT;
			}
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
		boolean hasAfipReasons = false;
		
		Node errors = getNode(node, AFIP_REASON_NODES);
		if (errors != null && errors.getChildNodes() != null) {
			Node n;
			for (int i = 0; i < errors.getChildNodes().getLength(); i++) {
				n = errors.getChildNodes().item(i);
				if (AFIP_REASON_NODE.equalsIgnoreCase(n.getNodeName())) {
					hasAfipReasons = true;
					result.append(ERROR_MESSAGE_SEPARATOR + getNodeText(n, AFIP_REASON_CODE) + ERROR_MESSAGE_TOKEN + getNodeText(n, AFIP_REASON_DESCRIPTION));
				}
			}
		}
		
		if(!hasAfipReasons && node != null) {
			Node n = getNode(node, REASON_NODE);
			result.append(n != null ? ERROR_MESSAGE_SEPARATOR + n.getTextContent() : "");
		}
		
		return result.toString();
	}	
	
	
	/**
	 * 
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private String buildInformaCaeaNoUtilizadoSuccessResult(Node response) throws Exception {
		StringBuffer result = new StringBuffer(SUCCESS_RESULT);
		
		if (response != null) {
			result.append(SUCCESS_MESSAGE_SEPARATOR);

			Node n;
			
			for(int i=0; i < INFORMA_CAEA_NO_UTILIZADO_NODES.length; i++) {
				n = getNode(response, INFORMA_CAEA_NO_UTILIZADO_NODES[i]);
				result.append(n != null ? n.getTextContent() : "");
				if(i + 1 < INFORMA_CAEA_NO_UTILIZADO_NODES.length) {
					result.append(SUCCESS_MESSAGE_SEPARATOR);
				}
			}
		}
		
		return result.toString();

	}

	/**
	 * 
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private String buildConsultaCaeaSuccessResult(Node response) throws Exception {
		StringBuffer result = new StringBuffer(SUCCESS_RESULT);
		
		if (response != null) {
			result.append(SUCCESS_MESSAGE_SEPARATOR);

			Node n;
			
			for(int i=0; i < CONSULTA_CAEA_SUCCESS_NODES.length; i++) {
				n = getNode(response, CONSULTA_CAEA_SUCCESS_NODES[i]);
				result.append(n != null ? n.getTextContent() : "");
				if(i + 1 < CONSULTA_CAEA_SUCCESS_NODES.length) {
					result.append(SUCCESS_MESSAGE_SEPARATOR);
				}
			}
		}
		
		return result.toString();

	}

	/**
	 * 
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private String buildSolicitaCaeaSuccessResult(Node response) throws Exception {
		StringBuffer result = new StringBuffer(SUCCESS_RESULT);
		
		if (response != null) {
			result.append(SUCCESS_MESSAGE_SEPARATOR);

			Node n;
			
			for(int i=0; i < SOLICITA_CAEA_SUCCESS_NODES.length; i++) {
				n = getNode(response, SOLICITA_CAEA_SUCCESS_NODES[i]);
				result.append(n != null ? n.getTextContent() : "");
				if(i + 1 < SOLICITA_CAEA_SUCCESS_NODES.length) {
					result.append(SUCCESS_MESSAGE_SEPARATOR);
				}
			}
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
	
	public static void main(String args[]) throws Exception {
		Map<String, Object> context = new HashMap<String, Object>();
		context.put(SAVE_PDF_PARAMETER, "x");
		context.put(PDF_TYPE, "ORIGINAL");
		context.put(PDF_FILE_PREFIX, "D:\\salida");
		
		InterfacturaWSFacturaResponseParser p = new InterfacturaWSFacturaResponseParser();
		System.out.println(p.parseResponse(FileUtils.readFile("D:\\Development\\Projects\\bunge\\Interfactura\\Bunge\\trace\\lote-x_20100614.192431_resp.xml"), context));
		
	}
}
