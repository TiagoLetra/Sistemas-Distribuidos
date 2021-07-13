package org.komparator.security.handler;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import org.komparator.security.CryptoUtil;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import  javax.xml.ws.soap.SOAPFaultException;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import java.nio.charset.StandardCharsets;






/**
 * This is the handler server class of the Relay example.
 *
 * #4 The server handler receives data from the client handler (via inbound SOAP
 * message header). #5 The server handler passes data to the server (via message
 * context).
 *
 * *** GO TO server class to see what happens next! ***
 *
 * #8 The server class receives data from the server (via message context). #9
 * The server handler passes data to the client handler (via outbound SOAP
 * message header).
 *
 * *** GO BACK TO client handler to see what happens next! ***
 */

public class IdOPerationSender implements SOAPHandler<SOAPMessageContext> {

	public static final String REQUEST_PROPERTY = "idoperation.request.property";
	public static final String REQUEST_PROPERTY2 = "operation.request.property";
	public static final String RESPONSE_PROPERTY = "idoperation.response.property";

	public static final String OPERATION_NAME_TO_CIPHER = "buyCart";
	public static final String OPERATION_NAME_TO_CIPHER2 = "addToCart";
	public static final String REQUEST_HEADER = "IdOperationHeader";
	public static final String REQUEST_NS = "urn:idoperation";


	public static final String RESPONSE_HEADER = "IdOperationHeader";
	public static final String RESPONSE_NS = REQUEST_NS;

	public static final String CLASS_NAME = IdOPerationSender.class.getSimpleName();

	public boolean handleMessage(SOAPMessageContext smc) {
		Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (outbound) {
			// outbound message
			try{

				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPBody sb = se.getBody();
				SOAPHeader sh = se.getHeader();
				QName opn = (QName) smc.get(MessageContext.WSDL_OPERATION);
				if (!opn.getLocalPart().equals(OPERATION_NAME_TO_CIPHER)&&!opn.getLocalPart().equals(OPERATION_NAME_TO_CIPHER2)) {
					return true;
					}
				if (sh == null)
					sh = se.addHeader();
				String id = (String) smc.get(REQUEST_PROPERTY);
				int operation = (int)  smc.get(REQUEST_PROPERTY2);
				Name name = se.createName(REQUEST_HEADER, "idoperation", REQUEST_NS);
				SOAPHeaderElement element = sh.addHeaderElement(name);
				element.addTextNode(id+operation);
					
				
				}catch (SOAPException e) {
				System.out.printf("Failed to get SOAP header because of %s%n", e);
			}
		}
				return true;
	
	}
	public boolean handleFault(SOAPMessageContext smc) {
		return true;
	}

	public Set<QName> getHeaders() {
		return null;
	}

	public void close(MessageContext messageContext) {
	}

}
