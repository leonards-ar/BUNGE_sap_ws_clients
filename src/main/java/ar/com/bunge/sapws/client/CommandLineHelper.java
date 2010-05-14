/*
 * File name: CommandLineHelper.java
 * Creation date: 01/08/2009 08:59:02
 * Copyright Mindpool
 */
package ar.com.bunge.sapws.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private static final String GLOBAL_KEY = "_global";
	
	private static final Map<String, String> PARAMS = new HashMap<String, String>();
	private static final Map<String, List<String>> REQUIRED_PARAMS = new HashMap<String, List<String>>();
	
	private Map<String, Object> variables = new HashMap<String, Object>();
	private Map<String, String> parameters = new HashMap<String, String>();
	
	private List<String> errors = new ArrayList<String>();
	
	
	
	static {
		PARAMS.put("b", "basic_auth.help");
		PARAMS.put("w", "wss_auth.help");
		PARAMS.put("i", "input_file.help");
		PARAMS.put("o", "output_file.help");
		PARAMS.put("u", "username.help");
		PARAMS.put("p", "password.help");
		PARAMS.put("url", "url.help");
		PARAMS.put("v", "variables.help");
		PARAMS.put("ssl", "ssl_auth.help");
		PARAMS.put("ks", "keystore.help");
		PARAMS.put("ksp", "keystore_password.help");
		PARAMS.put("px", "proxy.help");
		PARAMS.put("pxp", "proxy_port.help");
		
		List<String> global = new ArrayList<String>();
		global.add("i");
		global.add("url");
		REQUIRED_PARAMS.put(GLOBAL_KEY, global);

		List<String> sslAuth = new ArrayList<String>();
		sslAuth.add("ks");
		sslAuth.add("ksp");
		REQUIRED_PARAMS.put("ssl", sslAuth);
		
		List<String> otherAuth = new ArrayList<String>();
		otherAuth.add("u");
		otherAuth.add("p");		
		REQUIRED_PARAMS.put("b", otherAuth);
		REQUIRED_PARAMS.put("w", otherAuth);
		
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
		Set<String> requiredParams = new HashSet<String>();
		requiredParams.addAll(REQUIRED_PARAMS.get(GLOBAL_KEY));
		if(args != null) {
			String name, value;
			
			for(int i=0; i < args.length; i++) {
				String paramValue[] = Utils.parseParameter(args[i]);
				name = paramValue != null ? paramValue[0] : null;
				value = paramValue != null ? paramValue[1] : null;

				if(!StringUtils.isEmpty(name) && !StringUtils.isEmpty(value)) {
					if(PARAMS.containsKey(name)) {
						getParameters().put(name, value);
						if(REQUIRED_PARAMS.containsKey(name)) {
							requiredParams.addAll(REQUIRED_PARAMS.get(name));
						}
					} else {
						getVariables().put(name, value);
					}
				} else if(!StringUtils.isEmpty(name) && StringUtils.isEmpty(value) && PARAMS.containsKey(name)) {
					getErrors().add(getErrorMessage("error.missing_arg_value.message", name, value));
				}
			}
		}
		// Validate required parameters
		for(String param : requiredParams) {
			if(!getParameters().containsKey(param)) {
				getErrors().add(getErrorMessage("error.missing_arg_value.message", param, null));
			}
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
	 * @param param
	 * @return
	 */
	private boolean isOptionalParameter(String param) {
		for(Iterator<String> keys = REQUIRED_PARAMS.keySet().iterator(); keys.hasNext(); ) {
			String key = keys.next();
			if(key != null && key.equals(param)) {
				return false;
			} else {
				if(key != null && REQUIRED_PARAMS.get(key).contains(param)) {
					return false;
				}					
			}
			
		}
		return true;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getUsage() {
		StringBuffer sb = new StringBuffer( getBundleText("command.label") + " ");
		// Required first
		for(Iterator<String> it = REQUIRED_PARAMS.get(GLOBAL_KEY).iterator(); it.hasNext(); ) {
			String param = it.next();
			sb.append(param + "=" + getBundleText("value.label") + " ");
		}

		// Dependant
		sb.append("[");
		for(Iterator<String> keys = REQUIRED_PARAMS.keySet().iterator(); keys.hasNext(); ) {
			String key = keys.next();
			if(!GLOBAL_KEY.equals(key)) {
				sb.append(key + "=" + getBundleText("value.label"));
				for(Iterator<String> it = REQUIRED_PARAMS.get(key).iterator(); it.hasNext(); ) {
					String param = it.next();
					sb.append(" " + param + "=" + getBundleText("value.label"));
				}
				if(keys.hasNext()) {
					sb.append(" | ");
				}
			}
		}
		sb.append("] ");
		
		// Optional params
		for(Iterator<String> it = PARAMS.keySet().iterator(); it.hasNext(); ) {
			String param = it.next();
			if(isOptionalParameter(param)) {
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
