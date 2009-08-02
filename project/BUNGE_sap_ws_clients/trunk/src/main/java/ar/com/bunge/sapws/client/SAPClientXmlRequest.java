/*
 * File name: SAPClientXmlRequest.java
 * Creation date: Jul 28, 2009 5:11:37 PM
 * Copyright Mindpool
 */
package ar.com.bunge.sapws.client;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import bsh.Interpreter;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since SPM 1.0
 *
 */
public class SAPClientXmlRequest {
	private String requestTemplate;
	private String request;
	public static final String VARIABLE_SEPARATOR = "#";
	public static final String SCRIPT_OPEN_TOKEN = "${";
	public static final String SCRIPT_CLOSE_TOKEN = "}";
	
	/**
	 * 
	 */
	public SAPClientXmlRequest() {
		this(null);
	}

	/**
	 * 
	 * @param requestTemplate
	 */
	public SAPClientXmlRequest(String requestTemplate) {
		super();
		setRequestTemplate(requestTemplate);
	}
	
	/**
	 * 
	 * @param context
	 * @throws Exception
	 */
	public void compile(Map<String, Object> context) throws Exception {
		String request = new String(getRequestTemplate());
		String scripts[] = StringUtils.substringsBetween(request, SCRIPT_OPEN_TOKEN, SCRIPT_CLOSE_TOKEN);
		Object value;
		
		if(scripts != null) {
			for(int i=0; i < scripts.length; i++) {
				value = evalueateScript(scripts[i], context);
				if(value == null) {
					value = "";
				}
				request = StringUtils.replace(request, SCRIPT_OPEN_TOKEN + scripts[i] + SCRIPT_CLOSE_TOKEN, value.toString());
			}
		}
		setRequest(request);
	}

	/**
	 * 
	 * @param script
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private String resolveScript(String script, Map<String, Object> context) throws Exception {
		String resolvedScript = new String(script);
		String vars[] = StringUtils.substringsBetween(resolvedScript, VARIABLE_SEPARATOR, VARIABLE_SEPARATOR);
		Object value;
		
		if(vars != null) {
			for(int i=0; i < vars.length; i++) {
				value = context.get(vars[i]);
				if(value == null) {
					value = "";
				}
				resolvedScript = StringUtils.replace(resolvedScript, VARIABLE_SEPARATOR + vars[i] + VARIABLE_SEPARATOR, "\"" + value + "\"");
			}
		}
		return resolvedScript;
	}

	/**
	 * 
	 * @param script
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private String evalueateScript(String script, Map<String, Object> context) throws Exception {
		Interpreter i = new Interpreter();
		i.eval("import ar.com.bunge.util.Utils");
		String bshScript = resolveScript(script, context);
    	Object result = i.eval(bshScript);
    	return result != null ? result.toString() : "";
	}
	
	/**
	 * @return the requestTemplate
	 */
	public String getRequestTemplate() {
		return requestTemplate;
	}


	/**
	 * @param requestTemplate the requestTemplate to set
	 */
	public void setRequestTemplate(String requestTemplate) {
		this.requestTemplate = requestTemplate;
	}


	/**
	 * @return the request
	 */
	public String getRequest() {
		return request;
	}


	/**
	 * @param request the request to set
	 */
	public void setRequest(String request) {
		this.request = request;
	}
}
