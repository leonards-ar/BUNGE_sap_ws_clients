/*
 * File name: FileConverter.java
 * Creation date: 26/08/2012 10:47:42
 * Copyright Mindpool
 */
package ar.com.bunge.sapws.converter;

import java.util.Map;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since 1.0
 *
 */
public interface FileConverter {

	String convert(String inputFile, Map<String, Object> context) throws Exception;
}
