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
public class AfipCTG10ResponseParser extends BaseResponseParser implements ResponseParser {
	private static final Logger LOG = Logger.getLogger(AfipCTG10ResponseParser.class);
	
	private static final String SUCCESS_RESULT = "OK";
	private static final String ERROR_RESULT = "ERROR";

	private static final String ERRORS_CONTAINER_NODE = "arrayErrores";
	private static final String ERROR_NODE = "error";
	
	private static final String ANULAR_CTG_NODE = "ns1:anularCTGResponse";
	private static final String ANULAR_CTG_RESPONSE_CONTAINER_NODE = "datosResponse";
	private static final String ANULAR_CTG_RESPONSE_NODES[] = {"cartaPorte", "ctg", "fechaHora", "codigoOperacion"};

	private static final String CAMBIAR_DESTINO_CTG_RECHAZADO_NODE = "ns1:cambiarDestinoDestinatarioCTGRechazadoResponse";
	private static final String CAMBIAR_DESTINO_CTG_RECHAZADO_RESPONSE_CONTAINER_NODE = "datosResponse";
	private static final String CAMBIAR_DESTINO_CTG_RECHAZADO_RESPONSE_NODES[] = {"cartaPorte", "ctg", "fechaHora", "codigoOperacion"};

	private static final String CONFIRMAR_ARRIBO_NODE = "ns1:confirmarArriboResponse";
	private static final String CONFIRMAR_ARRIBO_RESPONSE_CONTAINER_NODE = "datosResponse";
	private static final String CONFIRMAR_ARRIBO_RESPONSE_NODES[] = {"cartaPorte", "ctg", "fechaHora", "codigoOperacion"};

	private static final String CONFIRMAR_DEFINITIVO_NODE = "ns1:confirmarDefinitivoResponse";
	private static final String CONFIRMAR_DEFINITIVO_RESPONSE_CONTAINER_NODE = "datosResponse";
	private static final String CONFIRMAR_DEFINITIVO_RESPONSE_NODES[] = {"cartaPorte", "ctg", "fechaHora", "codigoOperacion"};
	
	private static final String CONSULTAR_CONSTANCIA_CTG_PDF_NODE = "ns1:consultarConstanciaCTGPDFResponse";
	private static final String CONSULTAR_CONSTANCIA_CTG_PDF_RESPONSE_CONTAINER_NODE = "archivo";
	private static final String CONSULTAR_CONSTANCIA_CTG_PDF_RESPONSE_NODES[] = {"archivo"};
	
	private static final String CONSULTAR_CTG_NODE = "ns1:consultarCTGResponse";
	private static final String CONSULTAR_CTG_HEADER_CONTAINER_NODE = "consultarCTGDatos";
	private static final String CONSULTAR_CTG_HEADER_RESPONSE_NODES[] = {"cartaPorte", "ctg", "patente", "fechaEmisionDesde", "fechaEmisionHasta", "cuitSolicitante", "cuitDestino"};
	private static final String CONSULTAR_CTG_DETAILS_CONTAINER_NODE = "arrayDatosConsultaCTG";
	private static final String CONSULTAR_CTG_DETAILS_NODE = "datosConsultaCTG";
	private static final String CONSULTAR_CTG_DETAILS_RESPONSE_NODES[] = {"fechaSolicitud", "ctg", "cartaPorte", "estado", "imprimeConstancia"};

	private static final String CONSULTAR_CTG_ACTIVOS_PATENTE_NODE = "ns1:consultarCTGActivosPorPatenteResponse";
	private static final String CONSULTAR_CTG_ACTIVOS_PATENTE_ROWS_CONTAINER_NODE = "response";
	private static final String CONSULTAR_CTG_ACTIVOS_PATENTE_ROW_NODE = "arrayConsultarCTGActivosPorPatenteResponse";
	private static final String CONSULTAR_CTG_ACTIVOS_PATENTE_ROW_RESPONSE_NODES[] = {"ctg", "cartaPorte", "patente", "pesoNeto", "fechaEmision", "fechaVencimiento", "usuarioSolicitante", "usuarioReal"};

	private static final String CONSULTAR_CTG_EXCEL_NODE = "ns1:consultarCTGExcelResponse";
	private static final String CONSULTAR_CTG_EXCEL_RESPONSE_CONTAINER_NODE = "archivo";
	private static final String CONSULTAR_CTG_EXCEL_RESPONSE_NODES[] = {"archivo"};

