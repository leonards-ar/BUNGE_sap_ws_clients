/*
 * File name: AFIPUtils.java
 * Creation date: 19/06/2010 09:35:35
 * Copyright Mindpool
 */
package ar.com.bunge.util;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertStore;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import ar.com.bunge.sapws.client.ClientXmlRequest;
import ar.com.bunge.sapws.client.ClientXmlResponse;
import ar.com.bunge.sapws.client.SAPWSClient;
import ar.com.bunge.sapws.client.parser.AfipWSAAResponseParser;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since 1.0
 *
 */
public class AFIPUtils {
	private static final Logger LOG = Logger.getLogger(AFIPUtils.class);
	
	private static final String WSAA_TICKET_TEMP_FILE = "afip_wsaa.tmp";

	private static final String WSAA_REQ_TEMPLATE_PATH_PARAM = "i";
	private static final String WSAA_REQUEST_PARAM_TEMPLATE_PATH_PARAM = "wsaa_i";
	private static final String WSAA_TICKET_EXP_TIME_PARAM = "wsaa_ticket_expiration_time";
	private static final String WSAA_TICKET_EXP_THRESHOLD_PARAM = "wsaa_ticket_expiration_threshold";
	private static final String WSAA_TICKET_TEMP_DIR_PARAM = "wsaa_ticket_temp_dir";
	private static final String WSAA_ENDPOINT_PARAM = "url";
	private static final String WSAA_SERVICE_PARAM = "wsaa_service";
	private static final String WSAA_SRC_DN_PARAM = "wsaa_source_dn";
	private static final String WSAA_DEST_DN_PARAM = "wsaa_destination_dn";
	private static final String WSAA_UNIQUE_ID_PARAM = "wsaa_unique_id";
	private static final String WSAA_KEYSTORE_PATH_PARAM = "wsaa_keystore_path";
	private static final String WSAA_KEYSTORE_SIGNER_PARAM = "wsaa_keystore_signer";
	private static final String WSAA_KEYSTORE_PASSWORD_PARAM = "wsaa_keystore_password";
	private static final String WSAA_TICKET_GEN_TIME_PARAM = "wsaa_ticket_generation_time";
	

	private static final String[] WSAA_REQUIRED_PARAMS = new String[] {WSAA_REQ_TEMPLATE_PATH_PARAM, WSAA_REQUEST_PARAM_TEMPLATE_PATH_PARAM, WSAA_ENDPOINT_PARAM, WSAA_SERVICE_PARAM, WSAA_DEST_DN_PARAM, WSAA_KEYSTORE_PATH_PARAM, WSAA_KEYSTORE_PATH_PARAM, WSAA_KEYSTORE_SIGNER_PARAM, WSAA_KEYSTORE_PASSWORD_PARAM};

	// In minutes
	private static final long DEFAULT_WSAA_TICKET_EXP_TIME = 12 * 60; // 12 hours
	private static final long DEFAULT_TICKET_EXP_THRESHOLD = 5; // 5 minutes

	/**
	 * 
	 */
	public AFIPUtils() {
	}

	/**
	 * 
	 * @param configFilePath
	 * @return
	 * @throws Exception
	 */
	public static String getWSAAToken(String configFilePath, String forceNewTicket) throws Exception {
		Map<String, Object> wsaaValues = getWSAAValues(configFilePath, getForceNewTicket(forceNewTicket));
		
		return getMapValue(wsaaValues, AfipWSAAResponseParser.TICKET_TOKEN_KEY);
	}

	/**
	 * 
	 * @param configFilePath
	 * @return
	 * @throws Exception
	 */
	public static String getWSAASign(String configFilePath, String forceNewTicket) throws Exception {
		// The sign has to match the token, so for the moment ignore the forceNewTicket flag
		Map<String, Object> wsaaValues = getWSAAValues(configFilePath, false);
		
		return getMapValue(wsaaValues, AfipWSAAResponseParser.TICKET_SIGN_KEY);
	}	
	
