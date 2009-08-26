/*
 * File name: Utils.java
 * Creation date: Jul 28, 2009 4:46:23 PM
 * Copyright Mindpool
 */
package ar.com.bunge.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since 1.0
 *
 */
public class Utils {
	private static final String ENCODE_TOKEN = "_b64_";
	private static final String UNKNOWN_OBJECT = "-Unknown-";
	
	/**
	 * 
	 */
	private Utils() {
	}

	/**
	 * 
	 * @param o
	 * @param objectDescription
	 * @return
	 * @throws ValidationException
	 */
	public static Object required(Object o, String objectDescription) throws ValidationException {
		if(o != null && !"".equals(o.toString())) {
			return o;
		} else {
			throw new ValidationException(getErrorMessage("error.validation.required", objectDescription));
		}
	}
	
	/**
	 * 
	 * @param o
	 * @return
	 * @throws ValidationException
	 */
	public static Object required(Object o) throws ValidationException {
		return required(o, UNKNOWN_OBJECT);
	}

	/**
	 * 
	 * @param o
	 * @param objectDescription
	 * @return
	 * @throws ValidationException
	 */
	public static Object intNumber(Object o, String objectDescription) throws ValidationException {
		if(o instanceof Integer || o instanceof Long || o instanceof BigInteger || o instanceof Short || o instanceof Byte) {
			return o;
		} else if(o instanceof String) {
			try {
				Long.parseLong(o.toString());
				return o;
			} catch(Throwable ex) {
				throw new ValidationException(getErrorMessage("error.validation.int.number", objectDescription, o), ex);
			}
		} else {
			throw new ValidationException(getErrorMessage("error.validation.int.number", objectDescription, o));
		}
	}
	
	/**
	 * 
	 * @param o
	 * @return
	 * @throws ValidationException
	 */
	public static Object intNumber(Object o) throws ValidationException {
		return intNumber(o, UNKNOWN_OBJECT);
	}
	
	/**
	 * 
	 * @param o
	 * @param objectDescription
	 * @return
	 * @throws ValidationException
	 */
	public static Object decimalNumber(Object o, String objectDescription) throws ValidationException {
		if(o instanceof Integer || o instanceof Long || o instanceof BigInteger || o instanceof Short || o instanceof Byte || o instanceof Double || o instanceof Float || o instanceof BigDecimal) {
			return o;
		} else if(o instanceof String) {
			try {
				Double.parseDouble(o.toString());
				return o;
			} catch(Throwable ex) {
				throw new ValidationException(getErrorMessage("error.validation.decimal.number", objectDescription, o), ex);
			}
		} else {
			throw new ValidationException(getErrorMessage("error.validation.decimal.number", objectDescription, o));
		}
	}
	
	/**
	 * 
	 * @param o
	 * @return
	 * @throws ValidationException
	 */
	public static Object decimalNumber(Object o) throws ValidationException {
		return decimalNumber(o, UNKNOWN_OBJECT);
	}

	/**
	 * 
	 * @param s
	 * @param minLength
	 * @param strDescription
	 * @return
	 * @throws ValidationException
	 */
	public static String minLengthString(String s, int minLength, String strDescription) throws ValidationException {
		if(s != null && s.length() > minLength) {
			return s;
		} else {
			throw new ValidationException(getErrorMessage("error.validation.minlength.string", strDescription, s, new Integer(s != null ? s.length() : 0), minLength));
		}
	}

	/**
	 * 
	 * @param s
	 * @param minLength
	 * @return
	 * @throws ValidationException
	 */
	public static String minLengthString(String s, int minLength) throws ValidationException {
		return minLengthString(s, minLength, UNKNOWN_OBJECT);
	}
	
	/**
	 * 
	 * @param s
	 * @param maxLength
	 * @param strDescription
	 * @return
	 * @throws ValidationException
	 */
	public static String maxLengthString(String s, int maxLength, String strDescription) throws ValidationException {
		if(s != null && s.length() <= maxLength) {
			return s;
		} else {
			throw new ValidationException(getErrorMessage("error.validation.maxlength.string", strDescription, s, new Integer(s != null ? s.length() : 0), maxLength));
		}
	}

