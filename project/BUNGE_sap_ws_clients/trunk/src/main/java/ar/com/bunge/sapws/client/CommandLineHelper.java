/*
 * File name: CommandLineHelper.java
 * Creation date: 01/08/2009 08:59:02
 * Copyright Mindpool
 */
package ar.com.bunge.sapws.client;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since SPM 1.0
 *
 */
public class CommandLineHelper {
	private static final Map<String, String> PARAMS = new HashMap<String, String>();
	private static List<String> REQUIRED_PARAMS = new ArrayList<String>();
	
	private Map<String, String> variables = new HashMap<String, String>();
	private Map<String, String> parameters = new HashMap<String, String>();
	
	private List<String> errors = new ArrayList<String>();
	
	
	
	static {
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
				StringTokenizer st = new StringTokenizer(args[i], "=:");
				name = st.hasMoreTokens() ? st.nextToken().trim().toLowerCase() : null;
				value = st.hasMoreElements() ? st.nextToken().trim() : null;

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
		try {
			String text = getBundleText(key);
			if(text != null) {
				return MessageFormat.format(text, name, value);
			} else {
				return key;
			}
		} catch(Throwable ex) {
			return key;
		}
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	private String getBundleText(String key) {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle("messages");
			if(bundle != null) {
				return bundle.getString(key);
			} else {
				return key;
			}
		} catch(Throwable ex) {
			return key;
		}
	}
	
	/**
	 * @return the variables
	 */
	public Map<String, String> getVariables() {
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
