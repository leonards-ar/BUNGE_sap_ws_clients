package ar.com.bunge.sapws.client.parser;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;

import net.sf.saxon.Configuration;
import net.sf.saxon.FeatureKeys;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlSaxHandler;
import org.xml.sax.InputSource;

/**
 *
 * @author <a href="fbarreraoro@gmail.com">Federico Barrera Oro</a>
 * @version 1.0
 * @since 1.0
 */
public class XsltResponseParser implements ResponseParser {
    public static final String XSLT_FILE_PARAM = "xslt";
    
	/**
	 * 
	 * @param rawResponse
	 * @param context
	 * @return
	 * @throws Exception
	 * @see ar.com.bunge.sapws.client.parser.ResponseParser#parseResponse(java.lang.String, java.util.Map)
	 */
	public String parseResponse(String rawResponse, Map<String, Object> context) throws Exception {
		InputStream xsltFile = getXsltFileStream(context);
		
		try {
	        XmlObject xml = XmlObject.Factory.parse(rawResponse);
	
	        TransformerFactory transFactory = TransformerFactory.newInstance();
	        Configuration c = (Configuration) transFactory.getAttribute(FeatureKeys.CONFIGURATION);
	        c.setDOMLevel(2);
	
	        InputSource iSource = new InputSource(xsltFile);
	
	        SAXSource source = new SAXSource(iSource);
	
	        Transformer transform = transFactory.newTransformer(source);
	
	        XmlSaxHandler handler = XmlObject.Factory.newXmlSaxHandler();
	        SAXResult result = new SAXResult(handler.getContentHandler());
	        result.setLexicalHandler(handler.getLexicalHandler());
	
	        transform.transform(new DOMSource(xml.getDomNode()), result);
	
	        return handler.getObject().xmlText();

		} finally {
			if(xsltFile != null) {
				try {
					xsltFile.close();
				} catch(Throwable ex) {
					
				}
			}
		}
    }
	
	/**
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private InputStream getXsltFileStream(Map<String, Object> context) throws Exception {
		Object value = context.get(XSLT_FILE_PARAM);
		if(value != null) {
			return new FileInputStream(value.toString());
		} else {
			throw new Exception("Required parameter [" + XSLT_FILE_PARAM + "] missing from input variables. This parameter must hold the complete path to the XSLT file");
		}
	}
}
