/*
 * File name: TransporteBienesOfflineInputRetriever.java
 * Creation date: 29/10/2011 08:32:09
 * Copyright Mindpool
 */
package ar.com.bunge.sapws.client.offline;

import java.util.Map;

import org.apache.log4j.Logger;

import ar.com.bunge.sapws.client.ClientXmlResponse;
import ar.com.bunge.util.FileUtils;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since 1.0
 *
 */
public class TransporteBienesOfflineInputRetriever implements OfflineInputRetriever {
	private static final Logger LOG = Logger.getLogger(TransporteBienesOfflineInputRetriever.class);
	
	private static final String FILE_ID_PARAM = "num";
	private static final String INPUT_FILES_DIRECTORY = "dir";
	
	private static final String PREFIXES[] = {"AR", "BR", "CR"};
	private String prefix;
	
	/**
	 * 
	 */
	public TransporteBienesOfflineInputRetriever() {
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 * @see ar.com.bunge.sapws.client.offline.OfflineInputRetriever#retrieveInput(java.util.Map)
	 */
	public ClientXmlResponse retrieveInput(Map<String, Object> context) throws Exception {
		String dir = context.containsKey(INPUT_FILES_DIRECTORY) ? context.get(INPUT_FILES_DIRECTORY).toString() : null;
		String num = context.containsKey(FILE_ID_PARAM) ? context.get(FILE_ID_PARAM).toString() : null;
		
		ClientXmlResponse response = new ClientXmlResponse();
		
		if(dir == null || num == null) {
			response.setNumber(new Long(-100));
			response.setMessage("No se puede determinar el archivo de entrada. Por favor provea valores para los argumentos [" + INPUT_FILES_DIRECTORY + "] y/o [" + FILE_ID_PARAM + "]");
			LOG.warn("At least one of the required arguments [" + INPUT_FILES_DIRECTORY + "] and/or [" + FILE_ID_PARAM + "] is missing");
		} else {
			String fileName;
			boolean found = false;
			for(int i=0; !found && i < PREFIXES.length; i++) {
				fileName = FileUtils.buildFileName(dir, PREFIXES[i] + num + ".xml");
				LOG.debug("Checking file [" + fileName + "]");
				if(FileUtils.existsFile(fileName)) {
					response.setNumber(new Long(0));
					response.setResponse(FileUtils.readFile(fileName));
					found = true;
					setPrefix(PREFIXES[i]);
					LOG.debug("Found file [" + fileName + "]");
				}
			}
			
			if(!found) {
				response.setNumber(new Long(-200));
				response.setMessage("No se puede encontrar archivo de entrada en el directorio [" + dir + "] para el numero [" + num + "].");
				LOG.warn("No input file found in the directory [" + dir + "] for number [" + num + "].");
			}
		}
		
		return response;
	}

	/**
	 * 
	 * @return
	 * @see ar.com.bunge.sapws.client.offline.OfflineInputRetriever#changeResponseFile()
	 */
	public boolean changeResponseFile() {
		return true;
	}

	/**
	 * 
	 * @param cmdLineResponseFile
	 * @param context
	 * @return
	 * @see ar.com.bunge.sapws.client.offline.OfflineInputRetriever#getResponseFile(java.lang.String, java.util.Map)
	 */
	public String getResponseFile(String cmdLineResponseFile, Map<String, Object> context) throws Exception {
		String fileName = null;
		
		if(context.containsKey(FILE_ID_PARAM)) {
			fileName = getPrefix() + context.get(FILE_ID_PARAM).toString() + ".txt";
		} else {
			LOG.warn("No value for argument [" + FILE_ID_PARAM + "] in context.");
			throw new Exception("Cannot build response file. No value for argument [" + FILE_ID_PARAM + "] in context.");
		}
		return FileUtils.buildFileName(cmdLineResponseFile, fileName);
	}

}
