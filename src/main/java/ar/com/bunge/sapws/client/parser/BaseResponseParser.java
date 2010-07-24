/*
 * File name: BaseResponseParser.java
 * Creation date: 19/06/2010 19:23:34
 * Copyright Mindpool
 */
package ar.com.bunge.sapws.client.parser;

import java.util.Map;

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
public abstract class BaseResponseParser implements ResponseParser {

	/**
	 * 
	 */
	public BaseResponseParser() {
	}

	/**
	 * 
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	protected Document getXmlDocumentFromString(String xml) throws Exception {
		XmlObject xmlObject = XmlObject.Factory.parse(xml);
		
    	Document doc1 = (Document) xmlObject.getDomNode();
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc2 = builder.newDocument();

		Element newRoot = (Element) doc2.importNode(doc1.getDocumentElement(), true);
		doc2.appendChild(newRoot);		
		
		return doc2;
	}
    /**
	 * 
	 * @param parent
	 * @return
	 * @throws Exception
	 */
	protected Node getNode(Node node, String nodeName) throws Exception {
    	if(nodeName == null) {
    		return null;
    	} else if(nodeName.equalsIgnoreCase(node.getNodeName())) {
    		return node;
    	} else if(node == null || node.getChildNodes() == null || node.getChildNodes().getLength() <= 0) {
    		return null;
    	} else {
    		for(int i = 0; i < node.getChildNodes().getLength(); i++) {
    			Node n = getNode(node.getChildNodes().item(i), nodeName);
    			if(n != null) {
    				return n;
    			}
    		}
    		return null;
    	}
    }	
    
    /**
     * 
     * @param node
     * @param nodeName
     * @return
     * @throws Exception
     */
    protected String getNodeText(Node node, String nodeName) throws Exception {
    	Node childNode = getNode(node, nodeName);
    	return childNode != null ? childNode.getTextContent() : null;
    }
    
	/**
	 * 
	 * @param errorNumber
	 * @param message
	 * @param context
	 * @return
	 * @throws Exception
	 * @see ar.com.bunge.sapws.client.parser.ResponseParser#parseError(java.lang.Long, java.lang.String, java.util.Map)
	 */
	public String parseError(Long errorNumber, String message, Map<String, Object> context) throws Exception {
		// Default implementation. Return null and so just an Exception will be thrown
		return null;
	}    
}
