/*
 * File name: AfipWSAAResponseParser.java
 * Creation date: 19/06/2010 19:09:53
 * Copyright Mindpool
 */
package ar.com.bunge.sapws.client.parser;

import java.util.Map;

import org.w3c.dom.Document;

import ar.com.bunge.util.FileUtils;
import ar.com.bunge.util.Utils;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since 1.0
 *
 */
public class AfipWSAAResponseParser extends BaseResponseParser {

	public static final String TICKET_TOKEN_KEY = "token";
	public static final String TICKET_SIGN_KEY = "sign";
	public static final String TICKET_EXPIRATION_TIME_KEY = "expiration";
	public static final String TICKET_GENERATION_TIME_KEY = "generation";
	public static final String TICKET_EXPIRATION_TIME_STR_KEY = "expiration_str";
	public static final String TICKET_GENERATION_TIME_STR_KEY = "generation_str";
	public static final String TICKET_UNIQUE_ID_KEY = "unique_id";
	public static final String TICKET_SRC_KEY = "source";
	public static final String TICKET_DEST_KEY = "destination";
	
	private static final String NEW_LINE = FileUtils.getNewLine();
	
	/**
	 * 
	 */
	public AfipWSAAResponseParser() {
	}

	/**
	 * 
	 * @param rawResponse
	 * @param context
	 * @return
	 * @throws Exception
	 * @see ar.com.bunge.sapws.client.parser.ResponseParser#parseResponse(java.lang.String, java.util.Map)
	 */
	public String parseResponse(String rawResponse, Map<String, Object> context) throws Exception {
		Document doc = getXmlDocumentFromString(rawResponse);
		Document resp = getXmlDocumentFromString(getNodeText(doc, "loginCmsReturn"));
		StringBuffer response = new StringBuffer();
		
		response.append(TICKET_TOKEN_KEY + "=" + getNodeText(resp, "token") + NEW_LINE);
		response.append(TICKET_SIGN_KEY + "=" + getNodeText(resp, "sign") + NEW_LINE);
		response.append(TICKET_UNIQUE_ID_KEY + "=" + getNodeText(resp, "uniqueId") + NEW_LINE);
		response.append(TICKET_SRC_KEY + "=" + getNodeText(resp, "source") + NEW_LINE);
		response.append(TICKET_DEST_KEY + "=" + getNodeText(resp, "destination") + NEW_LINE);
		response.append(TICKET_EXPIRATION_TIME_STR_KEY + "=" + getNodeText(resp, "expirationTime") + NEW_LINE);
		response.append(TICKET_GENERATION_TIME_STR_KEY + "=" + getNodeText(resp, "generationTime") + NEW_LINE);
		response.append(TICKET_EXPIRATION_TIME_KEY + "=" + Utils.isoStringToDate(getNodeText(resp, "expirationTime")).getTime() + NEW_LINE);
		response.append(TICKET_GENERATION_TIME_KEY + "=" + Utils.isoStringToDate(getNodeText(resp, "generationTime")).getTime());
		
		return response.toString();
	}

}
