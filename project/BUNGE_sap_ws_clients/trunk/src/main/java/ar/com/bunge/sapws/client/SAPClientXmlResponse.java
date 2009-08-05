/*
 * File name: SAPClientXmlResponse.java
 * Creation date: Jul 28, 2009 5:15:47 PM
 * Copyright Mindpool
 */
package ar.com.bunge.sapws.client;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
	private Long number = new Long(0L);
	private String message;
	private String type;
	private String id;
	
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
        parseReturn();
    }

    /**
     * 
     * @throws Exception
     */
    private void parseReturn() throws Exception {
    	Document doc1 = (Document) getXmlResponse().getDomNode();
	   DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	   Document doc2 = builder.newDocument();

    	   Element newRoot = (Element) doc2.importNode (doc1.getDocumentElement(), true);
    	   doc2.appendChild (newRoot);    	
        Node result = getReturnNode(doc2);
        
        if(result != null && result.getChildNodes() != null) {
        	Node n;
            for(int i=0; i < result.getChildNodes().getLength(); i++) {
            	n = result.getChildNodes().item(i);
            	if("type".equalsIgnoreCase(n.getNodeName())) {
            		setType(n.getTextContent());
            	} else if("id".equalsIgnoreCase(n.getNodeName())) {
            		setId(n.getTextContent());
            	} else if("number".equalsIgnoreCase(n.getNodeName())) {
            		setNumber(new Long(n.getTextContent()));
            	} else if("message".equalsIgnoreCase(n.getNodeName())) {
            		setMessage(n.getTextContent());
            	}
            }
        }
    }
    
    /**
     * 
     * @param parent
     * @return
     * @throws Exception
     */
    private Node getReturnNode(Node node) throws Exception {
    	if("return".equalsIgnoreCase(node.getNodeName())) {
    		return node;
    	} else if(node == null || node.getChildNodes() == null || node.getChildNodes().getLength() <= 0) {
    		return null;
    	} else {
    		for(int i = 0; i < node.getChildNodes().getLength(); i++) {
    			Node n = getReturnNode(node.getChildNodes().item(i));
    			if(n != null) {
    				return n;
    			}
    		}
    		return null;
    	}
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

	/**
	 * @return the number
	 */
	public Long getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(Long number) {
		this.number = number;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isSuccess() {
		return getNumber() != null && getNumber().longValue() == 0L;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
}
