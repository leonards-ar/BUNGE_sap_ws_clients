/*
 * File name: ClientXmlRequest.java
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
import bsh.TargetError;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since 1.0
 *
 */
public class ClientXmlRequest {
	private String requestTemplate;
	private String request;
	public static final String VARIABLE_SEPARATOR = "#";
	public static final String SCRIPT_OPEN_TOKEN = "${";
	public static final String SCRIPT_CLOSE_TOKEN = "}";
	public static final String ITERATOR_VARIABLE_OPEN_TOKEN = "${loop(";
	public static final String ITERATOR_VARIABLE_CLOSE_TOKEN = ")}";
	public static final String ITERATOR_BLOCK_END_TOKEN = "{end loop}";

	public static final String NESTED_ITERATOR_VARIABLE_OPEN_TOKEN = "${xloop(";
	public static final String NESTED_ITERATOR_VARIABLE_CLOSE_TOKEN = ")}";
	public static final String NESTED_ITERATOR_BLOCK_END_TOKEN = "{end xloop}";

	public static final String NESTED_NESTED_ITERATOR_VARIABLE_OPEN_TOKEN = "${xxloop(";
	public static final String NESTED_NESTED_ITERATOR_VARIABLE_CLOSE_TOKEN = ")}";
	public static final String NESTED_NESTED_ITERATOR_BLOCK_END_TOKEN = "{end xxloop}";
	
	public static final String ITERATOR_NULL_VARIABLE_OPEN_TOKEN = "${loopn(";
	public static final String ITERATOR_NULL_VARIABLE_CLOSE_TOKEN = ")}";
	public static final String ITERATOR_NULL_BLOCK_END_TOKEN = "{end loopn}";
	
	public static final String NESTED_ITERATOR_NULL_VARIABLE_OPEN_TOKEN = "${xloopn(";
	public static final String NESTED_ITERATOR_NULL_VARIABLE_CLOSE_TOKEN = ")}";
	public static final String NESTED_ITERATOR_NULL_BLOCK_END_TOKEN = "{end xloopn}";	

	public static final String NESTED_NESTED_ITERATOR_NULL_VARIABLE_OPEN_TOKEN = "${xxloopn(";
	public static final String NESTED_NESTED_ITERATOR_NULL_VARIABLE_CLOSE_TOKEN = ")}";
	public static final String NESTED_NESTED_ITERATOR_NULL_BLOCK_END_TOKEN = "{end xxloopn}";
	
	public static final String IF_VALUE_VARIABLE_OPEN_TOKEN = "${if value(";
	public static final String IF_VALUE_VARIABLE_CLOSE_TOKEN = ")}";
	public static final String IF_VALUE_BLOCK_END_TOKEN = "{end if value}";

	public static final String SCRIPT_ALT_OPEN_TOKEN = "%{";

	/**
	 * 
	 */
	public ClientXmlRequest() {
		this(null);
	}

	/**
	 * 
	 * @param requestTemplate
	 */
	public ClientXmlRequest(String requestTemplate) {
		super();
		setRequestTemplate(requestTemplate);
	}
	
