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
public class AfipLPGResponseParser extends BaseResponseParser implements ResponseParser {
	private static final Logger LOG = Logger.getLogger(AfipLPGResponseParser.class);
	
	private static final String SUCCESS_RESULT = "OK";
	private static final String ERROR_RESULT = "ERROR";

	private static final String ERRORS_CONTAINER_NODE = "errores";
	private static final String ERROR_NODE = "error";
	
	private static final String FORMAT_ERRORS_CONTAINER_NODE = "erroresFormato";
	private static final String EVENTS_CONTAINER_NODE = "eventos";
	private static final String EVENT_NODE = "evento";

	private static final String CODE_DESCRIPTION_DETAIL_RESPONSE_NODES[] = {"codigo", "descripcion"};  
	private static final String DEDUCCIONES_LPG_DETAIL_RESPONSE_NODES[] = {"codigoConcepto", "detalleAclaratorio", "diasAlmacenaje", "precioPKGdiario", "comisionGastosAdm", "baseCalculo", "alicuotaIva", "importeIva", "importeDeduccion"};
	private static final String RETENCIONES_LPG_DETAIL_RESPONSE_NODES[] = {"codigoConcepto", "detalleAclaratorio", "baseCalculo", "alicuota", "nroCertificadoRetencion", "fechaCertificadoRetencion", "importeCertificadoRetencion", "importeRetencion"};
	private static final String CERTIFICACIONES_LPG_DETAIL_RESPONSE_NODES[] = {"tipoCertificadoDeposito", "nroCertificadoDeposito", "pesoNeto", "codLocalidadProcedencia", "codProvProcedencia", "campania", "fechaCierretipoCertificadoDeposito", "nroCertificadoDeposito", "pesoNeto", "codLocalidadProcedencia", "codProvProcedencia", "campania", "fechaCierre"};
	private static final String IMPORTES_LPG_DETAIL_RESPONSE_NODES[] = {"importe", "concepto", "alicuota", "ivaCalculado"};

	private static final String LIQUIDACION_LPG_NODE = "ns2:liquidacionResp";
	private static final String LIQUIDACION_LPG_RESPONSE_CONTAINER_NODE = "liqReturn";
	private static final String LIQUIDACION_LPG_HEADER_RESPONSE_NODES[] = {"coe", "coeAjustado", "estado", "ptoEmision", "nroOrden", "codTipoOperacion", "nroOpComercial", "fechaLiquidacion", "precioOperacion", "subTotal", "importeIva", "operacionConIva", "totalPesoNeto"};
	private static final String LIQUIDACION_LPG_TOTALES_RESPONSE_NODES[] = {"totalDeduccion", "totalRetencion", "totalRetencionAfip", "totalOtrasRetenciones", "totalNetoAPagar", "totalIvaRg2300_07", "totalPagoSegunCondicion"};

	private static final String AJUSTE_LPG_NODE = "ns2:ajusteResp";
	private static final String AJUSTE_LPG_RESPONSE_CONTAINER_NODE = "ajusteReturn";
	private static final String AJUSTE_LPG_HEADER_RESPONSE_NODES[] = {"coe", "coeAjustado", "estado", "ptoEmision", "nroOrden", "codTipoOperacion", "codTipoAjuste", "nroOpComercial", "fechaLiquidacion", "precioOperacion", "subTotal", "importeIva", "operacionConIva", "totalPesoNeto"};
	private static final String AJUSTE_LPG_TOTALES_RESPONSE_NODES[] = {"totalDeduccion", "totalRetencion", "totalRetencionAfip", "totalOtrasRetenciones", "totalNetoAPagar", "totalIvaRg2300_07", "totalPagoSegunCondicion"};

	private static final String AJUSTAR_UNIFICADO_LPG_NODE = "ns2:ajustarUnificadoResp";
	private static final String AJUSTAR_UNIFICADO_LPG_RESPONSE_CONTAINER_NODE = "ajusteUnifReturn";
	private static final String AJUSTAR_UNIFICADO_LPG_HEADER_RESPONSE_NODES[] = {"coe", "coeAjustado", "estado", "ptoEmision", "nroOrden", "nroContrato", "codTipoOperacion"};
	private static final String AJUSTAR_UNIFICADO_LPG_TOTALES_RESPONSE_NODES[] = {"subTotalGeneral", "iva105", "iva21", "retencionesGanancias", "retencionesIVA", "importeNeto", "ivaRG2300_2007", "pagoSCondicion"};
	private static final String AJUSTAR_UNIFICADO_CREDITO_LPG_RESPONSE_NODES[] = {"nroOpComercial", "fechaLiquidacion", "precioOperacion", "subTotal", "importeIva", "operacionConIva", "totalPesoNeto", "totalDeduccion", "totalRetencion", "totalRetencionAfip", "totalOtrasRetenciones", "totalNetoAPagar", "totalIvaRg2300_07", "totalPagoSegunCondicion"};
	private static final String AJUSTAR_UNIFICADO_DEBITO_LPG_RESPONSE_NODES[] = {"nroOpComercial", "fechaLiquidacion", "precioOperacion", "subTotal", "importeIva", "operacionConIva", "totalPesoNeto", "totalDeduccion", "totalRetencion", "totalRetencionAfip", "totalOtrasRetenciones", "totalNetoAPagar", "totalIvaRg2300_07", "totalPagoSegunCondicion"};

