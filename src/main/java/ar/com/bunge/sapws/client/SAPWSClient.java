/*
 * File name: SAPWSClient.java
 * Creation date: 01/08/2009 08:58:27
 * Copyright Mindpool
 */
package ar.com.bunge.sapws.client;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.log4j.Logger;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.CommonsHttpMessageSender;

import ar.com.bunge.util.Utils;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since SPM 1.0
 *
 */
public class SAPWSClient {
	private static final Logger LOG = Logger.getLogger(SAPWSClient.class);
	
	private String username;
	private String password;
	private String url;
	private String requestTemplateFile;
	private String responseFile;
	private boolean basicAuthentication;
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
			client.setRequestTemplateFile(cmdLine.getParameter("i"));
			client.setResponseFile(cmdLine.getParameter("o"));
			//:TODO: Load from command line.
			client.setBasicAuthentication(true);
			client.execute(cmdLine.getVariables());
			
			System.exit(0);
		} catch(Throwable ex) {
			System.err.println(ex.getLocalizedMessage());
			LOG.error(ex.getLocalizedMessage(), ex);
			System.exit(2);
		}
	}

	/**
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public void execute(Map<String, Object> context) throws Exception {
		SAPClientXmlRequest request = new SAPClientXmlRequest();
		request.setRequestTemplate(Utils.readFile(getRequestTemplateFile()));
		
		SAPClientXmlResponse response = execute(request, context);

		if(getResponseFile() != null) {
			Utils.writeFile(getResponseFile(), response.getResponse());
		} else {
			System.out.println(response.getResponse());
		}
		
		if(!response.isSuccess()) {
			throw new Exception(response.getMessage() + ". Error code: " + response.getNumber());
		}
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public SAPClientXmlResponse execute(SAPClientXmlRequest request, Map<String, Object> context) throws Exception {
		if(request != null) {
			request.compile(context);
			
			SAPClientXmlResponse response = new SAPClientXmlResponse();
			response.setResponse(sendAndReceive(request.getRequest()));
			response.parseResponse();
			
			return response;
		} else {
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
}
