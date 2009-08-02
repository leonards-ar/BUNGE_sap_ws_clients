/*
 * File name: WSSEHeaderWebServiceMessageCallback.java
 * Creation date: 01/08/2009 21:28:22
 * Copyright Mindpool
 */
package ar.com.bunge.sapws.client;

import java.io.IOException;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.TransformerException;

import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since SPM 1.0
 *
 */
public class WSSEHeaderWebServiceMessageCallback implements WebServiceMessageCallback {
	public static final String WSS_10_NAMESPACE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    public final static String WSSE_SECURITY_ELEMENT = "Security";
    public final static String WSSE_SECURITY_PREFIX = "wsse";
    public final static String WSSE_USERNAMETOKEN_ELEMENT = "UsernameToken";
    public final static String WSSE_USERNAME_ELEMENT = "Username";
    public final static String WSSE_PASSWORD_ELEMENT = "Password";

    private String username;
    private String password;

	/**
	 * 
	 */
	public WSSEHeaderWebServiceMessageCallback() {
		this(null, null);
	}

	/**
	 * 
	 * @param username
	 * @param pasword
	 */
	public WSSEHeaderWebServiceMessageCallback(String username, String password) {
		super();
		setUsername(username);
		setPassword(password);
	}
	
	/**
	 * @param message
	 * @throws IOException
	 * @throws TransformerException
	 * @see org.springframework.ws.client.core.WebServiceMessageCallback#doWithMessage(org.springframework.ws.WebServiceMessage)
	 */
	public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
    	if(getUsername() != null && getPassword() != null) {
    		try {
                // You have to use the default SAAJWebMessageFactory 
                SaajSoapMessage saajSoapMessage = (SaajSoapMessage) message;

                SOAPMessage soapMessage = saajSoapMessage.getSaajMessage();
                SOAPPart soapPart = soapMessage.getSOAPPart();
                SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
                SOAPHeader soapHeader = soapEnvelope.getHeader();

            	//soapEnvelope.addAttribute(soapEnvelope.createName("sug"), "http://www.sugarcrm.com/sugarcrm");
            	//soapEnvelope.addAttribute(soapEnvelope.createName("soapenv"), "http://schemas.xmlsoap.org/soap/envelope/");

                soapEnvelope.removeNamespaceDeclaration(soapEnvelope.getPrefix());
                soapEnvelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
                soapEnvelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
                soapEnvelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
                soapEnvelope.addNamespaceDeclaration("sug", "http://www.sugarcrm.com/sugarcrm");

            	// Add the WS-Security Header Element
                Name headerElementName = soapEnvelope.createName(WSSE_SECURITY_ELEMENT, WSSE_SECURITY_PREFIX, WSS_10_NAMESPACE);
                SOAPHeaderElement soapHeaderElement = soapHeader.addHeaderElement(headerElementName);

                soapHeaderElement.setMustUnderstand(true);

                // Add the usernameToken to "Security" soapHeaderElement
                SOAPElement usernameTokenSOAPElement = soapHeaderElement.addChildElement(WSSE_USERNAMETOKEN_ELEMENT);

                // Add the username to usernameToken
                SOAPElement userNameSOAPElement = usernameTokenSOAPElement.addChildElement(WSSE_USERNAME_ELEMENT);
                userNameSOAPElement.addTextNode(getUsername());

                // Add the password to usernameToken
                SOAPElement passwordSOAPElement = usernameTokenSOAPElement.addChildElement(WSSE_PASSWORD_ELEMENT);
                passwordSOAPElement.addTextNode(getPassword());

            } catch (SOAPException ex) {
                throw new RuntimeException(ex.getClass().getName() + ": " + ex.getLocalizedMessage(), ex);
            }
    	}
	}


	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}


	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}


	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}


	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
}