	private static final String AJUSTAR_CONTRATO_LPG_NODE = "ns2:ajustarContratoResp";
	private static final String AJUSTAR_CONTRATO_LPG_RESPONSE_CONTAINER_NODE = "ajusteContratoReturn";
	private static final String AJUSTAR_CONTRATO_LPG_HEADER_RESPONSE_NODES[] = {"coe", "coeAjustado", "estado", "ptoEmision", "nroOrden", "nroContrato", "codTipoOperacion"};
	private static final String AJUSTAR_CONTRATO_LPG_TOTALES_RESPONSE_NODES[] = {"subTotalGeneral", "iva105", "iva21", "retencionesGanancias", "retencionesIVA", "importeNeto", "ivaRG2300_2007", "pagoSCondicion"};
	private static final String AJUSTAR_CONTRATO_CREDITO_LPG_RESPONSE_NODES[] = {"nroOpComercial", "fechaLiquidacion", "precioOperacion", "subTotal", "importeIva", "operacionConIva", "totalPesoNeto", "totalDeduccion", "totalRetencion", "totalRetencionAfip", "totalOtrasRetenciones", "totalNetoAPagar", "totalIvaRg2300_07", "totalPagoSegunCondicion"};
	private static final String AJUSTAR_CONTRATO_DEBITO_LPG_RESPONSE_NODES[] = {"nroOpComercial", "fechaLiquidacion", "precioOperacion", "subTotal", "importeIva", "operacionConIva", "totalPesoNeto", "totalDeduccion", "totalRetencion", "totalRetencionAfip", "totalOtrasRetenciones", "totalNetoAPagar", "totalIvaRg2300_07", "totalPagoSegunCondicion"};
	
	private static final String ANULACION_LPG_NODE = "ns2:anulacionResp";
	private static final String ANULACION_LPG_RESPONSE_CONTAINER_NODE = "anulacionReturn";
	private static final String ANULACION_LPG_HEADER_RESPONSE_NODES[] = {"coe", "resultado"};

	private static final String CONSULTA_ULTIMO_NRO_ORDEN_LPG_NODE = "ns2:liqUltNroOrdenResp";
	private static final String CONSULTA_ULTIMO_NRO_ORDEN_LPG_RESPONSE_CONTAINER_NODE = "liqUltNroOrdenReturn";
	private static final String CONSULTA_ULTIMO_NRO_ORDEN_LPG_HEADER_RESPONSE_NODES[] = {"nroOrden"};

	private static final String CONSULTAR_CONTRATO_LPG_NODE = "ns2:ajustePorContratoConsultarResp";
	private static final String CONSULTAR_CONTRATO_LPG_RESPONSE_CONTAINER_NODE = "ajusteContratoReturn";
	private static final String CONSULTAR_CONTRATO_LPG_HEADER_RESPONSE_NODES[] = {"coe", "coeAjustado", "estado", "ptoEmision", "nroOrden", "nroContrato", "codTipoOperacion"};
	private static final String CONSULTAR_CONTRATO_LPG_TOTALES_RESPONSE_NODES[] = {"subTotalGeneral", "iva105", "iva21", "retencionesGanancias", "retencionesIVA", "importeNeto", "ivaRG2300_2007", "pagoSCondicion"};
	private static final String CONSULTAR_CONTRATO_CREDITO_LPG_RESPONSE_NODES[] = {"nroOpComercial", "fechaLiquidacion", "precioOperacion", "subTotal", "importeIva", "operacionConIva", "totalPesoNeto", "totalDeduccion", "totalRetencion", "totalRetencionAfip", "totalOtrasRetenciones", "totalNetoAPagar", "totalIvaRg2300_07", "totalPagoSegunCondicion"};
	private static final String CONSULTAR_CONTRATO_DEBITO_LPG_RESPONSE_NODES[] = {"nroOpComercial", "fechaLiquidacion", "precioOperacion", "subTotal", "importeIva", "operacionConIva", "totalPesoNeto", "totalDeduccion", "totalRetencion", "totalRetencionAfip", "totalOtrasRetenciones", "totalNetoAPagar", "totalIvaRg2300_07", "totalPagoSegunCondicion"};
	
	private static final String CONSULTAR_UNIFICADO_LPG_NODE = "ns2:ajustarUnificadoResp";
	private static final String CONSULTAR_UNIFICADO_LPG_RESPONSE_CONTAINER_NODE = "ajusteUnifReturn";
	private static final String CONSULTAR_UNIFICADO_LPG_HEADER_RESPONSE_NODES[] = {"coe", "coeAjustado", "estado", "ptoEmision", "nroOrden", "nroContrato", "codTipoOperacion"};
	private static final String CONSULTAR_UNIFICADO_LPG_TOTALES_RESPONSE_NODES[] = {"subTotalGeneral", "iva105", "iva21", "retencionesGanancias", "retencionesIVA", "importeNeto", "ivaRG2300_2007", "pagoSCondicion"};
	private static final String CONSULTAR_UNIFICADO_CREDITO_LPG_RESPONSE_NODES[] = {"nroOpComercial", "fechaLiquidacion", "precioOperacion", "subTotal", "importeIva", "operacionConIva", "totalPesoNeto", "totalDeduccion", "totalRetencion", "totalRetencionAfip", "totalOtrasRetenciones", "totalNetoAPagar", "totalIvaRg2300_07", "totalPagoSegunCondicion"};
	private static final String CONSULTAR_UNIFICADO_DEBITO_LPG_RESPONSE_NODES[] = {"nroOpComercial", "fechaLiquidacion", "precioOperacion", "subTotal", "importeIva", "operacionConIva", "totalPesoNeto", "totalDeduccion", "totalRetencion", "totalRetencionAfip", "totalOtrasRetenciones", "totalNetoAPagar", "totalIvaRg2300_07", "totalPagoSegunCondicion"};
	
