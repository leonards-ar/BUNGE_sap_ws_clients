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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since SPM 1.0
 *
 */
public class Utils {
	private static final String ENCODE_TOKEN = "_b64_";
	
	/**
	 * 
	 */
	private Utils() {
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
}
