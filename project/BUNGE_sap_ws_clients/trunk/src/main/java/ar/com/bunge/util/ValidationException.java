/*
 * File name: ValidationException.java
 * Creation date: Aug 26, 2009 3:18:26 PM
 * Copyright Mindpool
 */
package ar.com.bunge.util;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since SPM 1.0
 *
 */
public class ValidationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2397331759707629133L;

	/**
	 * 
	 */
	public ValidationException() {
	}

	/**
	 * @param message
	 */
	public ValidationException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ValidationException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ValidationException(String message, Throwable cause) {
		super(message, cause);
	}

}