	private static final String CONSULTA_COE_LPG_NODE = "ns2:liqConsXCoeResp";
	private static final String CONSULTA_COE_LPG_HEADER1_RESPONSE_NODES[] = {"nroOrden", "cuitComprador", "nroActComprador", "nroIngBrutoComprador", "codTipoOperacion", "codTipoAjuste", "nroOpComercial", "esLiquidacionPropia", "esCanje", "codPuerto", "desPuertoLocalidad", "codGrano", "cuitVendedor", "nroIngBrutoVendedor", "actuaCorredor", "liquidaCorredor", "cuitCorredor", "comisionCorredor", "nroIngBrutoCorredor", "fechaPrecioOperacion", "precioRefTn", "codGradoRef", "codGradoEnt", "valGradoEnt", "factorEnt", "precioFleteTn", "contProteico", "alicIvaOperacion", "campaniaPPal", "codLocalidadProcedencia", "datosAdicionales"};
	private static final String CONSULTA_COE_LPG_HEADER2_RESPONSE_NODES[] = {"coe", "coeAjustado", "estado", "ptoEmision", "nroOrden", "codTipoOperacion", "codTipoAjuste", "nroOpComercial", "fechaLiquidacion", "precioOperacion", "subTotal", "importeIva", "operacionConIva", "totalPesoNeto"};

	private static final String CONSULTA_NRO_ORDEN_LPG_NODE = "ns2:liqConsXNroOrdenResp";
	private static final String CONSULTA_NRO_ORDEN_LPG_HEADER1_RESPONSE_NODES[] = {"nroOrden", "cuitComprador", "nroActComprador", "nroIngBrutoComprador", "codTipoOperacion", "codTipoAjuste", "nroOpComercial", "esLiquidacionPropia", "esCanje", "codPuerto", "desPuertoLocalidad", "codGrano", "cuitVendedor", "nroIngBrutoVendedor", "actuaCorredor", "liquidaCorredor", "cuitCorredor", "comisionCorredor", "nroIngBrutoCorredor", "fechaPrecioOperacion", "precioRefTn", "codGradoRef", "codGradoEnt", "valGradoEnt", "factorEnt", "precioFleteTn", "contProteico", "alicIvaOperacion", "campaniaPPal", "codLocalidadProcedencia", "datosAdicionales"};
	private static final String CONSULTA_NRO_ORDEN_LPG_HEADER2_RESPONSE_NODES[] = {"coe", "coeAjustado", "estado", "ptoEmision", "nroOrden", "codTipoOperacion", "codTipoAjuste", "nroOpComercial", "fechaLiquidacion", "precioOperacion", "subTotal", "importeIva", "operacionConIva", "totalPesoNeto"};

	private static final String CAMPANIA_LPG_NODE = "ns2:campaniaResp";
	private static final String TIPO_GRANO_LPG_NODE = "ns2:tipoGranoResp";
	private static final String GRADO_REFERENCIA_LPG_NODE = "ns2:gradoReferenciaResp";
	private static final String GRADO_ENTREGADO_LPG_NODE = "ns2:gradoEntregadoResp";
	private static final String GRADO_ENTREGADO_RESPONSE_NODES[] = {"codigo", "descripcion", "valor"};
	private static final String TIPO_CERTIFICADO_DEPOSITO_LPG_NODE = "ns2:tipoCertificadoDepResp";
	private static final String TIPO_DEDUCCION_LPG_NODE = "ns2:tipoDeduccionResp";
	private static final String TIPO_RETENCION_LPG_NODE = "ns2:tipoRetencionResp";
	private static final String PUERTOS_LPG_NODE = "ns2:puertoResp";
	private static final String TIPO_ACTIVIDAD_LPG_NODE = "ns2:tipoActividadResp";
	private static final String TIPO_ACTIVIDAD_REPRESENTADO_LPG_NODE = "ns2:tipoActividadRepresentadoResp";
	private static final String PROVINCIAS_LPG_NODE = "ns2:provinciasResp";
	private static final String LOCALIDADES_LPG_NODE = "ns2:localidadResp";
	private static final String TIPO_OPERACION_LPG_NODE = "ns2:tipoOperacionResp";
	
	// Tokens
	private static final String RESULT_MESSAGE_SEPARATOR = FileUtils.getNewLine();


