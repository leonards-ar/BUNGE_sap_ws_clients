package ar.com.bunge.sapws.client.parser;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Docs
 */
public class XsltResponseParserTest extends TestCase {


    public XsltResponseParserTest(String name){

        super(name);

    }
    
    public void testParseResponse() throws Exception {

        XsltResponseParser parser = new XsltResponseParser();
        Map<String, Object> context = new HashMap<String, Object>();
        
        context.put(XsltResponseParser.XSLT_FILE_PARAM, "D:\\Development\\eclipse\\workspace\\sap-ws-clients\\src\\test\\java\\ar\\com\\bunge\\sapws\\client\\parser\\XsltTransformTestStylesheet.xsl");

        System.out.println(parser.parseResponse("<root><response>pepepepepe</response></root>", context));

    }
}
