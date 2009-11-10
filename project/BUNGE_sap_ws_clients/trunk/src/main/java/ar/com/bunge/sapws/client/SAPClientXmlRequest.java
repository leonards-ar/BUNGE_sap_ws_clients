/*
 * File name: SAPClientXmlRequest.java
 * Creation date: Jul 28, 2009 5:11:37 PM
 * Copyright Mindpool
 */
package ar.com.bunge.sapws.client;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import ar.com.bunge.util.ValidationException;
import bsh.EvalError;
import bsh.Interpreter;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since 1.0
 *
 */
public class SAPClientXmlRequest {
	private String requestTemplate;
	private String request;
	public static final String VARIABLE_SEPARATOR = "#";
	public static final String SCRIPT_OPEN_TOKEN = "${";
	public static final String SCRIPT_CLOSE_TOKEN = "}";
	public static final String ITERATOR_VARIABLE_OPEN_TOKEN = "${loop(";
	public static final String ITERATOR_VARIABLE_CLOSE_TOKEN = ")}";
	public static final String ITERATOR_BLOCK_END_TOKEN = "{end loop}";

	public static final String SCRIPT_ALT_OPEN_TOKEN = "%{";

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
		String request = expandLoops(getRequestTemplate(), context);
		
		request = StringUtils.replace(request, SCRIPT_ALT_OPEN_TOKEN, SCRIPT_OPEN_TOKEN);
		
		String scripts[] = StringUtils.substringsBetween(request, SCRIPT_OPEN_TOKEN, SCRIPT_CLOSE_TOKEN);
		Object value;
		
		if(scripts != null) {
			for(int i=0; i < scripts.length; i++) {
				value = evaluateScript(scripts[i], context);
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
	 * @param request
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private String expandLoops(String request, Map<String, Object> context) throws Exception {
		String expandedRequest = new String(request);
		
		String prefix, suffix , loopVariable, xml;
		String parts[];
		while(expandedRequest.indexOf(ITERATOR_VARIABLE_OPEN_TOKEN) > -1) {
			parts = splitByWholeSeparators(expandedRequest, new String[] {ITERATOR_VARIABLE_OPEN_TOKEN, ITERATOR_VARIABLE_CLOSE_TOKEN, ITERATOR_BLOCK_END_TOKEN});
			
			prefix = parts.length > 0 ? parts[0] : null;
			loopVariable = parts.length > 1 ? parts[1] : null;
			xml = parts.length > 2 ? parts[2] : null;
			suffix = parts.length > 3 ? parts[3] : null;
			
			int repetitions = getRepetitions(loopVariable, context);
			
			expandedRequest = prefix;
			
			for(int i = 0; i < repetitions; i++) {
				expandedRequest += addIndexToVariables(xml, i);
			}
			
			expandedRequest += suffix;
		}
		
		
		return expandedRequest;
	}
	
	/**
	 * 
	 * @param xml
	 * @param index
	 * @return
	 */
	private String addIndexToVariables(String xml, int index) {
		String indexedXml = new String(xml);
		String vars[] = StringUtils.substringsBetween(indexedXml, VARIABLE_SEPARATOR, VARIABLE_SEPARATOR);
		
		if(vars != null) {
			for(int i=0; i < vars.length; i++) {
				indexedXml = StringUtils.replace(indexedXml, VARIABLE_SEPARATOR + vars[i] + VARIABLE_SEPARATOR, VARIABLE_SEPARATOR + vars[i] + "(" + index + ")" + VARIABLE_SEPARATOR);
			}
		}
		return indexedXml;		
	}
	
	/**
	 * 
	 * @param loopVariable
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private int getRepetitions(String loopVariable, Map<String, Object> context) throws Exception {
		try {
			Object reps = context.get(loopVariable);
			int repetitions = reps != null ? Integer.parseInt(reps.toString()) : 1;
			return repetitions > 0 ? repetitions : 1;
		} catch(Throwable ex) {
			throw new Exception("Loop variable [" + loopVariable + "] must be a valid variable name which value is a valid integer representing the number of times to repeat the loop", ex);
		}
	}
	
	/**
	 * 
	 * @param text
	 * @param separators
	 * @return
	 */
	private String[] splitByWholeSeparators(String text, String separators[]) {
		if(separators != null && text != null) {
			String temp[] = new String[separators.length + 1];
			
			int from = 0, to;
			int partsIdx = 0;
			for(int i = 0; i < separators.length; i++) {
				if(separators[i] != null) {
					to = text.indexOf(separators[i], from);
					if(from >= 0 && from < text.length() && to > from && to <= text.length()) {
						temp[partsIdx++] = text.substring(from, to);
						from = to + separators[i].length();
					} else if(from >= 0 && from < text.length() && to < 0) {
						temp[partsIdx++] = text.substring(from);
						from = text.length();
					}
				}
			}
			
			if(from >= 0 && from <= text.length()) {
				temp[partsIdx++] = text.substring(from);
			}
			
			String parts[] = new String[partsIdx];
			System.arraycopy(temp, 0, parts, 0, partsIdx);
			
			return parts;
		} else {
			return null;
		}
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
	private String evaluateScript(String script, Map<String, Object> context) throws Exception {
		try {
			Interpreter i = new Interpreter();
			i.eval("import ar.com.bunge.util.Utils");
			String bshScript = resolveScript(script, context);
	    	Object result = i.eval(bshScript);
	    	return result != null ? result.toString() : "";
		} catch(EvalError e) {
			if(e.getCause() instanceof ValidationException) {
				throw new ValidationException(e.getCause().getMessage(), e);
			} else {
				throw e;
			}
		}
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
	
	/**
	 * 
	 * @return
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
	   	.append("requestTemplate", getRequestTemplate())
	   	.toString();		
	}	
}
