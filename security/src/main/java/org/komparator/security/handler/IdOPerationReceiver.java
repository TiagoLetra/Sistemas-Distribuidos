package org.komparator.security.handler;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

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

import pt.ulisboa.tecnico.sdis.ws.cli.CAClientException;
import javax.crypto.NoSuchPaddingException;
import java.security.cert.CertificateException;
import java.security.UnrecoverableKeyException;
import javax.crypto.BadPaddingException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.*;
import java.security.cert.Certificate;
import pt.ulisboa.tecnico.sdis.ws.cli.CAClient;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import java.io.ByteArrayOutputStream;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPFactory;

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

public class IdOPerationReceiver implements SOAPHandler<SOAPMessageContext> {


	public static final String REQUEST_PROPERTY = "idoperation.request.property";
	public static final String RESPONSE_PROPERTY = "idoperation.response.property";

	public static final String OPERATION_NAME_TO_CIPHER = "buyCart";
	public static final String OPERATION_NAME_TO_CIPHER2 = "addToCart";
	public static final String REQUEST_HEADER = "IdOperationHeader";
	public static final String REQUEST_NS = "urn:idoperation";


	public static final String RESPONSE_HEADER = "IdOperationHeader";
	public static final String RESPONSE_NS = REQUEST_NS;

	public static final String CLASS_NAME = IdOPerationReceiver.class.getSimpleName();

	public boolean handleMessage(SOAPMessageContext smc) {
		Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (outbound) {

				}  else {
			
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
				if (sh == null) {
					System.out.println("Header not found.");
					QName faultCode = new QName(REQUEST_NS, "Header Not Found");
					SOAPFault soapFault = SOAPFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createFault("No Header", faultCode);
    				throw new SOAPFaultException(soapFault);
				}

				// get first header element
				Name name = se.createName(REQUEST_HEADER, "idoperation", REQUEST_NS);
				Iterator it = sh.getChildElements(name);
				// check header element
				if (!it.hasNext()) {
					System.out.printf("Header element %s not found.%n", REQUEST_HEADER);
					QName faultCode = new QName(REQUEST_NS, "No id operation");
					SOAPFault soapFault = SOAPFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createFault("No ID Operation", faultCode);
    				throw new SOAPFaultException(soapFault);
				}
				SOAPHeaderElement elementpath = (SOAPHeaderElement) it.next();
				String idoperation=elementpath.getValue();

    			smc.put(REQUEST_PROPERTY, idoperation);
				smc.setScope(REQUEST_PROPERTY, Scope.APPLICATION);
				msg.saveChanges();
				
			} catch (SOAPException e) {
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
