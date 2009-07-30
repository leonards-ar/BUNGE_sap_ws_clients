/*
 * File name: Utils.java
 * Creation date: Jul 28, 2009 4:46:23 PM
 * Copyright Mindpool
 */
package ar.com.bunge.util;

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
	
}