	/**
	 * 
	 * @param configFilePath
	 * @return
	 * @throws Exception
	 */
	public static String getWSAASign(String configFilePath) throws Exception {
		return getWSAASign(configFilePath, "false");
	}

	/**
	 * 
	 * @param configFilePath
	 * @return
	 * @throws Exception
	 */
	public static String getWSAAToken(String configFilePath) throws Exception {
		return getWSAAToken(configFilePath, "false");
	}
	
	/**
	 * 
	 * @param forceNewTicket
	 * @return
	 */
	private static boolean getForceNewTicket(String forceNewTicket) {
		Boolean b = Utils.stringToBoolean(forceNewTicket);
		return b != null ? b.booleanValue() : false;
	}
	
	/**
	 * 
	 * @return
	 */
	private static String getConfigTempFileName() {
		String user = System.getProperty("user.name");
		return (user != null ? user : "all") + "_" + WSAA_TICKET_TEMP_FILE;
	}
	
	/**
	 * 
	 * @param configFilePath
	 * @return
	 * @throws Exception
	 */
	private static Map<String, Object> getWSAAValues(String configFilePath, boolean forceNewTicket) throws Exception {
		Map<String, Object> config = getWSAAConfiguration(configFilePath);
		String ticketTempFilename = FileUtils.buildFileName(getMapValue(config, WSAA_TICKET_TEMP_DIR_PARAM), getConfigTempFileName());
		if(!forceNewTicket && FileUtils.existsFile(ticketTempFilename)) {
			Map<String, Object> wsaaValues = FileUtils.parseKeyValueFile(ticketTempFilename);
			
			if(!isWSAATicketValid(config, wsaaValues)) {
				wsaaValues = generateWSAATicket(config, ticketTempFilename);
			}
			
			return wsaaValues;
		} else {
			return generateWSAATicket(config, ticketTempFilename);
		}
	}
	