	private static final String CONSULTAR_CTG_RECHAZADOS_NODE = "ns1:consultarCTGRechazadosResponse";
	private static final String CONSULTAR_CTG_RECHAZADOS_ROWS_CONTAINER_NODE = "response";
	private static final String CONSULTAR_CTG_RECHAZADOS_ROW_NODE = "arrayConsultarCTGRechazados";
	private static final String CONSULTAR_CTG_RECHAZADOS_ROW_RESPONSE_NODES[] = {"ctg", "cartaPorte", "fechaRechazo", "destino", "destinatario", "observaciones"};
	
	private static final String CONSULTAR_DETALLE_CTG_NODE = "ns1:consultarDetalleCTGResponse";
	private static final String CONSULTAR_DETALLE_CTG_RESPONSE_CONTAINER_NODE = "consultarDetalleCTGDatos";
	private static final String CONSULTAR_DETALLE_CTG_RESPONSE_NODES[] = {"ctg", "solicitante", "cartaPorte", "estado", "fechaEmision", "fechaVigenciaDesde", "fechaVigenciaHasta", "especie", "cuitCanjeador", "cuitDestino", "cuitDestinatario", "establecimiento", "localidadOrigen", "localidadDestino", "cosecha", "cuitTransportista", "cantidadHoras", "patenteVehiculo", "pesoNetoCarga"};

	private static final String DESVIAR_CTG_OTRO_ESTABLECIMIENTO_NODE = "ns1:desviarCTGAOtroEstablecimientoResponse";
	private static final String DESVIAR_CTG_OTRO_ESTABLECIMIENTO_RESPONSE_CONTAINER_NODE = "datosResponse";
	private static final String DESVIAR_CTG_OTRO_ESTABLECIMIENTO_RESPONSE_NODES[] = {"cartaPorte", "ctg", "fechaHora", "codigoOperacion"};

	private static final String DESVIAR_CTG_OTRO_DESTINO_NODE = "ns1:desviarCTGAOtroDestinoResponse";
	private static final String DESVIAR_CTG_OTRO_DESTINO_RESPONSE_CONTAINER_NODE = "datosResponse";
	private static final String DESVIAR_CTG_OTRO_DESTINO_RESPONSE_NODES[] = {"cartaPorte", "ctg", "fechaHora", "codigoOperacion"};
	
	private static final String RECHAZAR_CTG_NODE = "ns1:rechazarCTGResponse";
	private static final String RECHAZAR_CTG_RESPONSE_CONTAINER_NODE = "datosResponse";
	private static final String RECHAZAR_CTG_RESPONSE_NODES[] = {"cartaPorte", "ctg", "fechaHora", "codigoOperacion"};

	private static final String REGRESAR_ORIGEN_CTG_RECHAZADO_NODE = "ns1:regresarAOrigenCTGRechazadoResponse";
	private static final String REGRESAR_ORIGEN_CTG_RECHAZADO_RESPONSE_CONTAINER_NODE = "datosResponse";
	private static final String REGRESAR_ORIGEN_CTG_RECHAZADO_RESPONSE_NODES[] = {"cartaPorte", "ctg", "fechaHora", "codigoOperacion"};
	
	private static final String SOLICITAR_CTG_DATO_PENDIENTE_NODE = "ns1:solicitarCTGDatoPendienteResponse";
	private static final String SOLICITAR_CTG_DATO_PENDIENTE_HEADER_CONTAINER_NODE = "response";
	private static final String SOLICITAR_CTG_DATO_PENDIENTE_HEADER_RESPONSE_NODES[] = {"observacion", "cartaPorte", "ctg", "fechaEmision", "fechaVigenciaDesde", "fechaVigenciaHasta"};
	private static final String SOLICITAR_CTG_DATO_PENDIENTE_DETAILS_CONTAINER_NODE = "arrayControles";
	private static final String SOLICITAR_CTG_DATO_PENDIENTE_DETAILS_NODE = "control";
	private static final String SOLICITAR_CTG_DATO_PENDIENTE_DETAILS_RESPONSE_NODES[] = {"tipo", "descripcion"};
	
