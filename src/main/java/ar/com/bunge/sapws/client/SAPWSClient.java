/*
 * File name: SAPWSClient.java
 * Creation date: 01/08/2009 08:58:27
 * Copyright Mindpool
 */
package ar.com.bunge.sapws.client;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since SPM 1.0
 *
 */
public class SAPWSClient {

	/**
	 * 
	 */
	public SAPWSClient() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CommandLineHelper cmdLine = new CommandLineHelper(args);
		if(!cmdLine.isValid()) {
			System.err.println(cmdLine.getUsage());
			System.exit(1);
		}
	}

}
