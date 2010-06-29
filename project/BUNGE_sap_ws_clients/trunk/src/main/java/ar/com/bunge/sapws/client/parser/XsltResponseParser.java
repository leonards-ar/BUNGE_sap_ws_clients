package ar.com.bunge.sapws.client.parser;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlSaxHandler;
import net.sf.saxon.Configuration;
import net.sf.saxon.FeatureKeys;
import org.xml.sax.InputSource;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;

import java.io.InputStream;
import java.io.FileInputStream;

/**
 *
 * @author <a href="fbarreraoro@gmail.com">Federico Barrera Oro</a>
 * @version 1.0
 * @since 1.0
 */
public class XsltResponseParser implements ResponseParser {

    private InputStream xsltFile;
    
    public String parseResponse(String rawResponse) throws Exception {
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

    }

    public void setXsltFile(InputStream xsltFile) {
        this.xsltFile = xsltFile;
    }
}
