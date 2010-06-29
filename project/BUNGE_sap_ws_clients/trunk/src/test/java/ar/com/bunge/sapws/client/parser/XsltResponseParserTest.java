package ar.com.bunge.sapws.client.parser;

import junit.framework.TestCase;

import java.io.FileInputStream;
import java.io.File;

/**
 * Docs
 */
public class XsltResponseParserTest extends TestCase {


    public XsltResponseParserTest(String name){

        super(name);

    }
    
    public void testParseResponse() throws Exception {

        XsltResponseParser parser = new XsltResponseParser();

        parser.setXsltFile(new FileInputStream(new File("C:\\federico\\workspace\\sap_ws_clients\\src\\test\\java\\ar\\com\\bunge\\sapws\\client\\parser\\XsltTransformTestStylesheet.xsl")));

        System.out.println(parser.parseResponse("<root><response>pepepepepe</response></root>"));

    }
}
