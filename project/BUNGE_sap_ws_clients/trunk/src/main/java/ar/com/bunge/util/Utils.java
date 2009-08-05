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

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since SPM 1.0
 *
 */
public class Utils {

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
}
