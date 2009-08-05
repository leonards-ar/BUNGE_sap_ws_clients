package ar.com.bunge.sapws.client;

import java.io.StringReader;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.log4j.Logger;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.CommonsHttpMessageSender;

import ar.com.bunge.util.Utils;

/**
 * Hello world!
 *
 */
public class App 
{
	private static final Logger LOG = Logger.getLogger(App.class);	

	private final WebServiceTemplate webServiceTemplate = new WebServiceTemplate();

	private final String MESSAGE = "<urn:doSpellingSuggestion soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><key xsi:type=\"xsd:string\">ABQIAAAA51sPsH4l_w-4q_I3VuV2cxT2yXp_ZAY8_ufC3CFXhHIE1NvwkxSiYvxeUOS0JQ1fIvkQ0mKPaYPjQA</key><phrase xsi:type=\"xsd:string\">hola mundo</phrase></urn:doSpellingSuggestion>";
	
	/**
	 * 
	 */
	public App() {
	}
	
    // send to an explicit URI
    public void customSendAndReceive() {
    	try {
            StreamSource source = new StreamSource(new StringReader(Utils.readFile("/home/mariano/Development/leonards/eclipse/workspace/sap-ws-clients/src/main/docs/req1.xml")));
            StreamResult result = new StreamResult(System.out);
            //webServiceTemplate.setMessageFactory(new SaajSoapMessageFactory());

            // Start HTTP Basic Authentication
            CommonsHttpMessageSender sender = new CommonsHttpMessageSender();
            sender.setCredentials(new UsernamePasswordCredentials("ugensapapar", "FREET238"));
            HttpClient client = new HttpClient();
            client.getParams().setAuthenticationPreemptive(true);
            sender.setHttpClient(client);
            
            sender.afterPropertiesSet();
            webServiceTemplate.setMessageSender(sender);
            webServiceTemplate.sendSourceAndReceiveToResult("http://bungesapqas.bar.sa.dir.bunge.com:8001/sap/bc/srt/rfc/sap/BAPI_PROJECTDEF_UPDATE4?sap-client=900", source, result);
            // End HTTP Basic Authentication
            
            
            // WSSE Auth
            //webServiceTemplate.sendSourceAndReceiveToResult("http://bungesapqas.bar.sa.dir.bunge.com:8001/sap/bc/srt/rfc/sap/BAPI_PROJECTDEF_UPDATE4?sap-client=900", source, new WSSEHeaderWebServiceMessageCallback("username", "password"), result);
    	} catch(Throwable e) {
    		e.printStackTrace();
    	}

    }

    public static void main(String args[]) {
    	new App().customSendAndReceive();
    	/*
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
    	*/
    }
}
