/*
 * File name: SAPWSClient.java
 * Creation date: 01/08/2009 08:58:27
 * Copyright Mindpool
 */
package ar.com.bunge.sapws.client;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.ssl.HttpSecureProtocol;
import org.apache.commons.ssl.KeyMaterial;
import org.apache.commons.ssl.TrustMaterial;
import org.apache.log4j.Logger;
import org.springframework.ws.client.core.SourceExtractor;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.CommonsHttpMessageSender;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ar.com.bunge.sapws.client.offline.OfflineInputRetriever;
import ar.com.bunge.sapws.client.parser.ResponseParser;
import ar.com.bunge.sapws.converter.FileConverter;
import ar.com.bunge.util.BypassProtocolSocketFactory;
import ar.com.bunge.util.CertificateUtils;
import ar.com.bunge.util.FileUtils;
import ar.com.bunge.util.ValidationException;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since 1.0
 *
 */
public class SAPWSClient {
	private static final Logger LOG = Logger.getLogger(SAPWSClient.class);
	private static final String TRACE_REQUEST_SUFFIX = "req.xml";
	private static final String TRACE_RESPONSE_SUFFIX = "resp.xml";
	private static final String VARIABLE_SEPARATOR = "#";
	
	private String username;
	private String password;
	private String url;
	private String requestTemplateFile;
	private String responseFile;
	private String variablesFile;
	private boolean basicAuthentication;
	private boolean sslAuthentication;
	private boolean wssAuthentication;
	private String keyStore;
	private String keyStorePassword;
	private String proxyServer;
	private int proxyPort;
	private ResponseParser responseParser = null;
	private OfflineInputRetriever inputRetriever = null;
	private String tracePath;
	private String tracePrefix = null;
	private String messageFactoryImplementationClass = null;
	private boolean asynchronousResult = false;
	private boolean ignoreSSLValidation = false;
	private String soapAction = null;
	
	/**
	 * @return the tracePath
	 */
	public String getTracePath() {
		return tracePath;
	}

	/**
	 * @param tracePath the tracePath to set
	 */
	public void setTracePath(String tracePath) {
		this.tracePath = tracePath;
	}

	/**
	 * @return the tracePrefix
	 */
	public String getTracePrefix() {
		return tracePrefix;
	}
	
	/**
	 * 
	 * @param context
	 */
	private void replaceParameterVariables(Map<String, Object> context) {
		setUrl(replaceVariablesInParameter(getUrl(), context));
		setUsername(replaceVariablesInParameter(getUsername(), context));
		setPassword(replaceVariablesInParameter(getPassword(), context));
		setRequestTemplateFile(replaceVariablesInParameter(getRequestTemplateFile(), context));
		setResponseFile(replaceVariablesInParameter(getResponseFile(), context));
		setVariablesFile(replaceVariablesInParameter(getVariablesFile(), context));
		setKeyStore(replaceVariablesInParameter(getKeyStore(), context));
		setKeyStorePassword(replaceVariablesInParameter(getKeyStorePassword(), context));
		setProxyServer(replaceVariablesInParameter(getProxyServer(), context));
		setTracePath(replaceVariablesInParameter(getTracePath(), context));
		setTracePrefix(replaceVariablesInParameter(getTracePrefix(), context));
		setMessageFactoryImplementationClass(replaceVariablesInParameter(getMessageFactoryImplementationClass(), context));		
	}
	
	/**
	 * 
	 * @param parameterValue
	 * @param context
	 * @return
	 */
	private String replaceVariablesInParameter(String parameterValue, Map<String, Object> context) {
		if(parameterValue != null && parameterValue.trim().indexOf(VARIABLE_SEPARATOR) >= 0) {
			String vars[] = StringUtils.substringsBetween(parameterValue, VARIABLE_SEPARATOR, VARIABLE_SEPARATOR);
			Object value;
			String replacedParameter = new String(parameterValue);
			
			if(vars != null) {
				for(int i=0; i < vars.length; i++) {
					value = context.get(vars[i] != null ? vars[i].toLowerCase() : vars[i]);
					if(value == null) {
						value = "";
					}
					replacedParameter = StringUtils.replace(replacedParameter, VARIABLE_SEPARATOR + vars[i] + VARIABLE_SEPARATOR, String.valueOf(value));
				}
			}
			return replacedParameter;
		} else {
			return parameterValue;
		}
	}
	