	/**
	 * 
	 * @param s
	 * @param maxLength
	 * @return
	 * @throws ValidationException
	 */
	public static String maxLengthString(String s, int maxLength) throws ValidationException {
		return maxLengthString(s, maxLength, UNKNOWN_OBJECT);
	}	
	
	/**
	 * 
	 * @param o
	 * @param from
	 * @param to
	 * @param objDescription
	 * @return
	 * @throws ValidationException
	 */
	public static Object numberInRange(Object o, double from, double to, String objDescription) throws ValidationException {
		decimalNumber(o, objDescription);
		double num = Double.parseDouble(o.toString());
		if(num >= from && num <= to) {
			return o;
		} else {
			throw new ValidationException(getErrorMessage("error.validation.number.range", objDescription, o, from, to));
		}
	}
	
	/**
	 * 
	 * @param o
	 * @param from
	 * @param to
	 * @return
	 * @throws ValidationException
	 */
	public static Object numberInRange(Object o, double from, double to) throws ValidationException {
		return numberInRange(o, from, to);
	}
	
	/**
	 * 
	 * @param o
	 * @return
	 */
	public static String trim(Object o) {
		return o != null ? o.toString().trim() : "";
	}

	/**
	 * 
	 * @param o
	 * @return
	 */
	public static String toLower(Object o) {
		return o != null ? o.toString().toLowerCase() : "";
	}
	
	/**
	 * 
	 * @param o
	 * @return
	 */
	public static String toUpper(Object o) {
		return o != null ? o.toString().toUpperCase() : "";
	}	
	
	/**
	 * 
	 * @param o
	 * @param size
	 * @param padChar
	 * @return
	 */
	public static String rightPad(Object o, int size, char padChar) {
		String s = o != null ? o.toString() : "";
		return StringUtils.rightPad(s, size, padChar);
	}
	
	/**
	 * 
	 * @param o
	 * @param size
	 * @param padChar
	 * @return
	 */
	public static String leftPad(Object o, int size, char padChar) {
		String s = o != null ? o.toString() : "";
		return StringUtils.leftPad(s, size, padChar);
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getNewLine() {
		return System.getProperty("line.separator") != null ? System.getProperty("line.separator") : "\n";
	}
	
	/**
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String readFile(String file) throws IOException {
		StringBuffer contents = new StringBuffer();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			while( (line = reader.readLine()) != null) {
				contents.append(line + getNewLine());
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		
		return contents.toString();
	}
	
	/**
	 * 
	 * @param file
	 * @param contents
	 * @throws IOException
	 */
	public static void writeFile(String file, String contents) throws IOException {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(contents);
			writer.flush();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	
	/**
	 * 
	 * @param str
	 * @param maxLength
	 * @return
	 */
	public static String truncate(String str, int maxLength) {
		if(str != null && str.length() > maxLength) {
			return str.substring(0, maxLength);
		} else {
			return str;
		}
	}
	
	/**
	 * 
	 * @param str
	 * @return
	 */
	public static String encode(String str) {
		try {
			Base64 b64 = new Base64();
			return ENCODE_TOKEN + (new String(b64.encode(str.getBytes())));
		} catch(Exception ex) {
			return str;
		}
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static String decode(String str) {
		try {
			Base64 b64 = new Base64();
			if(str != null && str.startsWith(ENCODE_TOKEN)) {
				return new String(b64.decode(StringUtils.remove(str, ENCODE_TOKEN).getBytes()));
			} else {
				return str;
			}
		} catch(Exception ex) {
			return str;
		}
	}
	
	/**
	 * 
	 * @param key
	 * @param args
	 * @return
	 */
	public static String getErrorMessage(String key, Object ... args) {
		try {
			String text = getBundleText(key);
			if(text != null) {
				return MessageFormat.format(text, args);
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
	public static String getBundleText(String key) {
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
}