	/**
	 * 
	 */
	public AfipLPGResponseParser() {
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
		
		Node responseNode = getNode(doc, AJUSTE_LPG_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + AJUSTE_LPG_NODE + "] response");
			StringBuilder response = new StringBuilder();
			
			generateStatusAndMessages(response, responseNode);
			response.append(parseSingleRowResponse("H", responseNode, AJUSTE_LPG_RESPONSE_CONTAINER_NODE, AJUSTE_LPG_HEADER_RESPONSE_NODES));
			response.append(parseMultiRowResponse("D", getNode(responseNode, AJUSTE_LPG_RESPONSE_CONTAINER_NODE), "deducciones", "deduccionReturn", DEDUCCIONES_LPG_DETAIL_RESPONSE_NODES));
			response.append(parseMultiRowResponse("R", getNode(responseNode, AJUSTE_LPG_RESPONSE_CONTAINER_NODE), "retenciones", "retencionReturn", RETENCIONES_LPG_DETAIL_RESPONSE_NODES));
			response.append(parseSingleRowResponse("T", responseNode, AJUSTE_LPG_RESPONSE_CONTAINER_NODE, AJUSTE_LPG_TOTALES_RESPONSE_NODES));
			
			return response.toString();
		}

		responseNode = getNode(doc, AJUSTAR_UNIFICADO_LPG_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + AJUSTAR_UNIFICADO_LPG_NODE + "] response");
			StringBuilder response = new StringBuilder();

			generateStatusAndMessages(response, responseNode);
			response.append(parseSingleRowResponse("H", responseNode, AJUSTAR_UNIFICADO_LPG_RESPONSE_CONTAINER_NODE, AJUSTAR_UNIFICADO_LPG_HEADER_RESPONSE_NODES));

			response.append(parseSingleRowResponse("AC", responseNode, "ajusteCredito", AJUSTAR_UNIFICADO_CREDITO_LPG_RESPONSE_NODES));
			response.append(parseMultiRowResponse("I", getNode(responseNode, "ajusteCredito"), "importes", "importeReturn", IMPORTES_LPG_DETAIL_RESPONSE_NODES));
			response.append(parseMultiRowResponse("D", getNode(responseNode, "ajusteCredito"), "deducciones", "deduccionReturn", DEDUCCIONES_LPG_DETAIL_RESPONSE_NODES));
			response.append(parseMultiRowResponse("R", getNode(responseNode, "ajusteCredito"), "retenciones", "retencionReturn", RETENCIONES_LPG_DETAIL_RESPONSE_NODES));

			response.append(parseSingleRowResponse("AD", responseNode, "ajusteDebito", AJUSTAR_UNIFICADO_DEBITO_LPG_RESPONSE_NODES));
			response.append(parseMultiRowResponse("I", getNode(responseNode, "ajusteDebito"), "importes", "importeReturn", IMPORTES_LPG_DETAIL_RESPONSE_NODES));
			response.append(parseMultiRowResponse("D", getNode(responseNode, "ajusteDebito"), "deducciones", "deduccionReturn", DEDUCCIONES_LPG_DETAIL_RESPONSE_NODES));
			response.append(parseMultiRowResponse("R", getNode(responseNode, "ajusteDebito"), "retenciones", "retencionReturn", RETENCIONES_LPG_DETAIL_RESPONSE_NODES));
			
			response.append(parseSingleRowResponse("T", responseNode, AJUSTAR_UNIFICADO_LPG_RESPONSE_CONTAINER_NODE, AJUSTAR_UNIFICADO_LPG_TOTALES_RESPONSE_NODES));
			