	/**
	 * 
	 * @param context
	 * @throws Exception
	 */
	public void compile(Map<String, Object> context) throws Exception {
		String request = expandNestedNestedNullLoops(getRequestTemplate(), context);

		request = expandNestedNullLoops(request, context);

		request = expandNestedNestedLoops(request, context);

		request = expandNestedLoops(request, context);

		request = expandLoops(request, context);
		
		request = expandNullLoops(request, context);
		
		request = evaluateIfValues(request, context);
		
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
	private String evaluateIfValues(String request, Map<String, Object> context) throws Exception {
		String evaluatedRequest = new String(request);

		String prefix, suffix , ifVariable, xml;
		String parts[];
		while(evaluatedRequest.indexOf(IF_VALUE_VARIABLE_OPEN_TOKEN) > -1) {
			parts = splitByWholeSeparators(evaluatedRequest, new String[] {IF_VALUE_VARIABLE_OPEN_TOKEN, IF_VALUE_VARIABLE_CLOSE_TOKEN, IF_VALUE_BLOCK_END_TOKEN});
			
			prefix = parts.length > 0 ? parts[0] : null;
			ifVariable = parts.length > 1 ?  StringUtils.substringBetween(parts[1], VARIABLE_SEPARATOR, VARIABLE_SEPARATOR) : null;
			xml = parts.length > 2 ? parts[2] : null;
			suffix = parts.length > 3 ? parts[3] : null;
			
			evaluatedRequest = prefix;
			
			if(ifVariable != null && getFromContext(ifVariable, context) != null) {
				evaluatedRequest += xml;
			}
			
			evaluatedRequest += suffix;
		}
		
		
		return evaluatedRequest;
		
	}
	
	/**
	 * 
	 * @param key
	 * @param context
	 * @return
	 */
	private Object getFromContext(String key, Map<String, Object> context) {
		if(key != null && context != null) {
			return context.get(key.toLowerCase());
		} else {
			return null;
		}
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
			
			int repetitions = getRepetitions(loopVariable, context, 1);
			
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
	 * @param request
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private String expandNullLoops(String request, Map<String, Object> context) throws Exception {
		String expandedRequest = new String(request);
		
		String prefix, suffix , loopVariable, xml;
		String parts[];
		while(expandedRequest.indexOf(ITERATOR_NULL_VARIABLE_OPEN_TOKEN) > -1) {
			parts = splitByWholeSeparators(expandedRequest, new String[] {ITERATOR_NULL_VARIABLE_OPEN_TOKEN, ITERATOR_NULL_VARIABLE_CLOSE_TOKEN, ITERATOR_NULL_BLOCK_END_TOKEN});
			
			prefix = parts.length > 0 ? parts[0] : null;
			loopVariable = parts.length > 1 ? parts[1] : null;
			xml = parts.length > 2 ? parts[2] : null;
			suffix = parts.length > 3 ? parts[3] : null;
			
			int repetitions = getRepetitions(loopVariable, context, 0);
			
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
	 * @param request
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private String expandNestedNullLoops(String request, Map<String, Object> context) throws Exception {
		String expandedRequest = new String(request);
		
		String prefix, suffix , loopVariable, xml;
		String parts[];
		while(expandedRequest.indexOf(NESTED_ITERATOR_NULL_VARIABLE_OPEN_TOKEN) > -1) {
			parts = splitByWholeSeparators(expandedRequest, new String[] {NESTED_ITERATOR_NULL_VARIABLE_OPEN_TOKEN, NESTED_ITERATOR_NULL_VARIABLE_CLOSE_TOKEN, NESTED_ITERATOR_NULL_BLOCK_END_TOKEN});
			
			prefix = parts.length > 0 ? parts[0] : null;
			loopVariable = parts.length > 1 ? parts[1] : null;
			xml = parts.length > 2 ? parts[2] : null;
			suffix = parts.length > 3 ? parts[3] : null;
			
			int repetitions = getRepetitions(loopVariable, context, 0);
			
			expandedRequest = prefix;
			
			for(int i = 0; i < repetitions; i++) {
				expandedRequest += addIndexToVariables(addIndexToNestedLoopVariables(xml, i), i);
			}
			
			expandedRequest += suffix;
		}
		
		
		return expandedRequest;
	}
	
	/**
	 * 
	 * @param request
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private String expandNestedNestedNullLoops(String request, Map<String, Object> context) throws Exception {
		String expandedRequest = new String(request);
		
		String prefix, suffix , loopVariable, xml;
		String parts[];
		while(expandedRequest.indexOf(NESTED_NESTED_ITERATOR_NULL_VARIABLE_OPEN_TOKEN) > -1) {
			parts = splitByWholeSeparators(expandedRequest, new String[] {NESTED_NESTED_ITERATOR_NULL_VARIABLE_OPEN_TOKEN, NESTED_NESTED_ITERATOR_NULL_VARIABLE_CLOSE_TOKEN, NESTED_NESTED_ITERATOR_NULL_BLOCK_END_TOKEN});
			
			prefix = parts.length > 0 ? parts[0] : null;
			loopVariable = parts.length > 1 ? parts[1] : null;
			xml = parts.length > 2 ? parts[2] : null;
			suffix = parts.length > 3 ? parts[3] : null;
			
			int repetitions = getRepetitions(loopVariable, context, 0);
			
			expandedRequest = prefix;
			
			for(int i = 0; i < repetitions; i++) {
				expandedRequest += addIndexToVariables(addIndexToNestedLoopVariables(xml, i), i);
			}
			
			expandedRequest += suffix;
		}
		
		
		return expandedRequest;
	}
	
	/**
	 * 
	 * @param request
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private String expandNestedLoops(String request, Map<String, Object> context) throws Exception {
		String expandedRequest = new String(request);
		
		String prefix, suffix , loopVariable, xml;
		String parts[];
		while(expandedRequest.indexOf(NESTED_ITERATOR_VARIABLE_OPEN_TOKEN) > -1) {
			parts = splitByWholeSeparators(expandedRequest, new String[] {NESTED_ITERATOR_VARIABLE_OPEN_TOKEN, NESTED_ITERATOR_VARIABLE_CLOSE_TOKEN, NESTED_ITERATOR_BLOCK_END_TOKEN});
			
			prefix = parts.length > 0 ? parts[0] : null;
			loopVariable = parts.length > 1 ? parts[1] : null;
			xml = parts.length > 2 ? parts[2] : null;
			suffix = parts.length > 3 ? parts[3] : null;
			
			int repetitions = getRepetitions(loopVariable, context, 0);
			
			expandedRequest = prefix;
			
			for(int i = 0; i < repetitions; i++) {
				expandedRequest += addIndexToVariables(addIndexToNestedLoopVariables(xml, i), i);
			}
			
			expandedRequest += suffix;
		}
		
		
		return expandedRequest;
	}
	
	/**
	 * 
	 * @param request
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private String expandNestedNestedLoops(String request, Map<String, Object> context) throws Exception {
		String expandedRequest = new String(request);
		
		String prefix, suffix , loopVariable, xml;
		String parts[];
		while(expandedRequest.indexOf(NESTED_NESTED_ITERATOR_VARIABLE_OPEN_TOKEN) > -1) {
			parts = splitByWholeSeparators(expandedRequest, new String[] {NESTED_NESTED_ITERATOR_VARIABLE_OPEN_TOKEN, NESTED_NESTED_ITERATOR_VARIABLE_CLOSE_TOKEN, NESTED_NESTED_ITERATOR_BLOCK_END_TOKEN});
			
			prefix = parts.length > 0 ? parts[0] : null;
			loopVariable = parts.length > 1 ? parts[1] : null;
			xml = parts.length > 2 ? parts[2] : null;
			suffix = parts.length > 3 ? parts[3] : null;
			
			int repetitions = getRepetitions(loopVariable, context, 0);
			
			expandedRequest = prefix;
			
			for(int i = 0; i < repetitions; i++) {
				expandedRequest += addIndexToVariables(addIndexToNestedLoopVariables(xml, i), i);
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
				indexedXml = StringUtils.replace(indexedXml, VARIABLE_SEPARATOR + vars[i] + VARIABLE_SEPARATOR, VARIABLE_SEPARATOR + vars[i] + "(" + (index + 1) + ")" + VARIABLE_SEPARATOR);
			}
		}
		
		
		return indexedXml;		
	}

	private String addIndexToLoopVariable(String open, String close, String xml, int index) {
		String indexedXml = new String(xml);
		String vars[] = StringUtils.substringsBetween(indexedXml, open, close);
		
		if(vars != null) {
			for(int i=0; i < vars.length; i++) {
				indexedXml = StringUtils.replace(indexedXml, open + vars[i] + close, open + vars[i] + "(" + (index + 1) + ")" + close);
			}
		}
		
		
		return indexedXml;		
	}	
	
	private String addIndexToNestedLoopVariables(String xml, int index) {
		String indexedXml = addIndexToLoopVariable(ITERATOR_VARIABLE_OPEN_TOKEN, ITERATOR_VARIABLE_CLOSE_TOKEN, xml, index);
		indexedXml = addIndexToLoopVariable(ITERATOR_NULL_VARIABLE_OPEN_TOKEN, ITERATOR_NULL_VARIABLE_CLOSE_TOKEN, xml, index);
		return indexedXml;
	}
	
	/**
	 * 
	 * @param loopVariable
	 * @param context
	 * @param minRepetitions
	 * @return
	 * @throws Exception
	 */
	private int getRepetitions(String loopVariable, Map<String, Object> context, int minRepetitions) throws Exception {
		try {
			Object reps = getFromContext(loopVariable, context);
			int repetitions = reps != null ? Integer.parseInt(reps.toString()) : minRepetitions;
			return repetitions >= 0 ? repetitions : minRepetitions;
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
				value = getFromContext(vars[i], context);
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
			i.eval("import ar.com.bunge.util.*");
			String bshScript = resolveScript(script, context);
	    	Object result = i.eval(bshScript);
	    	return result != null ? result.toString() : "";
		} catch(TargetError e) {
			if(e.getTarget() instanceof ValidationException) {
				throw new ValidationException(e.getTarget() != null ? e.getTarget().getMessage() : e.getMessage(), e);
			} else if(e.getTarget() instanceof Exception) {
				throw (Exception) e.getTarget();
			} else if(e.getTarget() instanceof Exception) {
				throw (Exception) e.getTarget();
			} else {
				throw new Exception(e.getTarget() != null ? e.getTarget().getMessage() : e.getMessage(), e);
			}
		} catch(EvalError e) {
			if(e.getCause() instanceof ValidationException) {
				throw new ValidationException(e.getCause().getMessage(), e);
			} else if (e.getCause() instanceof Exception) {
				throw (Exception) e.getCause();
			} else {
				throw new Exception(e.getCause() != null ? e.getCause().getMessage() : e.getMessage(), e.getCause());
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
