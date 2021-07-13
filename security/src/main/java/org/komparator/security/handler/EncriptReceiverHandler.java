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
import java.security.Key;
import java.security.KeyStore;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

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

public class EncriptReceiverHandler implements SOAPHandler<SOAPMessageContext> {

	public static final String REQUEST_PROPERTY = "encript.request.property";
	public static final String RESPONSE_PROPERTY = "encript.response.property";

	public static final String OPERATION_NAME_TO_CIPHER = "buyCart";

	public static final String REQUEST_HEADER = "EncriptRequestHeader";
	public static final String REQUEST_NS = "urn:example";
	public static final String CERT_NAME ="T59_Mediator";

	public static final String STORE_PASSWORD="jBo1oxEt";
	public static final String KEY_PASSWORD="jBo1oxEt";
	public static final String NAME_OF_SECRET_ARGUMENT ="creditCardNr";
	private static Key PUBLICEKEY = null;

	public static final String  PATH_KEYSTORE = "T59_Mediator.jks";
	public static final String  PATH_CACERT = "ca.cer";

	public static final String RESPONSE_HEADER = "EncriptResponseHeader";
	public static final String RESPONSE_NS = REQUEST_NS;

	public static final String CLASS_NAME = EncriptReceiverHandler.class.getSimpleName();

	public boolean handleMessage(SOAPMessageContext smc) {
		Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (outbound) {
			
		}  else {
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
					if(secretArgument==null){return true;}
						secretArgument = secretArgument.trim();
					if (secretArgument.length() != 0){
					KeyStore store = CryptoUtil.readKeystoreFromResource(PATH_KEYSTORE, STORE_PASSWORD.toCharArray());
					Key key = CryptoUtil.getPrivateKeyFromKeyStore("T59_Mediator", KEY_PASSWORD.toCharArray(), store);
					System.out.println("Decifrar Mensagem");
					byte[] decipheredArgument = CryptoUtil.asymDecipher(parseBase64Binary(secretArgument),key);
					argument.setTextContent(new String(decipheredArgument,StandardCharsets.UTF_8));
					msg.saveChanges();
					}
					}
				}
			}catch (SOAPException e) {
				System.out.printf("Failed to get SOAP header because of %s%n", e);
			}catch(NoSuchAlgorithmException x){
				throw new RuntimeException("Wrong Algorithm");
			}catch(IllegalBlockSizeException x){
				// can't happen on decipher
			}catch(KeyStoreException x){
				throw new RuntimeException("Exception while getting KeyStore");
    		}catch(UnrecoverableKeyException x){
				throw new RuntimeException("Exception while getting PrivateKey");
			}catch(NoSuchPaddingException x){
				throw new RuntimeException("Padding doesn't exist");
			}catch(BadPaddingException x){
				throw new RuntimeException("Wrong Padding");
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
