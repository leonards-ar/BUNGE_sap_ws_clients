/*
 * File name: DeclaracionJuradaLaPampaConverter.java
 * Creation date: 26/08/2012 12:21:00
 * Copyright Mindpool
 */
package ar.com.bunge.sapws.converter;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import ar.com.bunge.util.FileUtils;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since 1.0
 *
 */
public class DeclaracionJuradaLaPampaConverter implements FileConverter {
	private static final Logger LOG = Logger.getLogger(DeclaracionJuradaLaPampaConverter.class);

	private static int[] CABECERA = {1, 2, 10, 10, 6, 6, 2, 5};
	private static int CABECERA_LENGTH = addRecordLengths(CABECERA);
	private static int[] DETALLE = {10, 12, 11, 11, 11, 5, 5, 3, 7, 5, 6, 20, 8, 6, 3};
	private static int DETALLE_LENGTH = addRecordLengths(DETALLE);
	private static int[] DETALLE_VAGON = {10, 6, 6};
	private static int DETALLE_VAGON_LENGTH = addRecordLengths(DETALLE_VAGON);

	private static String T = "  ";
	private static String TT = T + T;
	private static String TTT = TT + T;
	private static String TTTT = TTT + T;
	private static String TTTTT = TTTT + T;
	
	private static String NL = FileUtils.getNewLine();
	/**
	 * 
	 */
	public DeclaracionJuradaLaPampaConverter() {
	}

	/**
	 * @param inputFile
	 * @param context
	 * @return
	 * @throws Exception
	 * @see ar.com.bunge.sapws.converter.FileConverter#convert(java.lang.String, java.util.Map)
	 */
	@Override
	public String convert(String inputFile, Map<String, Object> context) throws Exception {
		StringBuilder sb = new StringBuilder();
		List<String> lines = FileUtils.readFileLines(inputFile);
		
		sb.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" + NL);
		sb.append("<!DOCTYPE DDJJ SYSTEM \"http://www.dgr.lapampa.gov.ar/consultas/DTD_Agentes_Informacion.dtd\">" + NL);
		sb.append("<DDJJ>" + NL);
		
		String line;
		for(int lineNumber = 0; lineNumber < lines.size(); lineNumber++ ) {
			line = lines.get(lineNumber);
			
			if(isHeaderLine(line)) {
				lineNumber = processHeader(line, lines, lineNumber, sb);
			} else if(!isEmptyLine(line)) {
				LOG.warn("Ignoring unexpected line " + (lineNumber + 1) + " looking for HEADER: " + line);
			} else {
				LOG.debug("Ignoring line " + (lineNumber + 1) + " as it is empty.");
			}
		}
		
		sb.append("</DDJJ>" + NL);
		sb.append("<?xmlend?>");
		
		return sb.toString();
	}
	
	private int processHeader(String headerLine, List<String> lines, int lineNumber, StringBuilder sb) {
		List<String> records = FileUtils.parseFixedLengthLine(headerLine, CABECERA);
		
		sb.append(T + toIntegerXMLAttribute("Programa", records.get(0)) + NL);
		sb.append(T + toIntegerXMLAttribute("TipoCta", records.get(1)) + NL);
		sb.append(T + toStringXMLAttribute("Version", records.get(2)) + NL);
		sb.append(T + toStringXMLAttribute("Fecha", records.get(3)) + NL);
		sb.append(T + toIntegerXMLAttribute("NroAgente", records.get(4)) + NL);
		sb.append(T + toStringXMLAttribute("Periodo", records.get(5)) + NL);
		sb.append(T + toIntegerXMLAttribute("NroRec", records.get(6)) + NL);
		sb.append(T + toIntegerXMLAttribute("RegInformados", records.get(7)) + NL);
		
		sb.append(T + "<Operaciones>" + NL);
		
		String line;
		for(lineNumber += 1; lineNumber < lines.size(); lineNumber++) {
			line = lines.get(lineNumber);
			
			if(isDetailLine(line)) {
				lineNumber = processDetail(line, lines, lineNumber, sb);
			} else if(isHeaderLine(line)) {
				lineNumber--;
				break;
			} else if(!isEmptyLine(line)) {
				LOG.warn("Ignoring unexpected line " + (lineNumber + 1) + " looking for DETAIL: " + line);
			} else {
				LOG.debug("Ignoring line " + (lineNumber + 1) + " as it is empty.");
			}			
		}
		
		sb.append(T + "</Operaciones>" + NL);
		
		return lineNumber;
	}

