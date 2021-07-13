package org.komparator.security.handler;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

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
import java.nio.charset.Charset;

import java.security.cert.CertificateException;
import pt.ulisboa.tecnico.sdis.ws.cli.CAClientException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import pt.ulisboa.tecnico.sdis.ws.cli.CAClient;
import java.security.*;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import java.io.ByteArrayOutputStream;
import org.w3c.dom.Document;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerFactory;
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

public class NoSignatureAttack implements SOAPHandler<SOAPMessageContext> {

	public static final String REQUEST_PROPERTY = "wsname.request.property";
	public static final String RESPONSE_PROPERTY = "signature.response.property";

	public static final String REQUEST_HEADER = "SignatureRequestHeader";
	public static final String REQUEST_NS = "urn:signaturehadler";


	public static final String OPERATION_NAME_TO_CIPHER = "ping";
	public static final String NAME_OF_SECRET_ARGUMENT ="arg0";

	public static final String  PATH_KEYSTORE = "T59_Mediator.jks";
	public static final String  PATH_CACERT = "ca.cer";
	public static final String PATH_NS="urn:supplier";

	private static Key PRIVATEKEY = null;
	private static TreeMap<String,PublicKey> PUBLICKEY = new TreeMap<String,PublicKey>();

	public static final String RESPONSE_HEADER = "SignatureResponseHeader";
	public static final String RESPONSE_NS = REQUEST_NS;

	public static final String CLASS_NAME = NoSignatureAttack.class.getSimpleName();

	public boolean handleMessage(SOAPMessageContext smc) {
		Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (outbound) {
			// outbound message
				try{

				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPBody sb = se.getBody();

				QName opn = (QName) smc.get(MessageContext.WSDL_OPERATION);
				if (!opn.getLocalPart().equals(OPERATION_NAME_TO_CIPHER)) {
					return true;
					}
				NodeList children = sb.getFirstChild().getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
				Node argument = children.item(i);
				if (argument.getNodeName().equals(NAME_OF_SECRET_ARGUMENT)) {
					String secretArgument = argument.getTextContent();
					argument.setTextContent("XPTO");
					msg.saveChanges();
				}
			}
				
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