	/**
	 * 
	 * @param config
	 * @param wsaaValues
	 * @return
	 */
	private static boolean isWSAATicketValid(Map<String, Object> config, Map<String, Object> wsaaValues) {
		Date expirationDate = parseDate(getMapValue(wsaaValues, AfipWSAAResponseParser.TICKET_EXPIRATION_TIME_KEY));
		if(expirationDate != null) {
			return System.currentTimeMillis() + getMapMinuteValueAsMillis(config, WSAA_TICKET_EXP_THRESHOLD_PARAM, DEFAULT_TICKET_EXP_THRESHOLD) < expirationDate.getTime();
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param dateValue
	 * @return
	 */
	private static Date parseDate(String dateValue) {
		try {
			return new Date(Long.parseLong(dateValue));
		} catch(Exception ex) {
			return null;
		}
	}
	
	/**
	 * 
	 * @param config
	 * @param ticketTempFilename
	 * @return
	 * @throws Exception
	 */
	private static Map<String, Object> generateWSAATicket(Map<String, Object> config, String ticketTempFilename) throws Exception {
		Map<String, Object> wsaaValues = doWSAALogin(config);
		
		FileUtils.writeKeyValueFile(wsaaValues, ticketTempFilename);
		
		return wsaaValues;
	}
	
	/**
	 * 
	 * @param config
	 * @return
	 * @throws Exception
	 */
	private static Map<String, Object> doWSAALogin(Map<String, Object> config) throws Exception {
		SAPWSClient client = getWSClient(config);
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("request", buildWSAARequestParameter(config));
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("About to execute WSAALogin with context: " + context);
		}
		ClientXmlResponse response = client.execute(context);

		return FileUtils.parseKeyValueString(client.getResponseParser().parseResponse(response.getResponse(), context));
	}

	/**
	 * 
	 * @param config
	 * @return
	 * @throws Exception
	 */
	private static SAPWSClient getWSClient(Map<String, Object> config) throws Exception {
		SAPWSClient client = new SAPWSClient();
		client.setUsername(getMapValue(config, "u"));
		client.setUrl(getMapValue(config, WSAA_ENDPOINT_PARAM));
		client.setPassword(getMapValue(config, "p"));
		client.setRequestTemplateFile(getMapValue(config, WSAA_REQ_TEMPLATE_PATH_PARAM));
		String auth = getMapValue(config, "b");
		client.setBasicAuthentication(auth != null ? "true".equalsIgnoreCase(auth) || "yes".equalsIgnoreCase(auth) : true);
		auth = getMapValue(config, "w");
		client.setWssAuthentication(auth != null ? "true".equalsIgnoreCase(auth) || "yes".equalsIgnoreCase(auth) : false);
		auth = getMapValue(config, "ssl");
		client.setSslAuthentication(auth != null ? "true".equalsIgnoreCase(auth) || "yes".equalsIgnoreCase(auth) : false);
		client.setKeyStore(getMapValue(config, "ks"));
		client.setKeyStorePassword(getMapValue(config, "ksp"));
		client.setProxyServer(getMapValue(config, "px"));
		client.setProxyPort(getMapValue(config, "pxp") != null ? Integer.parseInt(getMapValue(config, "pxp")) : 8080);
		client.setTracePath(getMapValue(config, "td"));
		client.setTracePrefix(getMapValue(config, "tp"));

		// Force expected response parser
		client.setResponseParser(new AfipWSAAResponseParser());

		return client;
	}
	
	/**
	 * 
	 * @param config
	 * @return
	 * @throws Exception
	 */
	public static String buildWSAARequestParameter(Map<String, Object> config) throws Exception {
		PrivateKey privateKey = null;
		X509Certificate privateCert = null;
		CertStore certStore = null;
		String signerDN = null;
		FileInputStream ksFile = null;
		String keyStorePath = getMapValue(config, WSAA_KEYSTORE_PATH_PARAM);
		String keyStorePass = getMapValue(config, WSAA_KEYSTORE_PASSWORD_PARAM, "");
		String signer = getMapValue(config, WSAA_KEYSTORE_SIGNER_PARAM);
		
		// For this to work you need to have the email appender enabled for CertificateUtils class
		//CertificateUtils.validateCertificateExpiration(keyStorePath, keyStorePass, signer);
		
		//
		// Manage Keys & Certificates
		//
		// Create a keystore using keys from the pkcs#12 p12file
		try {
			KeyStore ks = KeyStore.getInstance("pkcs12");
			ksFile = new FileInputStream(keyStorePath) ;
			ks.load(ksFile, keyStorePass.toCharArray());
	
			// Get Certificate & Private key from KeyStore
			privateKey = (PrivateKey) ks.getKey(signer, keyStorePass.toCharArray());
			privateCert = (X509Certificate) ks.getCertificate(signer);
			if(privateCert != null) {
				signerDN = privateCert.getSubjectDN().toString();
				
				// Create a list of Certificates to include in the final CMS
				List<X509Certificate> certList = new ArrayList<X509Certificate>();
				certList.add(privateCert);
		
				if(Security.getProvider("BC") == null) {
					Security.addProvider(new BouncyCastleProvider());
				}
		
				certStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(certList), "BC");
			} else {
				LOG.error("Invalid certificate or configuration. No certificate found in [" + keyStorePath + "] with alias [" + signer + "]");
				throw new Exception("Invalid certificate or configuration. No certificate found in [" + keyStorePath + "] with alias [" + signer + "]");
			}
			
		} finally {
			if(ksFile != null) {
				try {
					ksFile.close();
				} catch(Exception ex) {
					LOG.warn("Could not close keyStore file", ex);
				}
			}
		}
		
		String xmlRequestParam = buildXMLRequestParameter(config, signerDN);
		if(LOG.isDebugEnabled()) {
			LOG.debug("WSAA request XML: " + xmlRequestParam);
		}
		
		//
		// Create CMS Message
		//
		// Create a new empty CMS Message
		CMSSignedDataGenerator gen = new CMSSignedDataGenerator();

		// Add a Signer to the Message
		gen.addSigner(privateKey, privateCert, CMSSignedDataGenerator.DIGEST_SHA1);

		// Add the Certificate to the Message
  		gen.addCertificatesAndCRLs(certStore);

		// Add the data (XML) to the Message
		CMSProcessable data = new CMSProcessableByteArray(xmlRequestParam.getBytes());

		// Add a Sign of the Data to the Message
		CMSSignedData signed = gen.generate(data, true, "BC");	

		Base64 b64 = new Base64();
		String request = new String(b64.encode(signed.getEncoded()));

		if(LOG.isDebugEnabled()) {
			LOG.debug("WSAA encoded request: [" + request + "]");
		}
		
		
		return request;
	}
	
