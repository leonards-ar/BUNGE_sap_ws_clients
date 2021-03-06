/*
 * File name: BaseResponseParser.java
 * Creation date: 19/06/2010 19:23:34
 * Copyright Mindpool
 */
package ar.com.bunge.sapws.client.parser;

import java.io.StringWriter;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ar.com.bunge.util.FileUtils;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since 1.0
 *
 */
public abstract class BaseResponseParser implements ResponseParser {
	private static final Logger LOG = Logger.getLogger(BaseResponseParser.class);

	protected static final String LINE_TOKEN = ";";
	protected static final String SUCCESS_MESSAGE_SEPARATOR = LINE_TOKEN;
	protected static final String REPLACEMENT_VALUE_TOKEN = ",";
	protected static final String LINE_SEPARATOR = FileUtils.getNewLine();
	private static final String ERROR_RESULT = "ERROR";
	private static final String ERROR_MESSAGE_SEPARATOR = FileUtils.getNewLine();
	
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
    	if(nodeName == null || node == null) {
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
    
    protected String nodeToXmlString(Node node) throws Exception {
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter sw = new StringWriter();
        t.transform(new DOMSource(node), new StreamResult(sw));
        return sw.toString();
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
    	if(childNode != null) {
    		return StringUtils.replace(childNode.getTextContent(), SUCCESS_MESSAGE_SEPARATOR, REPLACEMENT_VALUE_TOKEN);
    	} else {
    		LOG.debug("No such child node [" + nodeName + "] in node [" + (node != null ? node.getNodeName() : "null") + "]");
    		return "";
    	}
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
		StringBuffer errorResult = new StringBuffer(ERROR_RESULT);

		errorResult.append(ERROR_MESSAGE_SEPARATOR);
		errorResult.append(errorNumber);
		errorResult.append(LINE_TOKEN);
		errorResult.append(message);
		errorResult.append(LINE_TOKEN);
		
		return errorResult.toString();
	}   
	

	/**
	 * 
	 * @param status
	 * @param responseNode
	 * @param valueNodeNames
	 * @return
	 * @throws Exception
	 */
	protected String buildResultRow(String status, Node responseNode, String valueNodeNames[]) throws Exception {
		StringBuffer response = new StringBuffer();
		
		if(status != null) {
			response.append(status + LINE_TOKEN);
		}
		
		String value;
		for(int i=0; i < valueNodeNames.length; i++) {
			value = getNodeText(responseNode, valueNodeNames[i]);
			if(value != null) {
				response.append(value.trim());
			}
			
			// Jose wants a ; at the end of each row
			if(i < valueNodeNames.length) {
				response.append(LINE_TOKEN);
			}
		}
		return response.toString();
	}
	
	/**
	 * 
	 * @param responseNode
	 * @param valueNodeNames
	 * @return
	 * @throws Exception
	 */
	protected String buildResultRow(Node responseNode, String valueNodeNames[]) throws Exception {
		return buildResultRow(null, responseNode, valueNodeNames);
	}
}