	/**
	 * @param tracePrefix the tracePrefix to set
	 */
	public void setTracePrefix(String tracePrefix) {
		this.tracePrefix = tracePrefix;
	}

	/**
	 * 
	 */
	public SAPWSClient() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SAPWSClient client = new SAPWSClient();
		try {
			CommandLineHelper cmdLine = new CommandLineHelper(args);

			if(!cmdLine.isValid()) {
				System.err.println(cmdLine.isConverter() ? cmdLine.getConverterUsage() : cmdLine.getUsage());
				System.exit(1);
			}

			if(cmdLine.isUtility()) {
				processUtility(cmdLine);
			} else if(cmdLine.isConverter()) {
				processConverter(cmdLine);
			} else {
				client.setUsername(cmdLine.getParameter("u"));
				client.setUrl(cmdLine.getParameter("url"));
				client.setPassword(cmdLine.getParameter("p"));
				client.setVariablesFile(cmdLine.getParameter("v"));
				client.setRequestTemplateFile(cmdLine.getParameter("i"));
				client.setResponseFile(cmdLine.getParameter("o"));
				String auth = cmdLine.getParameter("b");
				client.setBasicAuthentication(auth != null ? "true".equalsIgnoreCase(auth) || "yes".equalsIgnoreCase(auth) : true);
				auth = cmdLine.getParameter("w");
				client.setWssAuthentication(auth != null ? "true".equalsIgnoreCase(auth) || "yes".equalsIgnoreCase(auth) : false);
				auth = cmdLine.getParameter("ssl");
				client.setSslAuthentication(auth != null ? "true".equalsIgnoreCase(auth) || "yes".equalsIgnoreCase(auth) : false);
				client.setKeyStore(cmdLine.getParameter("ks"));
				client.setKeyStorePassword(cmdLine.getParameter("ksp"));
				client.setProxyServer(cmdLine.getParameter("px"));
				client.setProxyPort(cmdLine.getParameter("pxp") != null ? Integer.parseInt(cmdLine.getParameter("pxp")) : 8080);
				client.setResponseParser(client.getResponseParserInstance(cmdLine.getParameter("rp")));
				client.setTracePath(cmdLine.getParameter("td"));
				client.setTracePrefix(cmdLine.getParameter("tp"));
				client.setInputRetriever(client.getInputRetrieverInstance(cmdLine.getParameter("ol")));
				client.setMessageFactoryImplementationClass(cmdLine.getParameter("fi"));
				String asyncResult = cmdLine.getParameter("sinrespuesta");
				client.setAsynchronousResult(asyncResult != null ? "true".equalsIgnoreCase(asyncResult) || "yes".equalsIgnoreCase(asyncResult) : false);
				String ignoreSSLVal = cmdLine.getParameter("ignorarssl");
				client.setIgnoreSSLValidation(ignoreSSLVal != null ? "true".equalsIgnoreCase(ignoreSSLVal) || "yes".equalsIgnoreCase(ignoreSSLVal) : false);
				client.setSoapAction(cmdLine.getParameter("soapaction"));
				
				Map<String, Object> context = client.parseVariablesFile();
				context.putAll(cmdLine.getVariables());

				client.replaceParameterVariables(context);
				
				client.executeCommandLine(context);
			}
			System.exit(0);
		} catch(Throwable ex) {
			System.err.println(ex.getLocalizedMessage());
			LOG.error("Fail to execute web service: " + client.getClientDescription());
			LOG.error(ex.getLocalizedMessage(), ex);
			System.exit(2);
		}
	}

	private static void processUtility(CommandLineHelper cmdLine) {
		String utilName = null;
		try {
			String value = cmdLine.getParameter("cvc");
			if(StringUtils.isNotEmpty(value)) {
				utilName = "Certificate Validation";
				CertificateUtils.validateCertificates(value);
			}
		} catch(Throwable ex) {
			System.err.println(ex.getLocalizedMessage());
			LOG.error("Fail to execute utility: " + utilName);
			LOG.error(ex.getLocalizedMessage(), ex);
			System.exit(2);
		}
	}

	private static void processConverter(CommandLineHelper cmdLine) {
		String converterClass = cmdLine.getParameter("c");
		String input = cmdLine.getParameter("i");
		String output = cmdLine.getParameter("o");
		
		LOG.info("About to convert input file " + input + " to " + (output != null ? output : "console (stdo)") + " using converter " + converterClass);

		try {
			FileConverter converter = getFileConverterInstance(converterClass);
			if(converter != null) {
				String result = converter.convert(input, cmdLine.getVariables());
				if(output != null) {
					FileUtils.writeFile(output, result);
				} else {
					System.out.println(result);
				}
			}
			LOG.info("Successfully converted input file " + input + " to " + (output != null ? output : "console (stdo)") + " using converter " + converterClass);

			System.exit(0);
		} catch(Throwable ex) {
			System.err.println(ex.getLocalizedMessage());
			LOG.error("Fail to convert file: " + input + " using converter " + converterClass);
			LOG.error(ex.getLocalizedMessage(), ex);
			System.exit(2);
		}
		
	}
	
	private static FileConverter getFileConverterInstance(String converterClass) throws Exception {
		if(converterClass != null) {
			try {
				if(LOG.isDebugEnabled()) {
					LOG.debug("About to create instance for file converter for class [" + converterClass + "]");
				}				
				return (FileConverter)Class.forName(converterClass).newInstance();
			} catch(Exception ex) {
				LOG.error("Could not create file converter instance for class [" + converterClass + "]. " + ex.getMessage(), ex);
				throw ex;
			}
		} else {
			if(LOG.isDebugEnabled()) {
				LOG.debug("No file converter configured. Returning null");
			}					
			return null;
		}
	}
	
	/**
	 * 
	 * @param responseParserClass
	 * @return
	 * @throws Exception
	 */
	public ResponseParser getResponseParserInstance(String responseParserClass) throws Exception {
		if(responseParserClass != null) {
			try {
				if(LOG.isDebugEnabled()) {
					LOG.debug("About to create instance for response parser for class [" + responseParserClass + "]");
				}				
				return (ResponseParser)Class.forName(responseParserClass).newInstance();
			} catch(Exception ex) {
				LOG.error("Could not create response parser instance for class [" + responseParserClass + "]. " + ex.getMessage(), ex);
				throw ex;
			}
		} else {
			if(LOG.isDebugEnabled()) {
				LOG.debug("No response parser configured. Returning null");
			}					
			return null;
		}
	}
	
	public OfflineInputRetriever getInputRetrieverInstance(String inputReceiverClass) throws Exception {
		if(inputReceiverClass != null) {
			try {
				if(LOG.isDebugEnabled()) {
					LOG.debug("About to create instance for offline input receiver for class [" + inputReceiverClass + "]");
				}				
				return (OfflineInputRetriever)Class.forName(inputReceiverClass).newInstance();
			} catch(Exception ex) {
				LOG.error("Could not create offline input receiver instance for class [" + inputReceiverClass + "]. " + ex.getMessage(), ex);
				throw ex;
			}
		} else {
			if(LOG.isDebugEnabled()) {
				LOG.debug("No offline input receiver configured. Returning null");
			}					
			return null;
		}
	}
	
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> parseVariablesFile() throws Exception {
		return FileUtils.parseKeyValueFile(getVariablesFile());
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public void executeCommandLine(Map<String, Object> context) throws Exception {
		if(LOG.isDebugEnabled()) {
			LOG.debug(this);
		}
		
		ClientXmlResponse response = null;
		
		if(getInputRetriever() != null) {
			LOG.debug("Retrieving offline response with class [" + getInputRetriever().getClass().getName() + "]");
			response = getInputRetriever().retrieveInput(context);
			
			if(getInputRetriever().changeResponseFile()) {
				String newResponseFile = getInputRetriever().getResponseFile(getResponseFile(), context);
				LOG.debug("Changing command line response file [" + getResponseFile() + "] to [" + newResponseFile + "]");
				setResponseFile(newResponseFile);
			}
		} else {
			response = execute(context, false);
		}

		if(LOG.isDebugEnabled()) {
			LOG.debug(response);
		}
		
		String parsedResponse = null;
		
		if(!isAsynchronousResult() && getResponseParser() != null) {
			if(LOG.isDebugEnabled()) {
				LOG.debug("Parsing response with parser [" + getResponseParser().getClass().getName() + "]");
			}
			if(response.isSuccess()) {
				parsedResponse = getResponseParser().parseResponse(response.getResponse(), context);
			} else {
				parsedResponse = getResponseParser().parseError(response.getNumber(), response.getMessage(), context);
			}
		} else {
			if(LOG.isDebugEnabled()) {
				LOG.debug(isAsynchronousResult() ? "No parsing for asynchronous response" : "No response parser configured");
			}
			parsedResponse = response.getResponse();
		}
		
		if(parsedResponse != null) {
			if(getResponseFile() != null) {
				if(LOG.isDebugEnabled()) {
					LOG.debug("Writing response contents to [" + getResponseFile() + "]");
				}
				FileUtils.writeFile(getResponseFile(), parsedResponse);
			} else {
				if(LOG.isDebugEnabled()) {
					LOG.debug("Writing response contents to [stdout]");
				}
				System.out.println(parsedResponse);
			}
		} else {
			LOG.warn("Response is null. Response will not be written to [" + (getResponseFile() != null ? getResponseFile() : "stdout") + "]");
		}
		
		if(!response.isSuccess()) {
			throw new Exception(response.getMessage() + ". Error code: " + response.getNumber());
		}
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public ClientXmlResponse execute(Map<String, Object> context, boolean throwException) throws Exception {
		try {
			ClientXmlRequest request = new ClientXmlRequest();

			if(LOG.isDebugEnabled()) {
				LOG.debug("About to read Request Template from file [" + getRequestTemplateFile() + "]");
			}
			
			request.setRequestTemplate(FileUtils.readFile(getRequestTemplateFile()));
			
			if(LOG.isDebugEnabled()) {
				LOG.debug("Read file contents from [" + getRequestTemplateFile() + "]");
				LOG.debug(request);
			}
			
			return execute(request, context);		
		} catch(Exception ex) {
			if(throwException) {
				throw ex;
			} else {
				return createErrorResponse(new Long(-4), ex);
			}
		}
	}
	
	/**
	 * 
	 * @param number
	 * @param ex
	 * @return
	 */
	private ClientXmlResponse createErrorResponse(Long number, Throwable ex) {
		ClientXmlResponse response = new ClientXmlResponse();
		setErrorResponse(response, number, ex);
		return response;
	}
	
	/**
	 * 
	 * @param response
	 * @param number
	 * @param ex
	 */
	private void setErrorResponse(ClientXmlResponse response, Long number, Throwable ex) {
		response.setNumber(number);
		response.setMessage(ex.getMessage() + (ex.getCause() != null ? " -> Caused by: " + ex.getCause().getMessage() : ""));
	}

	/**
	 * 
	 * @param response
	 * @param number
	 * @param ex
	 */
	private void setErrorResponse(ClientXmlResponse response, SoapFaultClientException ex) {
		String message = null;
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
		    Transformer transformer = transformerFactory.newTransformer();
		    DOMResult result = new DOMResult();
		    transformer.transform(ex.getSoapFault().getSource(), result);
		    NodeList nl = ((Document)result.getNode()).getElementsByTagName("detail");
		    Node detail = nl.item(0);
		    message = detail.getTextContent();
		    Node errors = detail.getFirstChild().getFirstChild();
		    String code = null;
		    String description = null;
		    for(int i=0; i < errors.getChildNodes().getLength(); i++) {
		    	if("code".equalsIgnoreCase(errors.getChildNodes().item(i).getLocalName())) {
		    		code = errors.getChildNodes().item(i).getTextContent();
		    	} else if("description".equalsIgnoreCase(errors.getChildNodes().item(i).getLocalName())) {
		    		description = errors.getChildNodes().item(i).getTextContent();
		    	} 
		    }
		    if(description != null) {
		    	response.setNumber(code != null && NumberUtils.isNumber(code) ? new Long(code) : new Long(-2));
		    	response.setMessage(description);
		    } else {
		    	response.setNumber(new Long(-2));
		    	response.setMessage(ex.getMessage() + " -> Details: " + message);
		    }
	    } catch(Throwable t) {
			response.setNumber(new Long(-2));
			if(message != null) {
				response.setMessage(ex.getMessage() + " -> Details: " + message);
			} else {
				response.setMessage(ex.getMessage() + (ex.getCause() != null ? " -> Caused by: " + ex.getCause().getMessage() : ""));
			}
	    }
	}
	
	/**
	 * 
	 * @param suffix
	 * @param contents
	 */
	private void trace(String suffix, String contents) {
		if(isTraceEnabled()) {
			String file = FileUtils.buildTraceFileName(getTracePath(), getTracePrefix(), suffix);
			try {
				FileUtils.writeFile(file, contents);
			} catch(Exception ex) {
				LOG.warn("Could not create trace file [" + file + "]: " + ex.getMessage(), ex);
			}
		}
		
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public ClientXmlResponse execute(ClientXmlRequest request, Map<String, Object> context) throws Exception {
		if(request != null) {
			ClientXmlResponse response = new ClientXmlResponse();

			try {
				request.compile(context);
				if(LOG.isDebugEnabled()) {
					LOG.debug("About to execute compiled request [" + request.getRequest() + "]");
				}
				
				trace(TRACE_REQUEST_SUFFIX, request.getRequest());
				
				response.setResponse(sendAndReceive(request.getRequest()));
				if(LOG.isDebugEnabled()) {
					LOG.debug("Received raw response [" + response.getResponse() + "]");
				}

				trace(TRACE_RESPONSE_SUFFIX, response.getResponse());

				if (!isAsynchronousResult()) {
					response.parseResponse();
				} else {
					response.setAsynchronousResponse(true);
				}
			} catch(SoapFaultClientException ex) {
				LOG.error(ex.getMessage(), ex);
				setErrorResponse(response, ex);
			} catch(ValidationException ex) {
				LOG.error(ex.getMessage(), ex);
				setErrorResponse(response, new Long(-3), ex);
			} catch(Throwable ex) {
				LOG.error(ex.getMessage(), ex);
				throw new Exception(ex.getMessage(), ex.getCause());
			}
			return response;
		} else {
			LOG.error("Request cannot be null");
			throw new Exception("Request cannot be null");
		}
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private String sendAndReceive(String request) throws Exception {
		WebServiceTemplate webServiceTemplate = new WebServiceTemplate();

        StreamSource source = new StreamSource(new StringReader(request));
        StringWriter response = new StringWriter();
        StreamResult result = new StreamResult(response);

        SaajSoapMessageFactory mf = getMessageFactory();
        if(mf != null) {
           	webServiceTemplate.setMessageFactory(mf);
        }
        
        SoapActionCallback soapActionCallback = null;
        
        if (getSoapAction() != null) {
        	LOG.debug("Using SOAP Action " + getSoapAction());
        	soapActionCallback = new SoapActionCallback(getSoapAction());
        }
        
        if (isIgnoreSSLValidation()) {
        	LOG.info("Ignoring SSL certificate validations");
        	Protocol.registerProtocol("https", new Protocol("https", new BypassProtocolSocketFactory(), 443));
        }
        
        if(isSslAuthentication()) {
        	// SSL Auth
        	sslProtocolInit();
            CommonsHttpMessageSender sender = new CommonsHttpMessageSender();
            HttpClient client = new HttpClient();
            if(getProxyServer() != null) {
            	client.getHostConfiguration().setProxy(getProxyServer(), getProxyPort());
            }

            sender.setHttpClient(client);
            sender.afterPropertiesSet();
            
        	webServiceTemplate.setMessageSender(sender);

        	if(!isAsynchronousResult()) {
                webServiceTemplate.sendSourceAndReceiveToResult(getUrl(), source, soapActionCallback, result);
        	} else {
        		LOG.debug("Asynchronous response for SSL Authentication");
        		webServiceTemplate.sendSourceAndReceive(getUrl(), source, soapActionCallback, getAsynchronousResponseExtractor());
        	}
        } else if(isBasicAuthentication()) {
        	// Start HTTP Basic Authentication
            CommonsHttpMessageSender sender = new CommonsHttpMessageSender();
            HttpClient client = new HttpClient();
            if(getUsername() != null) {
                sender.setCredentials(new UsernamePasswordCredentials(getUsername(), getPassword()));
                client.getParams().setAuthenticationPreemptive(true);
            }
            if(getProxyServer() != null) {
            	client.getHostConfiguration().setProxy(getProxyServer(), getProxyPort());
            }
            
            sender.setHttpClient(client);
            
            sender.afterPropertiesSet();

            webServiceTemplate.setMessageSender(sender);

        	if(!isAsynchronousResult()) {
                webServiceTemplate.sendSourceAndReceiveToResult(getUrl(), source, soapActionCallback, result);
        	} else {
        		LOG.debug("Asynchornous response for Basic Authentication");
        		webServiceTemplate.sendSourceAndReceive(getUrl(), source, soapActionCallback, getAsynchronousResponseExtractor());
        	}
        } else {
            // WSSE Auth
        	if(!isAsynchronousResult()) {
        		webServiceTemplate.sendSourceAndReceiveToResult(getUrl(), source, new WSSEHeaderWebServiceMessageCallback(getUsername(), getPassword()), result);
        	} else {
        		LOG.debug("Asynchornous response for WSSE Authentication");
        		webServiceTemplate.sendSourceAndReceive(getUrl(), source, new WSSEHeaderWebServiceMessageCallback(getUsername(), getPassword()), getAsynchronousResponseExtractor());
        	}
        }
        
        if(!isAsynchronousResult()) {
            return response.toString();
        } else {
        	return "OK";
        }
	}

	/**
	 * 
	 * @throws Exception
	 */
	protected void sslProtocolInit() throws Exception {
		//CertificateUtils.validateCertificateExpiration(getKeyStore(), getKeyStorePassword(), null);
		
		HttpSecureProtocol protocolSocketFactory;
		protocolSocketFactory = new HttpSecureProtocol();
		File jksTrustStore = new File(getKeyStore());
		TrustMaterial trustMaterial = new TrustMaterial(jksTrustStore, getKeyStorePassword().toCharArray());
		protocolSocketFactory.addTrustMaterial(trustMaterial);
		// No host name verification
		protocolSocketFactory.setCheckHostname(false);
		File jksKeyStore = new File(getKeyStore());
		KeyMaterial key = new KeyMaterial(jksKeyStore, getKeyStorePassword().toCharArray());
		protocolSocketFactory.setKeyMaterial(key);
		// Timeout
		// protocolSocketFactory.setConnectTimeout(getTimeOutMs());
		// Register protocol
		Protocol protocol = new Protocol("https", (ProtocolSocketFactory) protocolSocketFactory, 443);
		Protocol.registerProtocol("https", protocol);
	}  
	
	/**
	 * @return the requestTemplateFile
	 */
	public String getRequestTemplateFile() {
		return requestTemplateFile;
	}

	/**
	 * @param requestTemplateFile the requestTemplateFile to set
	 */
	public void setRequestTemplateFile(String requestTemplateFile) {
		this.requestTemplateFile = requestTemplateFile;
	}

	/**
	 * @return the responseFile
	 */
	public String getResponseFile() {
		return responseFile;
	}

	/**
	 * @param responseFile the responseFile to set
	 */
	public void setResponseFile(String responseFile) {
		this.responseFile = responseFile;
	}

	/**
	 * @return the basicAuthentication
	 */
	public boolean isBasicAuthentication() {
		return basicAuthentication;
	}

	/**
	 * @param basicAuthentication the basicAuthentication to set
	 */
	public void setBasicAuthentication(boolean basicAuthentication) {
		this.basicAuthentication = basicAuthentication;
	}
	
	/**
	 * 
	 * @return
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
	   	.append("url" , getUrl())
	   	.append("username", getUsername())
	   	.append("password", getPassword())
	   	.append("requestTemplateFile", getRequestTemplateFile())
	   	.append("responseFile", getResponseFile())
	   	.append("variablesFile", getVariablesFile())
	   	.append("basicAuthentication", isBasicAuthentication())
	   	.append("wssAuthentication", isWssAuthentication())
	   	.append("sslAuthentication", isSslAuthentication())
	   	.append("keystore", getKeyStore())
	   	.append("ketStorePassword", getKeyStorePassword())
	   	.append("proxyServer", getProxyServer())
	   	.append("proxyPort", getProxyPort())
	   	.append("messageFactoryImplementationClass", getMessageFactoryImplementationClass())
	   	.append("responseParser", getResponseParser() != null ? getResponseParser().getClass().getName() : null)
	   	.append("tracePath", getTracePath())
	   	.append("tracePrefix", getTracePrefix())
	   	.append("offlineInputRetriever", getInputRetriever() != null ? getInputRetriever().getClass().getName() : null)
	   	.toString();		
	}

	/**
	 * @return the messageFactoryImplementationClass
	 */
	public String getMessageFactoryImplementationClass() {
		return messageFactoryImplementationClass;
	}

	/**
	 * @param messageFactoryImplementationClass the messageFactoryImplementationClass to set
	 */
	public void setMessageFactoryImplementationClass(String messageFactoryImplementationClass) {
		this.messageFactoryImplementationClass = messageFactoryImplementationClass;
	}
	
	/**
	 * 
	 * @return
	 */
	private SaajSoapMessageFactory getMessageFactory() {
		if(getMessageFactoryImplementationClass() != null) {
			if ("SOAP-1.2".equalsIgnoreCase(getMessageFactoryImplementationClass())) {
	        	if(LOG.isDebugEnabled()) {
	        		LOG.debug("Setting message factory with class SOAP 1.2 Protocol");
	        	}
				try {
					MessageFactory mf = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
				    return new SaajSoapMessageFactory(mf);
				} catch(Throwable ex) {
					LOG.error("Cannot create SaajSoapMessageFactory for SOAP 1.2 Protocol", ex);
				}
			    
			} else {
	        	if(LOG.isDebugEnabled()) {
	        		LOG.debug("Setting message factory with class [" + getMessageFactoryImplementationClass() + "]");
	        	}
				try {
					MessageFactory instance = (MessageFactory) Class.forName(getMessageFactoryImplementationClass()).newInstance();
					SaajSoapMessageFactory mf = new SaajSoapMessageFactory();
					mf.setMessageFactory(instance);
					return mf;
				} catch(Throwable ex) {
					LOG.error("Cannot create SaajSoapMessageFactory for class [" + getMessageFactoryImplementationClass() + "]", ex);
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public String getVariablesFile() {
		return variablesFile;
	}

	/**
	 * @return the sslAuthentication
	 */
	public boolean isSslAuthentication() {
		return sslAuthentication;
	}

	/**
	 * @param sslAuthentication the sslAuthentication to set
	 */
	public void setSslAuthentication(boolean sslAuthentication) {
		this.sslAuthentication = sslAuthentication;
	}

	/**
	 * @return the wssAuthentication
	 */
	public boolean isWssAuthentication() {
		return wssAuthentication;
	}

	/**
	 * @param wssAuthentication the wssAuthentication to set
	 */
	public void setWssAuthentication(boolean wssAuthentication) {
		this.wssAuthentication = wssAuthentication;
	}

	/**
	 * 
	 * @param variablesFile
	 */
	public void setVariablesFile(String variablesFile) {
		this.variablesFile = variablesFile;
	}

	/**
	 * @return the keyStore
	 */
	public String getKeyStore() {
		return keyStore;
	}

	/**
	 * @param keyStore the keyStore to set
	 */
	public void setKeyStore(String keyStore) {
		this.keyStore = keyStore;
	}

	/**
	 * @return the keyStorePassword
	 */
	public String getKeyStorePassword() {
		return keyStorePassword;
	}

	/**
	 * @param keyStorePassword the keyStorePassword to set
	 */
	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}

	/**
	 * @return the proxyServer
	 */
	public String getProxyServer() {
		return proxyServer;
	}

	/**
	 * @param proxyServer the proxyServer to set
	 */
	public void setProxyServer(String proxyServer) {
		this.proxyServer = proxyServer;
	}

	/**
	 * @return the proxyPort
	 */
	public int getProxyPort() {
		return proxyPort;
	}

	/**
	 * @param proxyPort the proxyPort to set
	 */
	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	/**
	 * @return the responseParser
	 */
	public ResponseParser getResponseParser() {
		return responseParser;
	}

	/**
	 * @param responseParser the responseParser to set
	 */
	public void setResponseParser(ResponseParser responseParser) {
		this.responseParser = responseParser;
	}

	/**
	 * 
	 * @return
	 */
	private boolean isTraceEnabled() {
		return getTracePrefix() != null;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getClientDescription() {
		if(getInputRetriever() != null) {
			return "Offline input retriever: [" + getInputRetriever().getClass().getName() + "]";
		} else {
			return "Url: [" + getUrl() + "] - Template: [" + getRequestTemplateFile() + "]" + (getVariablesFile() != null ? " - Variables File [" + getVariablesFile() + "]" : "");
		}
	}

	/**
	 * @return the inputRetriever
	 */
	public OfflineInputRetriever getInputRetriever() {
		return inputRetriever;
	}

	/**
	 * @param inputRetriever the inputRetriever to set
	 */
	public void setInputRetriever(OfflineInputRetriever inputRetriever) {
		this.inputRetriever = inputRetriever;
	}
	
	public SourceExtractor getAsynchronousResponseExtractor() {
		return new SourceExtractor() {

			public Object extractData(Source source) throws IOException, TransformerException {
				LOG.debug("Ignoring data extract in asynchronous mode. Returning OK");
				return "OK";
			}
		};
	}
	
	public boolean isAsynchronousResult() {
		return this.asynchronousResult;
	}

	/**
	 * @param asynchronousResult the asynchronousResult to set
	 */
	public void setAsynchronousResult(boolean asynchronousResult) {
		this.asynchronousResult = asynchronousResult;
	}

	/**
	 * @return the soapAction
	 */
	public String getSoapAction() {
		return soapAction;
	}

	/**
	 * @param soapAction the soapAction to set
	 */
	public void setSoapAction(String soapAction) {
		this.soapAction = soapAction;
	}

	/**
	 * @return the ignoreSSLValidation
	 */
	public boolean isIgnoreSSLValidation() {
		return ignoreSSLValidation;
	}

	/**
	 * @param ignoreSSLValidation the ignoreSSLValidation to set
	 */
	public void setIgnoreSSLValidation(boolean ignoreSSLValidation) {
		this.ignoreSSLValidation = ignoreSSLValidation;
	}
}
