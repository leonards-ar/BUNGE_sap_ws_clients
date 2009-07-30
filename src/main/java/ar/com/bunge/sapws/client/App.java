package ar.com.bunge.sapws.client;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.ws.client.core.WebServiceTemplate;

import bsh.Interpreter;

/**
 * Hello world!
 *
 */
public class App 
{
	private final WebServiceTemplate webServiceTemplate = new WebServiceTemplate();

	private final String MESSAGE = "<urn:doSpellingSuggestion soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><key xsi:type=\"xsd:string\">ABQIAAAA51sPsH4l_w-4q_I3VuV2cxT2yXp_ZAY8_ufC3CFXhHIE1NvwkxSiYvxeUOS0JQ1fIvkQ0mKPaYPjQA</key><phrase xsi:type=\"xsd:string\">hola mundo</phrase></urn:doSpellingSuggestion>";
	
	/**
	 * 
	 */
	public App() {
	}
	
    // send to an explicit URI
    public void customSendAndReceive() {
        StreamSource source = new StreamSource(new StringReader(MESSAGE));
        StreamResult result = new StreamResult(System.out);
        webServiceTemplate.sendSourceAndReceiveToResult("http://api.google.com/search/beta2", source, result);
    }

    public static void main(String args[]) {
    	try {
    		SAPClientXmlRequest req = new SAPClientXmlRequest();
    		req.setRequestTemplate("<xml><param1>${Utils.trim(#issue.id#)}</param1><param1>${Utils.leftPad(#issue.id#, 20, '*')}</param1></xml>");
    		Map<String, Object> c = new HashMap<String, Object>();
    		c.put("issue.id", "XXX    ");
        	req.compile(c);
        	
        	System.out.println(req.getRequest());
    	} catch(Throwable ex) {
    		ex.printStackTrace();
    	}
    }
}