	private static final String SOLICITAR_CTG_INICIAL_NODE = "ns1:solicitarCTGInicialResponse";
	private static final String SOLICITAR_CTG_INICIAL_HEADER_CONTAINER_NODE = "response";
	private static final String SOLICITAR_CTG_INICIAL_HEADER_RESPONSE_NODES[] = {"observacion", "cartaPorte", "ctg", "fechaEmision", "fechaVigenciaDesde", "fechaVigenciaHasta"};
	private static final String SOLICITAR_CTG_INICIAL_DETAILS_CONTAINER_NODE = "arrayControles";
	private static final String SOLICITAR_CTG_INICIAL_DETAILS_NODE = "control";
	private static final String SOLICITAR_CTG_INICIAL_DETAILS_RESPONSE_NODES[] = {"tipo", "descripcion"};
	
	// Tokens
	private static final String RESULT_MESSAGE_SEPARATOR = FileUtils.getNewLine();


	/**
	 * 
	 */
	public AfipCTG10ResponseParser() {
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
		
		Node responseNode = getNode(doc, ANULAR_CTG_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + ANULAR_CTG_NODE + "] response");
			return parseSimpleResponse(responseNode, ANULAR_CTG_RESPONSE_CONTAINER_NODE, ANULAR_CTG_RESPONSE_NODES);
		}
		
