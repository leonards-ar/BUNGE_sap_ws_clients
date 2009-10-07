/*
 * File name: CommandLineHelper.java
 * Creation date: 01/08/2009 08:59:02
 * Copyright Mindpool
 */
package ar.com.bunge.sapws.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import ar.com.bunge.util.Utils;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since 1.0
 *
 */
public class CommandLineHelper {
	private static final Map<String, String> PARAMS = new HashMap<String, String>();
	private static List<String> REQUIRED_PARAMS = new ArrayList<String>();
	
	private Map<String, Object> variables = new HashMap<String, Object>();
	private Map<String, String> parameters = new HashMap<String, String>();
	
	private List<String> errors = new ArrayList<String>();
	
	
	
	static {
		PARAMS.put("b", "basic_auth.help");
		PARAMS.put("i", "input_file.help");
		PARAMS.put("o", "output_file.help");
		PARAMS.put("u", "username.help");
		PARAMS.put("p", "password.help");
		PARAMS.put("url", "url.help");
		
		REQUIRED_PARAMS.add("i");
		REQUIRED_PARAMS.add("u");
		REQUIRED_PARAMS.add("p");
		REQUIRED_PARAMS.add("url");
	}
	
	/**
	 * 
	 */
	public CommandLineHelper(String[] args) {
		super();
		parseArgs(args);
	}

	/**
	 * 
	 * @param args
	 */
	private void parseArgs(String args[]) {
		if(args != null) {
			String name, value;
			for(int i=0; i < args.length; i++) {
				String paramValue[] = parseParameter(args[i]);
				name = paramValue != null ? paramValue[0] : null;
				value = paramValue != null ? paramValue[1] : null;

				if(!StringUtils.isEmpty(name) && !StringUtils.isEmpty(value)) {
					if(PARAMS.containsKey(name)) {
						getParameters().put(name, value);
					} else {
						getVariables().put(name, value);
					}
				} else if(!StringUtils.isEmpty(name) && StringUtils.isEmpty(value) && PARAMS.containsKey(name)) {
					getErrors().add(getErrorMessage("error.missing_arg_value.message", name, value));
				}
			}
		}
		// Validate required parameters
		for(String param : REQUIRED_PARAMS) {
			if(!getParameters().containsKey(param)) {
				getErrors().add(getErrorMessage("error.missing_arg_value.message", param, null));
			}
		}
	}

	/**
	 * 
	 * @param paramValuePair
	 * @return
	 */
	private String[] parseParameter(String paramValuePair) {
		if(paramValuePair != null) {
			String[] parsed = new String[2];
			
			int i = paramValuePair.indexOf('=');
			
			if(i > 0 && i < paramValuePair.length() - 1) {
				parsed[0] = paramValuePair.substring(0, i).trim().toLowerCase();
				parsed[1] = paramValuePair.substring(i + 1).trim();
			} else if(i == 0) {
				parsed[0] = null;
				parsed[1] = paramValuePair.substring(1).trim();
			} else if(i == paramValuePair.length() - 1) {
				parsed[0] = paramValuePair.substring(0, paramValuePair.length() - 1).trim().toLowerCase();
				parsed[1] = null;
			} else {
				parsed[0] = paramValuePair.trim().toLowerCase();
				parsed[1] = null;
			}
			return parsed;
		} else {
			return null;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isValid() {
		return getErrors() == null || getErrors().size() <= 0;
	}
	
	/**
	 * 
	 * @param key
	 * @param name
	 * @param value
	 * @return
	 */
	private String getErrorMessage(String key, String name, String value) {
		return Utils.getErrorMessage(key, name, value);
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	private String getBundleText(String key) {
		return Utils.getBundleText(key);
	}
	
	/**
	 * @return the variables
	 */
	public Map<String, Object> getVariables() {
		return variables;
	}

	/**
	 * @return the parameters
	 */
	private Map<String, String> getParameters() {
		return parameters;
	}

	/**
	 * @return the errors
	 */
	public List<String> getErrors() {
		return errors;
	}
	
	/**
	 * 
	 * @param paramName
	 * @return
	 */
	public String getParameter(String paramName) {
		return getParameters().get(paramName);
	}
	
	/**
	 * 
	 * @param paramName
	 * @return
	 */
	public String getParameterHelp(String paramName) {
		if(paramName != null) {
			return paramName + ": " + getBundleText(PARAMS.get(paramName.toLowerCase()));
		} else {
			return null;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public String getParametersHelp() {
		StringBuffer sb = new StringBuffer();
		for(Iterator<String> it = PARAMS.keySet().iterator(); it.hasNext(); ) {
			sb.append("\t\t- " + getParameterHelp(it.next()));
			if(it.hasNext()) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getUsage() {
		StringBuffer sb = new StringBuffer( getBundleText("command.label") + " ");
		// Required first
		for(Iterator<String> it = REQUIRED_PARAMS.iterator(); it.hasNext(); ) {
			String param = it.next();
			sb.append(param + "=" + getBundleText("value.label") + " ");
		}
		
		// Optional params
		for(Iterator<String> it = PARAMS.keySet().iterator(); it.hasNext(); ) {
			String param = it.next();
			if(!REQUIRED_PARAMS.contains(param)) {
				sb.append("[" + param + "=" + getBundleText("value.label") + "] ");
			}	
		}

		// Variables
		sb.append(getBundleText("variables.label"));

		// Errors
		if(!isValid()) {
			sb.append("\n" + getBundleText("errors.title"));
			sb.append("\n");
			for(Iterator<String> it = getErrors().iterator(); it.hasNext(); ) {
				String error = it.next();
				sb.append("\t\t- " + error);
				if(it.hasNext()) {
					sb.append("\n");
				}
			}
		}
		
		// Parameters Help
		sb.append("\n" + getBundleText("parameters.title"));
		sb.append("\n");
		sb.append(getParametersHelp());
		return sb.toString();
	}
}
