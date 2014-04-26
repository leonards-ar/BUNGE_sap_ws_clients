/*
 * File name: FileUtils.java
 * Creation date: 19/06/2010 11:30:35
 * Copyright Mindpool
 */
package ar.com.bunge.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since 1.0
 *
 */
public class FileUtils {
	private static final Logger LOG = Logger.getLogger(FileUtils.class);

	private static final String FILE_PATH_SEPARATOR = System.getProperty("file.separator") != null ? System.getProperty("file.separator") : "/";
	private static final String TRACE_FILE_SEPARATOR = "_";

	private static final String VARIABLE_INDEX_OPEN_TOKEN = "(";
	private static final String VARIABLE_INDEX_CLOSE_TOKEN = ")";
	
	/**
	 * 
	 */
	public FileUtils() {
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
	 * @return
	 * @throws IOException
	 */
	public static List<String> readFileLines(String file) throws IOException {
		List<String> lines = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			while( (line = reader.readLine()) != null) {
				lines.add(line);
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		
		return lines;		
	}
	
	private static int addLengths(int[] lengths)
	{
		if(lengths != null && lengths.length > 0) {
			int total = 0;
			for(int i = 0; i < lengths.length; i++)
			{
				total += lengths[i];
			}
			return total;
		} else {
			return 0;
		}
	}
	
	public static List<String> parseFixedLengthLine(String line, int[] lengths)
	{
		if(line != null && lengths != null && lengths.length > 0) {
			List<String> records = new ArrayList<String>(lengths.length);

			if(line.length() != addLengths(lengths)) {
				LOG.warn("The line (length: " + line.length() + ") is not of the expected length: " + addLengths(lengths));
			}

			String record;
			for(int i=0, j=0; i < lengths.length && j < line.length(); i++) {
				if(j + lengths[i] < line.length()) {
					record = line.substring(j, j + lengths[i]);
				} else {
					record = line.substring(j);
				}
				records.add(record);
				j += lengths[i];
			}
			
			return records;
		} else if(line != null) {
			List<String> records = new ArrayList<String>(1);
			records.add(line);
			return records;
		} else {
			return null;
		}
	}

	public static void main(String a[]) {
		List<String> l = parseFixedLengthLine("0000327734  35960      ", new int[] {10, 7, 6});
		System.out.println(l);
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
	 * @param file
	 * @param contents
	 * @throws IOException
	 */
	public static void writeFile(String file, byte[] contents) throws IOException {
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(file);
			os.write(contents);
			os.flush();
		} finally {
			if (os != null) {
				os.close();
			}
		}		
	}
	
	/**
	 * 
	 * @param path
	 * @param filename
	 * @return
	 */
	public static String buildFileName(String path, String filename) {
		StringBuffer completeFilename = new StringBuffer();
		
		if(filename != null) {
			if(path != null && !path.trim().endsWith(FILE_PATH_SEPARATOR)) {
				completeFilename.append(path.trim() + FILE_PATH_SEPARATOR);
			} else if(path != null) {
				completeFilename.append(path.trim());
			}
			completeFilename.append(filename);
		}
		
		return completeFilename.toString();
		
	}
	
	/**
	 * 
	 * @param tracePath
	 * @param prefix
	 * @param suffix
	 * @return
	 */
	public static String buildTraceFileName(String tracePath, String prefix, String suffix) {
		final SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd.HHmmss");
		StringBuffer filename = new StringBuffer();
		
		if(prefix != null) {
			if(tracePath != null && !tracePath.trim().endsWith(FILE_PATH_SEPARATOR)) {
				filename.append(tracePath.trim() + FILE_PATH_SEPARATOR);
			} else if(tracePath != null) {
				filename.append(tracePath.trim());
			}
			
			filename.append(prefix.trim());
			filename.append(TRACE_FILE_SEPARATOR);
			
			filename.append(df.format(new Date()));
			filename.append(TRACE_FILE_SEPARATOR);
			filename.append(suffix);
		}
		
		return filename.toString();
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static String fixIndexedVariableName(String name) {
		String fixedName = null;
		if(name != null && StringUtils.contains(name, VARIABLE_INDEX_OPEN_TOKEN) && StringUtils.contains(name, VARIABLE_INDEX_CLOSE_TOKEN)) {
			
			String indexStrs[] = StringUtils.substringsBetween(name, VARIABLE_INDEX_OPEN_TOKEN, VARIABLE_INDEX_CLOSE_TOKEN);
			// Add the name
			fixedName = name.substring(0, name.indexOf(VARIABLE_INDEX_OPEN_TOKEN));
			for(int i=0; i < indexStrs.length; i++) {
				String correctedIndex = null;
				try {
					correctedIndex = String.valueOf(Integer.parseInt(indexStrs[i], 10));
				} catch(Exception ex) {
					correctedIndex = indexStrs[i];
				}
				fixedName += VARIABLE_INDEX_OPEN_TOKEN + correctedIndex + VARIABLE_INDEX_CLOSE_TOKEN;
			}
		} else {
			fixedName = name;
		}
		
		return fixedName != null ? fixedName.toLowerCase() : null;
	}

	/**
	 * 
	 * @param contents
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> parseKeyValueString(String contents) throws Exception {
		Map<String, Object> keyValues = new HashMap<String, Object>();
		
		if(contents != null) {
			BufferedReader reader = null;
			List<String> lines = new ArrayList<String>();
			String line;
			try {
				reader = new BufferedReader(new StringReader(contents));
				while( (line = reader.readLine()) != null) {
					lines.add(line);
				}
			} finally {
				try { reader.close(); } catch(Exception ex) {}
			}
			parseKeyValueLines(lines, keyValues);
		}
	
		return keyValues;		
	}
	
	/**
	 * 
	 * @param lines
	 * @return
	 * @throws Exception
	 */
	private static void parseKeyValueLines(List<String> lines, Map<String, Object> keyValues) throws Exception {
		if(lines != null && lines.size() > 0 && keyValues != null) {
			String paramValue[];
			String name, value;
			for(Iterator<String> it = lines.iterator(); it.hasNext(); ) {
				paramValue = parseParameter(it.next());
				if(paramValue != null && paramValue.length == 2) {
					name = paramValue != null ? paramValue[0] : null;
					value = paramValue != null ? paramValue[1] : null;			
					if(!StringUtils.isEmpty(name) && !StringUtils.isEmpty(value)) {
						String key = fixIndexedVariableName(name);
						if(keyValues.containsKey(key)) {
							System.err.println("Repetido: " + name);
						}
						keyValues.put(key, value);
					}
				}
			}
		}
		
	}
	
	/**
	 * 
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> parseKeyValueFile(String filename) throws Exception {
		Map<String, Object> keyValues = new HashMap<String, Object>();
		
		if(filename != null) {
			List<String> lines = readFileLines(filename);
			parseKeyValueLines(lines, keyValues);
		}
	
		return keyValues;
	}	
	
	/**
	 * 
	 * @param keyValue
	 * @param file
	 * @throws Exception
	 */
	public static void writeKeyValueFile(Map<String, Object> keyValue, String file) throws Exception {
		StringBuffer contents = new StringBuffer();
		
		if(keyValue != null) {
			String aKey;
			for(Iterator<String> it = keyValue.keySet().iterator(); it.hasNext(); ) {
				aKey = it.next();
				contents.append(aKey + "=" + keyValue.get(aKey));
				if(it.hasNext()) {
					contents.append(getNewLine());
				}
			}
		}
		
		writeFile(file, contents.toString());
	}
	
	/**
	 * 
	 * @param paramValuePair
	 * @return
	 */
	public static String[] parseParameter(String paramValuePair) {
		if(paramValuePair != null && paramValuePair.trim().length() > 0 && !paramValuePair.startsWith("#")) {
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
	 * @param filename
	 * @return
	 */
	public static boolean existsFile(String filename) {
		File f = new File(filename);
		return f.exists();
	}
}