		responseNode = getNode(doc, CAMBIAR_DESTINO_CTG_RECHAZADO_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + CAMBIAR_DESTINO_CTG_RECHAZADO_NODE + "] response");
			return parseSimpleResponse(responseNode, CAMBIAR_DESTINO_CTG_RECHAZADO_RESPONSE_CONTAINER_NODE, CAMBIAR_DESTINO_CTG_RECHAZADO_RESPONSE_NODES);
		}		

		responseNode = getNode(doc, CONFIRMAR_ARRIBO_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + CONFIRMAR_ARRIBO_NODE + "] response");
			return parseSimpleResponse(responseNode, CONFIRMAR_ARRIBO_RESPONSE_CONTAINER_NODE, CONFIRMAR_ARRIBO_RESPONSE_NODES);
		}		

		responseNode = getNode(doc, CONFIRMAR_DEFINITIVO_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + CONFIRMAR_DEFINITIVO_NODE + "] response");
			return parseSimpleResponse(responseNode, CONFIRMAR_DEFINITIVO_RESPONSE_CONTAINER_NODE, CONFIRMAR_DEFINITIVO_RESPONSE_NODES);
		}		

		responseNode = getNode(doc, CONSULTAR_CONSTANCIA_CTG_PDF_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + CONSULTAR_CONSTANCIA_CTG_PDF_NODE + "] response");
			return parseSimpleResponse(responseNode, CONSULTAR_CONSTANCIA_CTG_PDF_RESPONSE_CONTAINER_NODE, CONSULTAR_CONSTANCIA_CTG_PDF_RESPONSE_NODES);
		}		

		responseNode = getNode(doc, CONSULTAR_CTG_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + CONSULTAR_CTG_NODE + "] response");
			return parseHeaderDetailsResponse(responseNode, CONSULTAR_CTG_HEADER_CONTAINER_NODE, CONSULTAR_CTG_HEADER_RESPONSE_NODES, CONSULTAR_CTG_DETAILS_CONTAINER_NODE, CONSULTAR_CTG_DETAILS_NODE, CONSULTAR_CTG_DETAILS_RESPONSE_NODES);
		}				

		responseNode = getNode(doc, CONSULTAR_CTG_ACTIVOS_PATENTE_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + CONSULTAR_CTG_ACTIVOS_PATENTE_NODE + "] response");
			return parseMultiRowResponse(responseNode, CONSULTAR_CTG_ACTIVOS_PATENTE_ROWS_CONTAINER_NODE, CONSULTAR_CTG_ACTIVOS_PATENTE_ROW_NODE, CONSULTAR_CTG_ACTIVOS_PATENTE_ROW_RESPONSE_NODES);
		}				
		
		responseNode = getNode(doc, CONSULTAR_CTG_EXCEL_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + CONSULTAR_CTG_EXCEL_NODE + "] response");
			return parseSimpleResponse(responseNode, CONSULTAR_CTG_EXCEL_RESPONSE_CONTAINER_NODE, CONSULTAR_CTG_EXCEL_RESPONSE_NODES);
		}		

		responseNode = getNode(doc, CONSULTAR_CTG_RECHAZADOS_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + CONSULTAR_CTG_RECHAZADOS_NODE + "] response");
			return parseMultiRowResponse(responseNode, CONSULTAR_CTG_RECHAZADOS_ROWS_CONTAINER_NODE, CONSULTAR_CTG_RECHAZADOS_ROW_NODE, CONSULTAR_CTG_RECHAZADOS_ROW_RESPONSE_NODES);
		}				

		responseNode = getNode(doc, CONSULTAR_DETALLE_CTG_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + CONSULTAR_DETALLE_CTG_NODE + "] response");
			return parseSimpleResponse(responseNode, CONSULTAR_DETALLE_CTG_RESPONSE_CONTAINER_NODE, CONSULTAR_DETALLE_CTG_RESPONSE_NODES);
		}		

		responseNode = getNode(doc, DESVIAR_CTG_OTRO_ESTABLECIMIENTO_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + DESVIAR_CTG_OTRO_ESTABLECIMIENTO_NODE + "] response");
			return parseSimpleResponse(responseNode, DESVIAR_CTG_OTRO_ESTABLECIMIENTO_RESPONSE_CONTAINER_NODE, DESVIAR_CTG_OTRO_ESTABLECIMIENTO_RESPONSE_NODES);
		}		

		responseNode = getNode(doc, DESVIAR_CTG_OTRO_DESTINO_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + DESVIAR_CTG_OTRO_DESTINO_NODE + "] response");
			return parseSimpleResponse(responseNode, DESVIAR_CTG_OTRO_DESTINO_RESPONSE_CONTAINER_NODE, DESVIAR_CTG_OTRO_DESTINO_RESPONSE_NODES);
		}		

		responseNode = getNode(doc, RECHAZAR_CTG_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + RECHAZAR_CTG_NODE + "] response");
			return parseSimpleResponse(responseNode, RECHAZAR_CTG_RESPONSE_CONTAINER_NODE, RECHAZAR_CTG_RESPONSE_NODES);
		}		
		
		responseNode = getNode(doc, REGRESAR_ORIGEN_CTG_RECHAZADO_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + REGRESAR_ORIGEN_CTG_RECHAZADO_NODE + "] response");
			return parseSimpleResponse(responseNode, REGRESAR_ORIGEN_CTG_RECHAZADO_RESPONSE_CONTAINER_NODE, REGRESAR_ORIGEN_CTG_RECHAZADO_RESPONSE_NODES);
		}		

		responseNode = getNode(doc, SOLICITAR_CTG_DATO_PENDIENTE_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + SOLICITAR_CTG_DATO_PENDIENTE_NODE + "] response");
			return parseHeaderDetailsResponse(responseNode, SOLICITAR_CTG_DATO_PENDIENTE_HEADER_CONTAINER_NODE, SOLICITAR_CTG_DATO_PENDIENTE_HEADER_RESPONSE_NODES, SOLICITAR_CTG_DATO_PENDIENTE_DETAILS_CONTAINER_NODE, SOLICITAR_CTG_DATO_PENDIENTE_DETAILS_NODE, SOLICITAR_CTG_DATO_PENDIENTE_DETAILS_RESPONSE_NODES);
		}	
				
		responseNode = getNode(doc, SOLICITAR_CTG_INICIAL_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + SOLICITAR_CTG_INICIAL_NODE + "] response");
			return parseHeaderDetailsResponse(responseNode, SOLICITAR_CTG_INICIAL_HEADER_CONTAINER_NODE, SOLICITAR_CTG_INICIAL_HEADER_RESPONSE_NODES, SOLICITAR_CTG_INICIAL_DETAILS_CONTAINER_NODE, SOLICITAR_CTG_INICIAL_DETAILS_NODE, SOLICITAR_CTG_INICIAL_DETAILS_RESPONSE_NODES);
		}	
		
		return rawResponse;
	}


	/**
	 * 
	 * @param responseNode
	 * @param mainNodeName
	 * @param responseNodes
	 * @return
	 * @throws Exception
	 */
	private String parseSimpleResponse(Node responseNode, String responseContainerNodeName, String responseNodes[]) throws Exception {
		String errors = buildErrorResult(getNode(responseNode, ERRORS_CONTAINER_NODE));

		StringBuffer response = new StringBuffer(errors != null ? errors + RESULT_MESSAGE_SEPARATOR : "");
		Node responseDataNode = getNode(responseNode, responseContainerNodeName);

		if(responseDataNode != null) {
			response.append(SUCCESS_RESULT + RESULT_MESSAGE_SEPARATOR);
			response.append(buildResultRow(responseDataNode, responseNodes));
		}

		return response.toString();
	}
	
	/**
	 * 
	 * @param responseNode
	 * @param headerContainerNodeName
	 * @param headerNodes
	 * @param detailsContainerNodeName
	 * @param detailsNodeName
	 * @param detailsNodes
	 * @return
	 * @throws Exception
	 */
	private String parseHeaderDetailsResponse(Node responseNode, String headerContainerNodeName, String headerNodes[], String detailsContainerNodeName, String detailsNodeName, String detailsNodes[]) throws Exception {
		String errors = buildErrorResult(getNode(responseNode, ERRORS_CONTAINER_NODE));

		StringBuffer response = new StringBuffer(errors != null ? errors + RESULT_MESSAGE_SEPARATOR : "");
		Node responseDataNode = getNode(responseNode, headerContainerNodeName);

		if(responseDataNode != null) {
			response.append(SUCCESS_RESULT + RESULT_MESSAGE_SEPARATOR);
			response.append(buildResultRow(responseDataNode, headerNodes));
		}

		responseDataNode = getNode(responseNode, detailsContainerNodeName);
		if (responseDataNode != null && responseDataNode.getChildNodes() != null) {
			Node n;
			for (int i = 0; i < responseDataNode.getChildNodes().getLength(); i++) {
				n = responseDataNode.getChildNodes().item(i);
				if (detailsNodeName.equalsIgnoreCase(n.getNodeName())) {
					response.append(RESULT_MESSAGE_SEPARATOR);
					response.append(buildResultRow(n, detailsNodes));
				}
			}
		}		
		
		return response.toString();
	}	

	/**
	 * 
	 * @param responseNode
	 * @param rowsContainerNodeName
	 * @param rowNodeName
	 * @param rowNodes
	 * @return
	 * @throws Exception
	 */
	private String parseMultiRowResponse(Node responseNode, String rowsContainerNodeName, String rowNodeName, String rowNodes[]) throws Exception {
		String errors = buildErrorResult(getNode(responseNode, ERRORS_CONTAINER_NODE));

		StringBuffer response = new StringBuffer(errors != null ? errors + RESULT_MESSAGE_SEPARATOR : "");
		Node responseDataNode = getNode(responseNode, rowsContainerNodeName);

		if (responseDataNode != null && responseDataNode.getChildNodes() != null) {
			Node n;
			for (int i = 0; i < responseDataNode.getChildNodes().getLength(); i++) {
				n = responseDataNode.getChildNodes().item(i);
				if (rowNodeName.equalsIgnoreCase(n.getNodeName())) {
					response.append(RESULT_MESSAGE_SEPARATOR);
					response.append(buildResultRow(n, rowNodes));
				}
			}
		}		
		
		return response.toString();
	}		
	/**
	 * 
	 * @param responseNode
	 * @param valueNodeNames
	 * @return
	 * @throws Exception
	 */
	private String buildResultRow(Node responseNode, String valueNodeNames[]) throws Exception {
		StringBuffer response = new StringBuffer();
		String value;
		for(int i=0; i < valueNodeNames.length; i++) {
			value = getNodeText(responseNode, valueNodeNames[i]);
			if(value != null) {
				response.append(value.trim());
			}
			// Jose wants a ; at the end of each row
			if(i < valueNodeNames.length) {
				response.append(SUCCESS_MESSAGE_SEPARATOR);
			}
		}
		return response.toString();
	}
	
	/**
	 * 
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private String buildErrorResult(Node errors) throws Exception {
		StringBuffer errorResult = new StringBuffer(ERROR_RESULT);

		boolean hasErrors = false;
		if (errors != null && errors.getChildNodes() != null) {
			Node n;
			for (int i = 0; i < errors.getChildNodes().getLength(); i++) {
				n = errors.getChildNodes().item(i);
				if (ERROR_NODE.equalsIgnoreCase(n.getNodeName())) {
					errorResult.append(RESULT_MESSAGE_SEPARATOR + getNodeText(n, ERROR_NODE));
					hasErrors = true;
				}
			}
		}
		
		return hasErrors ? errorResult.toString() : null;
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
			AfipCTG10ResponseParser r = new AfipCTG10ResponseParser();
			System.out.println(r.parseResponse(FileUtils.readFile("C:\\file.txt"), null));
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