	private int processDetail(String detailLine, List<String> lines, int lineNumber, StringBuilder sb) {
		List<String> records = FileUtils.parseFixedLengthLine(detailLine, DETALLE);
		
		sb.append(TT + "<Detalle>" + NL);

		sb.append(TTT + toStringXMLAttribute("FechaOperacion", records.get(0)) + NL);
		sb.append(TTT + toStringXMLAttribute("NroCartaPorte", records.get(1)) + NL);
		sb.append(TTT + toStringXMLAttribute("CuitTitular", records.get(2)) + NL);
		sb.append(TTT + toStringXMLAttribute("CuitDestinatario", records.get(3)) + NL);
		sb.append(TTT + toStringXMLAttribute("CuitTransportista", records.get(4)) + NL);
		sb.append(TTT + toStringXMLAttribute("Procedencia", records.get(5)) + NL);
		sb.append(TTT + toStringXMLAttribute("Destino", records.get(6)) + NL);
		sb.append(TTT + toStringXMLAttribute("Grano", records.get(7)) + NL);
		sb.append(TTT + toStringXMLAttribute("KgTransportados", records.get(8)) + NL);
		sb.append(TTT + toStringXMLAttribute("KmaRecorrer", records.get(9)) + NL);
		sb.append(TTT + toStringXMLAttribute("Tarifa", records.get(10)) + NL);
		sb.append(TTT + toStringXMLAttribute("NroContrato", records.get(11)) + NL);
		sb.append(TTT + toStringXMLAttribute("NroOblea", records.get(12)) + NL);
		sb.append(TTT + toStringXMLAttribute("NroGuia", records.get(13)) + NL);
		sb.append(TTT + toIntegerXMLAttribute("NroVagones", records.get(14)) + NL);

		String line;
		boolean hasWagons = false;
		boolean closeWagons = false;
		
		for(lineNumber += 1; lineNumber < lines.size(); lineNumber++) {
			line = lines.get(lineNumber);
			
			if(isWagonDetailLine(line)) {
				if(!hasWagons) {
					hasWagons = true;
					closeWagons = true;
					sb.append(TTT + "<Vagones>" + NL);
				}
				lineNumber = processWagonDetail(line, lines, lineNumber, sb);
			} else if(isDetailLine(line) || isHeaderLine(line)) {
				lineNumber--;
				break;
			} else if(!isEmptyLine(line)) {
				LOG.warn("Ignoring unexpected line " + (lineNumber + 1) + " looking for WAGON DETAIL: " + line);
			} else {
				LOG.debug("Ignoring line " + (lineNumber + 1) + " as it is empty.");
			}			
		}

		if(closeWagons) {
			sb.append(TTT + "</Vagones>" + NL);
		}
		sb.append(TT + "</Detalle>" + NL);
		
		return lineNumber;
	}

	private int processWagonDetail(String wagonDetailLine, List<String> lines, int lineNumber, StringBuilder sb) {
		List<String> records = FileUtils.parseFixedLengthLine(wagonDetailLine, DETALLE_VAGON);
		
		sb.append(TTTT + "<Vagon>" + NL);

		sb.append(TTTTT + toStringXMLAttribute("NroVagon", records.get(0)) + NL);
		sb.append(TTTTT + toStringXMLAttribute("KgTransportadosVagon", records.get(1)) + NL);
		sb.append(TTTTT + toStringXMLAttribute("NroGuiaVagon", records.get(2)) + NL);
		
		sb.append(TTTT + "</Vagon>" + NL);
		
		return lineNumber;
	}
	
	private String toStringXMLAttribute(String attributeName, String value) {
		return "<" + attributeName + ">" + (value != null ? value.trim() : "") + "</" + attributeName + ">";
	}

	private String toIntegerXMLAttribute(String attributeName, String value) {
		return "<" + attributeName + ">" + (value != null ? Long.parseLong(value) : "") + "</" + attributeName + ">";
	}
	
	private boolean isHeaderLine(String line) {
		return line != null && CABECERA_LENGTH == line.length();
	}

	private boolean isDetailLine(String line) {
		return line != null && DETALLE_LENGTH == line.length();
	}

	private boolean isWagonDetailLine(String line) {
		return line != null && DETALLE_VAGON_LENGTH == line.length();
	}
	
	private boolean isEmptyLine(String line) {
		return line == null || line.length() <= 0;
	}
	
	private static int addRecordLengths(int lengths[]) {
		int total = 0;
		for(int i : lengths) {
			total += i;
		}
		return total;
	}

	public static void main(String a[]) throws Exception {
		DeclaracionJuradaLaPampaConverter c = new DeclaracionJuradaLaPampaConverter();
		
		System.out.println(c.convert("D:\\Development\\eclipse\\workspace\\sap-ws-clients\\src\\main\\examples\\DDJJ-LaPampa-Ejemplo.txt", null));
		
	}
}
