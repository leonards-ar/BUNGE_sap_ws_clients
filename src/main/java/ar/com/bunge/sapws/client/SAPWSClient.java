/*
 * File name: SAPWSClient.java
 * Creation date: 01/08/2009 08:58:27
 * Copyright Mindpool
 */
package ar.com.bunge.sapws.client;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.soap.MessageFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.CommonsHttpMessageSender;

import ar.com.bunge.util.Utils;
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
	
	private String username;
	private String password;
	private String url;
	private String requestTemplateFile;
	private String responseFile;
	private String variablesFile;
	private boolean basicAuthentication;
	
	private String messageFactoryImplementationClass = null;
	
	
	/**
	 * 
	 */
	public SAPWSClient() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CommandLineHelper cmdLine = new CommandLineHelper(args);
		if(!cmdLine.isValid()) {
			System.err.println(cmdLine.getUsage());
			System.exit(1);
		}
		try {
			SAPWSClient client = new SAPWSClient();
			client.setUsername(cmdLine.getParameter("u"));
			client.setUrl(cmdLine.getParameter("url"));
			client.setPassword(cmdLine.getParameter("p"));
			client.setVariablesFile(cmdLine.getParameter("v"));
			client.setRequestTemplateFile(cmdLine.getParameter("i"));
			client.setResponseFile(cmdLine.getParameter("o"));
			String auth = cmdLine.getParameter("a");
			client.setBasicAuthentication(auth != null ? "true".equalsIgnoreCase(auth) || "yes".equalsIgnoreCase(auth) : true);

			Map<String, Object> context = client.parseVariablesFile();
			context.putAll(cmdLine.getVariables());
			
			client.executeCommandLine(context);
			
			System.exit(0);
		} catch(Throwable ex) {
			System.err.println(ex.getLocalizedMessage());
			LOG.error(ex.getLocalizedMessage(), ex);
			System.exit(2);
		}
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> parseVariablesFile() throws Exception {
		Map<String, Object> fileVariables = new HashMap<String, Object>();
		
		if(getVariablesFile() != null) {
			List<String> lines = Utils.readFileLines(getVariablesFile());
			if(lines != null && lines.size() > 0) {
				String paramValue[];
				String name, value;
				for(Iterator<String> it = lines.iterator(); it.hasNext(); ) {
					paramValue = Utils.parseParameter(it.next());
					if(paramValue != null && paramValue.length == 2) {
						name = paramValue != null ? paramValue[0] : null;
						value = paramValue != null ? paramValue[1] : null;			
						if(!StringUtils.isEmpty(name) && !StringUtils.isEmpty(value)) {
							fileVariables.put(name, value);
						}
					}
					
				}
				
			}
		}
		
		return fileVariables;
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
		SAPClientXmlResponse response = execute(context);

		if(LOG.isDebugEnabled()) {
			LOG.debug(response);
		}
		
		if(getResponseFile() != null) {
			if(LOG.isDebugEnabled()) {
				LOG.debug("Writing response contents to [" + getResponseFile() + "]");
			}
			Utils.writeFile(getResponseFile(), response.getResponse());
		} else {
			if(LOG.isDebugEnabled()) {
				LOG.debug("Writing response contents to [stdout]");
			}
			System.out.println(response.getResponse());
		}
		
		if(!response.isSuccess()) {
			String msg = response.getMessage() + ". Error code: " + response.getNumber();
			LOG.error(msg);
			throw new Exception(msg);
		}
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public SAPClientXmlResponse execute(Map<String, Object> context) throws Exception {
		SAPClientXmlRequest request = new SAPClientXmlRequest();

		if(LOG.isDebugEnabled()) {
			LOG.debug("About to read Request Template from file [" + getRequestTemplateFile() + "]");
		}
		
		request.setRequestTemplate(Utils.readFile(getRequestTemplateFile()));
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("Read file contents from [" + getRequestTemplateFile() + "]");
			LOG.debug(request);
		}
		
		return execute(request, context);		
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public SAPClientXmlResponse execute(SAPClientXmlRequest request, Map<String, Object> context) throws Exception {
		if(request != null) {
			SAPClientXmlResponse response = new SAPClientXmlResponse();

			try {
				request.compile(context);
				if(LOG.isDebugEnabled()) {
					LOG.debug("About to execute compiled request [" + request.getRequest() + "]");
				}
				response.setResponse(sendAndReceive(request.getRequest()));
				if(LOG.isDebugEnabled()) {
					LOG.debug("Received raw response [" + response.getResponse() + "]");
				}
				response.parseResponse();
			} catch(SoapFaultClientException ex) {
				LOG.error(ex.getMessage(), ex);
				response.setNumber(new Long(-2));
				response.setMessage(ex.getMessage());
			} catch(ValidationException ex) {
				LOG.error(ex.getMessage(), ex);
				response.setNumber(new Long(-3));
				response.setMessage(ex.getMessage());
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
        
        // Start HTTP Basic Authentication
        if(isBasicAuthentication()) {
            CommonsHttpMessageSender sender = new CommonsHttpMessageSender();
            sender.setCredentials(new UsernamePasswordCredentials(getUsername(), getPassword()));
            HttpClient client = new HttpClient();
            client.getParams().setAuthenticationPreemptive(true);
            sender.setHttpClient(client);
            
            sender.afterPropertiesSet();
            webServiceTemplate.setMessageSender(sender);

            webServiceTemplate.sendSourceAndReceiveToResult(getUrl(), source, result);
        } else {
            // WSSE Auth
            webServiceTemplate.sendSourceAndReceiveToResult(getUrl(), source, new WSSEHeaderWebServiceMessageCallback(getUsername(), getPassword()), result);
        }
        

        return response.toString();
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
	   	.append("messageFactoryImplementationClass", getMessageFactoryImplementationClass())
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
	 * 
	 * @param variablesFile
	 */
	public void setVariablesFile(String variablesFile) {
		this.variablesFile = variablesFile;
	}
}
