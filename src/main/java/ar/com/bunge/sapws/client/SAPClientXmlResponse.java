/*
 * File name: SAPClientXmlResponse.java
 * Creation date: Jul 28, 2009 5:15:47 PM
 * Copyright Mindpool
 */
package ar.com.bunge.sapws.client;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Node;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since SPM 1.0
 *
 */
public class SAPClientXmlResponse {
	private String response;
	private XmlObject xmlResponse;
	
	/**
	 * 
	 */
	public SAPClientXmlResponse() {
		this(null);
	}

	/**
	 * 
	 * @param response
	 */
	public SAPClientXmlResponse(String response) {
		super();
		setResponse(response);
	}
	
	/**
	 * @return the response
	 */
	public String getResponse() {
		return response;
	}

	/**
	 * @param response the response to set
	 */
	public void setResponse(String response) {
		this.response = response;
	}

	/**
	 * 
	 * @throws Exception
	 */
    public void parseResponse() throws Exception {
        try {
            setXmlResponse(XmlObject.Factory.parse(getResponse()));
        } catch (Throwable ex) {
        	throw new Exception(ex.getMessage(), ex);
        }
        Node result = getXmlResponse().getDomNode();
    }

	/**
	 * @return the xmlResponse
	 */
	private XmlObject getXmlResponse() {
		return xmlResponse;
	}

	/**
	 * @param xmlResponse the xmlResponse to set
	 */
	private void setXmlResponse(XmlObject xmlResponse) {
		this.xmlResponse = xmlResponse;
	}
	
}