	/**
	 * 
	 * @param config
	 * @return
	 * @throws Exception
	 */
	private static String buildXMLRequestParameter(Map<String, Object> config, String signerDN) throws Exception {
		Map<String, Object> context = new HashMap<String, Object>();
		ClientXmlRequest request = new ClientXmlRequest(FileUtils.readFile(getMapValue(config, WSAA_REQUEST_PARAM_TEMPLATE_PATH_PARAM)));
		
		context.put(WSAA_SRC_DN_PARAM, signerDN);
		context.put(WSAA_DEST_DN_PARAM, config.get(WSAA_DEST_DN_PARAM));
		context.put(WSAA_UNIQUE_ID_PARAM, String.valueOf(System.currentTimeMillis() / 1000L));
		context.put(WSAA_TICKET_GEN_TIME_PARAM, Utils.dateToIsoString(new Date()));
		context.put(WSAA_TICKET_EXP_TIME_PARAM, Utils.dateToIsoString(new Date(System.currentTimeMillis() + getMapMinuteValueAsMillis(config, WSAA_TICKET_EXP_TIME_PARAM, DEFAULT_WSAA_TICKET_EXP_TIME))));
		context.put(WSAA_SERVICE_PARAM, config.get(WSAA_SERVICE_PARAM));

		request.compile(context);
		
		return request.getRequest();
	}
	
	
	
	/**
	 * 
	 * @param configFilePath
	 * @return
	 * @throws Exception
	 */
	private static Map<String, Object> getWSAAConfiguration(String configFilePath) throws Exception {
		if(FileUtils.existsFile(configFilePath)) {
			Map<String, Object> config = FileUtils.parseKeyValueFile(configFilePath);
			Object value;
			for(int i=0; i < WSAA_REQUIRED_PARAMS.length; i++) {
				value = config.get(WSAA_REQUIRED_PARAMS[i]);
				if(value == null || StringUtils.isEmpty(value.toString())) {
					throw new Exception("Configuration file [" + configFilePath + "] is missing a value for parameter [" + WSAA_REQUIRED_PARAMS[i] + "]");
				}
			}
			return config;
		} else {
			throw new Exception("Cannot load configuration from file [" + configFilePath + "]. File does not exist");
		}
	}

	/**
	 * 
	 * @param map
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	private static String getMapValue(Map<String, Object> map, String key, String defaultValue) {
		if(map != null) {
			Object value = map.get(key);
			return value != null && !StringUtils.isEmpty(value.toString()) ? value.toString() : defaultValue;
		} else {
			return defaultValue;
		}
	}
	
	/**
	 * 
	 * @param map
	 * @param key
	 * @return
	 */
	private static String getMapValue(Map<String, Object> map, String key) {
		return getMapValue(map, key, null);
	}
	
	/**
	 * 
	 * @param map
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	private static long getMapMinuteValueAsMillis(Map<String, Object> map, String key, long defaultValue) {
		String value = getMapValue(map, key);
		return (value != null ? Long.parseLong(value) : defaultValue) * 60000L;
	}
}