			return response.toString();
		}		

		responseNode = getNode(doc, CONSULTAR_UNIFICADO_LPG_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + CONSULTAR_UNIFICADO_LPG_NODE + "] response");
			StringBuilder response = new StringBuilder();

			generateStatusAndMessages(response, responseNode);
			response.append(parseSingleRowResponse("H", responseNode, CONSULTAR_UNIFICADO_LPG_RESPONSE_CONTAINER_NODE, CONSULTAR_UNIFICADO_LPG_HEADER_RESPONSE_NODES));

			response.append(parseSingleRowResponse("AJ", responseNode, "ajusteCredito", CONSULTAR_UNIFICADO_CREDITO_LPG_RESPONSE_NODES));
			response.append(parseMultiRowResponse("I", getNode(responseNode, "ajusteCredito"), "importes", "importeReturn", IMPORTES_LPG_DETAIL_RESPONSE_NODES));
			response.append(parseMultiRowResponse("D", getNode(responseNode, "ajusteCredito"), "deducciones", "deduccionReturn", DEDUCCIONES_LPG_DETAIL_RESPONSE_NODES));
			response.append(parseMultiRowResponse("R", getNode(responseNode, "ajusteCredito"), "retenciones", "retencionReturn", RETENCIONES_LPG_DETAIL_RESPONSE_NODES));

			response.append(parseSingleRowResponse("AD", responseNode, "ajusteDebito", CONSULTAR_UNIFICADO_DEBITO_LPG_RESPONSE_NODES));
			response.append(parseMultiRowResponse("I", getNode(responseNode, "ajusteDebito"), "importes", "importeReturn", IMPORTES_LPG_DETAIL_RESPONSE_NODES));
			response.append(parseMultiRowResponse("D", getNode(responseNode, "ajusteDebito"), "deducciones", "deduccionReturn", DEDUCCIONES_LPG_DETAIL_RESPONSE_NODES));
			response.append(parseMultiRowResponse("R", getNode(responseNode, "ajusteDebito"), "retenciones", "retencionReturn", RETENCIONES_LPG_DETAIL_RESPONSE_NODES));
			
			response.append(parseSingleRowResponse("T", responseNode, CONSULTAR_UNIFICADO_LPG_RESPONSE_CONTAINER_NODE, CONSULTAR_UNIFICADO_LPG_TOTALES_RESPONSE_NODES));
			
			return response.toString();
		}
		
		responseNode = getNode(doc, AJUSTAR_CONTRATO_LPG_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + AJUSTAR_CONTRATO_LPG_NODE + "] response");
			StringBuilder response = new StringBuilder();

			generateStatusAndMessages(response, responseNode);
			response.append(parseSingleRowResponse("H", responseNode, AJUSTAR_CONTRATO_LPG_RESPONSE_CONTAINER_NODE, AJUSTAR_CONTRATO_LPG_HEADER_RESPONSE_NODES));

			response.append(parseSingleRowResponse("AC", responseNode, "ajusteCredito", AJUSTAR_CONTRATO_CREDITO_LPG_RESPONSE_NODES));
			response.append(parseMultiRowResponse("I", getNode(responseNode, "ajusteCredito"), "importes", "importeReturn", IMPORTES_LPG_DETAIL_RESPONSE_NODES));
			response.append(parseMultiRowResponse("D", getNode(responseNode, "ajusteCredito"), "deducciones", "deduccionReturn", DEDUCCIONES_LPG_DETAIL_RESPONSE_NODES));
			response.append(parseMultiRowResponse("R", getNode(responseNode, "ajusteCredito"), "retenciones", "retencionReturn", RETENCIONES_LPG_DETAIL_RESPONSE_NODES));

			response.append(parseSingleRowResponse("AD", responseNode, "ajusteDebito", AJUSTAR_CONTRATO_DEBITO_LPG_RESPONSE_NODES));
			response.append(parseMultiRowResponse("I", getNode(responseNode, "ajusteDebito"), "importes", "importeReturn", IMPORTES_LPG_DETAIL_RESPONSE_NODES));
			response.append(parseMultiRowResponse("D", getNode(responseNode, "ajusteDebito"), "deducciones", "deduccionReturn", DEDUCCIONES_LPG_DETAIL_RESPONSE_NODES));
			response.append(parseMultiRowResponse("R", getNode(responseNode, "ajusteDebito"), "retenciones", "retencionReturn", RETENCIONES_LPG_DETAIL_RESPONSE_NODES));
			
			response.append(parseSingleRowResponse("T", responseNode, AJUSTAR_CONTRATO_LPG_RESPONSE_CONTAINER_NODE, AJUSTAR_CONTRATO_LPG_TOTALES_RESPONSE_NODES));

			
			return response.toString();
		}		

		responseNode = getNode(doc, CONSULTAR_CONTRATO_LPG_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + CONSULTAR_CONTRATO_LPG_NODE + "] response");
			StringBuilder response = new StringBuilder();

			generateStatusAndMessages(response, responseNode);
			response.append(parseSingleRowResponse("H", responseNode, CONSULTAR_CONTRATO_LPG_RESPONSE_CONTAINER_NODE, CONSULTAR_CONTRATO_LPG_HEADER_RESPONSE_NODES));

			response.append(parseSingleRowResponse("AJ", responseNode, "ajusteCredito", CONSULTAR_CONTRATO_CREDITO_LPG_RESPONSE_NODES));
			response.append(parseMultiRowResponse("I", getNode(responseNode, "ajusteCredito"), "importes", "importeReturn", IMPORTES_LPG_DETAIL_RESPONSE_NODES));
			response.append(parseMultiRowResponse("D", getNode(responseNode, "ajusteCredito"), "deducciones", "deduccionReturn", DEDUCCIONES_LPG_DETAIL_RESPONSE_NODES));
			response.append(parseMultiRowResponse("R", getNode(responseNode, "ajusteCredito"), "retenciones", "retencionReturn", RETENCIONES_LPG_DETAIL_RESPONSE_NODES));

			response.append(parseSingleRowResponse("AD", responseNode, "ajusteDebito", CONSULTAR_CONTRATO_DEBITO_LPG_RESPONSE_NODES));
			response.append(parseMultiRowResponse("I", getNode(responseNode, "ajusteDebito"), "importes", "importeReturn", IMPORTES_LPG_DETAIL_RESPONSE_NODES));
			response.append(parseMultiRowResponse("D", getNode(responseNode, "ajusteDebito"), "deducciones", "deduccionReturn", DEDUCCIONES_LPG_DETAIL_RESPONSE_NODES));
			response.append(parseMultiRowResponse("R", getNode(responseNode, "ajusteDebito"), "retenciones", "retencionReturn", RETENCIONES_LPG_DETAIL_RESPONSE_NODES));
			
			response.append(parseSingleRowResponse("T", responseNode, CONSULTAR_CONTRATO_LPG_RESPONSE_CONTAINER_NODE, CONSULTAR_CONTRATO_LPG_TOTALES_RESPONSE_NODES));

			
			return response.toString();
		}		
		
		responseNode = getNode(doc, LIQUIDACION_LPG_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + LIQUIDACION_LPG_NODE + "] response");
			StringBuilder response = new StringBuilder();
			
			generateStatusAndMessages(response, responseNode);
			response.append(parseSingleRowResponse("H", responseNode, LIQUIDACION_LPG_RESPONSE_CONTAINER_NODE, LIQUIDACION_LPG_HEADER_RESPONSE_NODES));
			response.append(parseMultiRowResponse("D", getNode(responseNode, LIQUIDACION_LPG_RESPONSE_CONTAINER_NODE), "deducciones", "deduccionReturn", DEDUCCIONES_LPG_DETAIL_RESPONSE_NODES));
			response.append(parseMultiRowResponse("R", getNode(responseNode, LIQUIDACION_LPG_RESPONSE_CONTAINER_NODE), "retenciones", "retencionReturn", RETENCIONES_LPG_DETAIL_RESPONSE_NODES));
			response.append(parseSingleRowResponse("T", responseNode,LIQUIDACION_LPG_RESPONSE_CONTAINER_NODE, LIQUIDACION_LPG_TOTALES_RESPONSE_NODES));
			
			return response.toString();
		}		

		responseNode = getNode(doc, ANULACION_LPG_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + ANULACION_LPG_NODE + "] response");
			StringBuilder response = new StringBuilder();
			
			generateStatusAndMessages(response, responseNode);
			response.append(parseSingleRowResponse("H", responseNode, ANULACION_LPG_RESPONSE_CONTAINER_NODE, ANULACION_LPG_HEADER_RESPONSE_NODES));
			
			return response.toString();			
		}		

		responseNode = getNode(doc, CONSULTA_COE_LPG_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + CONSULTA_COE_LPG_NODE + "] response");
			StringBuilder response = new StringBuilder();
			
			generateStatusAndMessages(response, responseNode);
			response.append(parseSingleRowResponse("H", responseNode, "liquidacion", CONSULTA_COE_LPG_HEADER1_RESPONSE_NODES));
			response.append(parseMultiRowResponse("C", getNode(responseNode, "liquidacion"), "certificados", "certificado", CERTIFICACIONES_LPG_DETAIL_RESPONSE_NODES));
			response.append(parseMultiRowResponse("O", getNode(responseNode, "liquidacion"), "opcionales", "opcional", CODE_DESCRIPTION_DETAIL_RESPONSE_NODES));
			response.append(parseSingleRowResponse("H", responseNode, "autorizacion", CONSULTA_COE_LPG_HEADER2_RESPONSE_NODES));
			response.append(parseMultiRowResponse("D", getNode(responseNode, "autorizacion"), "deducciones", "deduccionReturn", DEDUCCIONES_LPG_DETAIL_RESPONSE_NODES));
			response.append(parseMultiRowResponse("R", getNode(responseNode, "autorizacion"), "retenciones", "retencionReturn", RETENCIONES_LPG_DETAIL_RESPONSE_NODES));
			response.append(parseSingleRowResponse("T", responseNode, "autorizacion", LIQUIDACION_LPG_TOTALES_RESPONSE_NODES));
			
			return response.toString();
		}		

		responseNode = getNode(doc, CONSULTA_NRO_ORDEN_LPG_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + CONSULTA_NRO_ORDEN_LPG_NODE + "] response");
			StringBuilder response = new StringBuilder();
			
			generateStatusAndMessages(response, responseNode);
			response.append(parseSingleRowResponse("H", responseNode, "liquidacion", CONSULTA_NRO_ORDEN_LPG_HEADER1_RESPONSE_NODES));
			response.append(parseMultiRowResponse("C", getNode(responseNode, "liquidacion"), "certificados", "certificado", CERTIFICACIONES_LPG_DETAIL_RESPONSE_NODES));
			response.append(parseMultiRowResponse("O", getNode(responseNode, "liquidacion"), "opcionales", "opcional", CODE_DESCRIPTION_DETAIL_RESPONSE_NODES));
			response.append(parseSingleRowResponse("H", responseNode, "autorizacion", CONSULTA_NRO_ORDEN_LPG_HEADER2_RESPONSE_NODES));
			response.append(parseMultiRowResponse("D", getNode(responseNode, "autorizacion"), "deducciones", "deduccionReturn", DEDUCCIONES_LPG_DETAIL_RESPONSE_NODES));
			response.append(parseMultiRowResponse("R", getNode(responseNode, "autorizacion"), "retenciones", "retencionReturn", RETENCIONES_LPG_DETAIL_RESPONSE_NODES));
			response.append(parseSingleRowResponse("T", responseNode, "autorizacion", LIQUIDACION_LPG_TOTALES_RESPONSE_NODES));
			
			return response.toString();		}		

		responseNode = getNode(doc, CONSULTA_ULTIMO_NRO_ORDEN_LPG_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + CONSULTA_ULTIMO_NRO_ORDEN_LPG_NODE + "] response");
			StringBuilder response = new StringBuilder();
			
			generateStatusAndMessages(response, responseNode);
			response.append(parseSingleRowResponse("H", responseNode, CONSULTA_ULTIMO_NRO_ORDEN_LPG_RESPONSE_CONTAINER_NODE, CONSULTA_ULTIMO_NRO_ORDEN_LPG_HEADER_RESPONSE_NODES));
			
			return response.toString();			
		}
		
		responseNode = getNode(doc, CAMPANIA_LPG_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + CAMPANIA_LPG_NODE + "] response");
			StringBuilder response = new StringBuilder();
			
			generateStatusAndMessages(response, responseNode);
			response.append(parseMultiRowResponse(null, responseNode, "campanias", "codigoDescripcion", CODE_DESCRIPTION_DETAIL_RESPONSE_NODES));
			
			return response.toString();			
		}			

		responseNode = getNode(doc, TIPO_GRANO_LPG_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + TIPO_GRANO_LPG_NODE + "] response");
			StringBuilder response = new StringBuilder();
			
			generateStatusAndMessages(response, responseNode);
			response.append(parseMultiRowResponse(null, responseNode, "granos", "codigoDescripcion", CODE_DESCRIPTION_DETAIL_RESPONSE_NODES));
			
			return response.toString();			
		}

		responseNode = getNode(doc, GRADO_REFERENCIA_LPG_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + GRADO_REFERENCIA_LPG_NODE + "] response");
			StringBuilder response = new StringBuilder();
			
			generateStatusAndMessages(response, responseNode);
			response.append(parseMultiRowResponse(null, responseNode, "gradosRef", "codigoDescripcion", CODE_DESCRIPTION_DETAIL_RESPONSE_NODES));
			
			return response.toString();			
		}

		responseNode = getNode(doc, GRADO_ENTREGADO_LPG_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + GRADO_ENTREGADO_LPG_NODE + "] response");
			StringBuilder response = new StringBuilder();
			
			generateStatusAndMessages(response, responseNode);
			response.append(parseMultiRowResponse(null, responseNode, "gradoEnt", "gradoEnt", GRADO_ENTREGADO_RESPONSE_NODES));
			
			return response.toString();			
		}

		responseNode = getNode(doc, TIPO_CERTIFICADO_DEPOSITO_LPG_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + TIPO_CERTIFICADO_DEPOSITO_LPG_NODE + "] response");
			StringBuilder response = new StringBuilder();
			
			generateStatusAndMessages(response, responseNode);
			response.append(parseMultiRowResponse(null, responseNode, "tiposCertDep", "codigoDescripcion", CODE_DESCRIPTION_DETAIL_RESPONSE_NODES));
			
			return response.toString();			
		}

		responseNode = getNode(doc, TIPO_DEDUCCION_LPG_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + TIPO_DEDUCCION_LPG_NODE + "] response");
			StringBuilder response = new StringBuilder();
			
			generateStatusAndMessages(response, responseNode);
			response.append(parseMultiRowResponse(null, responseNode, "tiposDeduccion", "codigoDescripcion", CODE_DESCRIPTION_DETAIL_RESPONSE_NODES));
			
			return response.toString();			
		}

		responseNode = getNode(doc, TIPO_RETENCION_LPG_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + TIPO_RETENCION_LPG_NODE + "] response");
			StringBuilder response = new StringBuilder();
			
			generateStatusAndMessages(response, responseNode);
			response.append(parseMultiRowResponse(null, responseNode, "tiposRetencion", "codigoDescripcion", CODE_DESCRIPTION_DETAIL_RESPONSE_NODES));
			
			return response.toString();			
		}
		
		responseNode = getNode(doc, PUERTOS_LPG_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + PUERTOS_LPG_NODE + "] response");
			StringBuilder response = new StringBuilder();
			
			generateStatusAndMessages(response, responseNode);
			response.append(parseMultiRowResponse(null, responseNode, "puertos", "codigoDescripcion", CODE_DESCRIPTION_DETAIL_RESPONSE_NODES));
			
			return response.toString();			
		}

		responseNode = getNode(doc, TIPO_ACTIVIDAD_LPG_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + TIPO_ACTIVIDAD_LPG_NODE + "] response");
			StringBuilder response = new StringBuilder();
			
			generateStatusAndMessages(response, responseNode);
			response.append(parseMultiRowResponse(null, responseNode, "tiposActividad", "codigoDescripcion", CODE_DESCRIPTION_DETAIL_RESPONSE_NODES));
			
			return response.toString();			
		}

		responseNode = getNode(doc, TIPO_ACTIVIDAD_REPRESENTADO_LPG_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + TIPO_ACTIVIDAD_REPRESENTADO_LPG_NODE + "] response");
			StringBuilder response = new StringBuilder();
			
			generateStatusAndMessages(response, responseNode);
			response.append(parseMultiRowResponse(null, responseNode, "tiposActividad", "codigoDescripcion", CODE_DESCRIPTION_DETAIL_RESPONSE_NODES));
			
			return response.toString();			
		}

		responseNode = getNode(doc, PROVINCIAS_LPG_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + PROVINCIAS_LPG_NODE + "] response");
			StringBuilder response = new StringBuilder();
			
			generateStatusAndMessages(response, responseNode);
			response.append(parseMultiRowResponse(null, responseNode, "provincias", "codigoDescripcion", CODE_DESCRIPTION_DETAIL_RESPONSE_NODES));
			
			return response.toString();			
		}

		responseNode = getNode(doc, LOCALIDADES_LPG_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + LOCALIDADES_LPG_NODE + "] response");
			StringBuilder response = new StringBuilder();
			
			generateStatusAndMessages(response, responseNode);
			response.append(parseMultiRowResponse(null, responseNode, "localidades", "codigoDescripcion", CODE_DESCRIPTION_DETAIL_RESPONSE_NODES));
			
			return response.toString();			
		}

		responseNode = getNode(doc, TIPO_OPERACION_LPG_NODE);
		if(responseNode != null) {
			LOG.debug("Parsing [" + TIPO_OPERACION_LPG_NODE + "] response");
			StringBuilder response = new StringBuilder();
			
			generateStatusAndMessages(response, responseNode);
			response.append(parseMultiRowResponse(null, responseNode, "tiposOperacion", "codigoDescripcion", CODE_DESCRIPTION_DETAIL_RESPONSE_NODES));
			
			return response.toString();			
		}
		
		return rawResponse;
	}

	private void generateStatusAndMessages(StringBuilder response, Node responseNode) throws Exception {
		String errors = parseErrorsResponse("E", getNode(responseNode, ERRORS_CONTAINER_NODE), ERROR_NODE, CODE_DESCRIPTION_DETAIL_RESPONSE_NODES);
		String formatErrors = parseErrorsResponse("F", getNode(responseNode, FORMAT_ERRORS_CONTAINER_NODE), ERROR_NODE, CODE_DESCRIPTION_DETAIL_RESPONSE_NODES);
		String events = parseEventsResponse("V", getNode(responseNode, EVENTS_CONTAINER_NODE), EVENT_NODE, CODE_DESCRIPTION_DETAIL_RESPONSE_NODES);
		
		if(errors != null || formatErrors != null) {
			response.append(ERROR_RESULT);
		} else {
			response.append(SUCCESS_RESULT);
		}
		
		if(errors != null) {
			response.append(errors);
		}
		
		if(formatErrors != null) {
			response.append(formatErrors);
		}

		if(events != null) {
			response.append(events);
		}		
	}
	
	private String parseErrorsResponse(String lineType, Node errorNodeContainer, String errorNodeName, String valueNodeNames[]) throws Exception {
		StringBuffer errorResult = new StringBuffer();

		boolean hasErrors = false;
		if (errorNodeContainer != null && errorNodeContainer.getChildNodes() != null) {
			Node n;
			for (int i = 0; i < errorNodeContainer.getChildNodes().getLength(); i++) {
				n = errorNodeContainer.getChildNodes().item(i);
				if (errorNodeName.equalsIgnoreCase(n.getNodeName())) {
					errorResult.append(RESULT_MESSAGE_SEPARATOR);
					if(lineType != null) {
						errorResult.append(lineType + LINE_TOKEN);
					}
					errorResult.append(buildResultRow(n, valueNodeNames));
					hasErrors = true;
				}
			}
		}
		
		return hasErrors ? errorResult.toString() : null;		
	}

	private String parseEventsResponse(String lineType, Node errorNodeContainer, String errorNodeName, String valueNodeNames[]) throws Exception {
		StringBuffer eventResult = new StringBuffer();

		boolean hasEvents = false;
		if (errorNodeContainer != null && errorNodeContainer.getChildNodes() != null) {
			Node n;
			for (int i = 0; i < errorNodeContainer.getChildNodes().getLength(); i++) {
				n = errorNodeContainer.getChildNodes().item(i);
				if (errorNodeName.equalsIgnoreCase(n.getNodeName())) {
					eventResult.append(RESULT_MESSAGE_SEPARATOR);
					if(lineType != null) {
						eventResult.append(lineType + LINE_TOKEN);
					}
					eventResult.append(buildResultRow(n, valueNodeNames));
					hasEvents = true;
				}
			}
		}
		
		return hasEvents ? eventResult.toString() : null;		
		
	}
	
	/**
	 * 
	 * @param responseNode
	 * @param mainNodeName
	 * @param responseNodes
	 * @return
	 * @throws Exception
	 */
	private String parseSingleRowResponse(String lineType, Node responseNode, String responseContainerNodeName, String responseNodes[]) throws Exception {
		StringBuffer response = new StringBuffer();
		Node responseDataNode = getNode(responseNode, responseContainerNodeName);

		if(responseDataNode != null) {
			response.append(RESULT_MESSAGE_SEPARATOR);
			if(lineType != null) {
				response.append(lineType + LINE_TOKEN);
			}
			response.append(buildResultRow(responseDataNode, responseNodes));
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
	private String parseMultiRowResponse(String lineType, Node responseNode, String rowsContainerNodeName, String rowNodeName, String rowNodes[]) throws Exception {
		StringBuffer response = new StringBuffer();
		Node responseDataNode = getNode(responseNode, rowsContainerNodeName);

		if (responseDataNode != null && responseDataNode.getChildNodes() != null) {
			Node n;
			for (int i = 0; i < responseDataNode.getChildNodes().getLength(); i++) {
				n = responseDataNode.getChildNodes().item(i);
				if (rowNodeName.equalsIgnoreCase(n.getNodeName())) {
					response.append(RESULT_MESSAGE_SEPARATOR);
					if(lineType != null) {
						response.append(lineType + LINE_TOKEN);
					}
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
			AfipLPGResponseParser r = new AfipLPGResponseParser();
			System.out.println(r.parseResponse(FileUtils.readFile("C:\\file.txt"), null));
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
