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

public class NonceReceiverHandler implements SOAPHandler<SOAPMessageContext> {


	public static final String REQUEST_PROPERTY = "noncereceived.request.property";
	public static final String RESPONSE_PROPERTY = "noncesender.request.property";

	public static final String REQUEST_HEADER = "NonceRequestHeader";
	public static final String REQUEST_NS = "urn:noncehadler";

	public static final String STORE_PASSWORD="jBo1oxEt";
	public static final String KEY_PASSWORD="jBo1oxEt";
	private static Key PRIVATEKEY = null;
	private static Key PUBLICEKEY = null;
	public static final String PATH_NS="urn:supplier";

	public static final String  PATH_CACERT = "ca.cer";

	public static final String RESPONSE_HEADER = "NonceResponseHeader";
	public static final String RESPONSE_NS = REQUEST_NS;

	public static final String CLASS_NAME = NonceReceiverHandler.class.getSimpleName();

	public boolean handleMessage(SOAPMessageContext smc) {
		Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (outbound) {
			// outbound message
				try{
				CAClient caclient = new CAClient("http://sec.sd.rnl.tecnico.ulisboa.pt:8081/ca");
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPBody sb = se.getBody();

				byte[] ncsended = (byte[]) smc.get(REQUEST_PROPERTY);


				// add header
				SOAPHeader sh = se.getHeader();
				if (sh == null)
					sh = se.addHeader();

				// add header element (name, namespace prefix, namespace)
				Name name = se.createName(REQUEST_HEADER, "noncer", REQUEST_NS);
				Certificate caCert = CryptoUtil.getX509CertificateFromResource(PATH_CACERT);
				SOAPHeaderElement element = sh.addHeaderElement(name);
				if(PUBLICEKEY==null){
				Certificate cert=CryptoUtil.getCertificateFromString(caclient.getCertificate("T59_Mediator"));
				if( !CryptoUtil.verifySignedCertificate(cert,caCert)){
					QName faultCode = new QName(REQUEST_NS, "Failed to get Public key from CA");
					SOAPFault soapFault = SOAPFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createFault("Invalid Certificate", faultCode);
    				throw new SOAPFaultException(soapFault);
				}
				PUBLICEKEY = CryptoUtil.getPublicKeyFromCertificate(cert);
				}
				String newValue = printBase64Binary(CryptoUtil.asymCipher(ncsended,PUBLICEKEY));
				element.addTextNode(newValue);

				name = se.createName(REQUEST_HEADER, "nonces", REQUEST_NS);
				element = sh.addHeaderElement(name);
				byte[] noncesended= CryptoUtil.generateNonce();
				while(CryptoUtil.isUsed(noncesended))
					noncesended = CryptoUtil.generateNonce();
				 newValue = printBase64Binary(CryptoUtil.asymCipher(noncesended,PUBLICEKEY));
				element.addTextNode(newValue);	
				CryptoUtil.usedNonce(noncesended);	
				msg.saveChanges();
			
				
			} catch (SOAPException e) {
				System.out.printf("Failed to get SOAP header because of %s%n", e);
			}catch(CAClientException x){
				throw new RuntimeException("Failed to Connect CA");
			}catch(CertificateException x){
				throw new RuntimeException("Invalid Certificate from CA Certificate");
			}catch(NoSuchAlgorithmException x){
				throw new RuntimeException("Invalid Algorithm");
			}catch(IllegalBlockSizeException x){
				throw new RuntimeException("cipher isnn't the size of multiple blocks");
			}catch(IOException x){
				throw new RuntimeException("CA Certificate not found");
			}catch(NoSuchPaddingException x){
				throw new RuntimeException("Padding doesn't exist");
			}catch(BadPaddingException x){
				throw new RuntimeException("Wrong Padding");
			}
		}  else {
			
			try{
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPHeader sh = se.getHeader();
				SOAPBody sb = se.getBody();

				if (sh == null) {
					System.out.println("Header not found.");
					QName faultCode = new QName(REQUEST_NS, "Header Not Found");
					SOAPFault soapFault = SOAPFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createFault("No Header", faultCode);
    				throw new SOAPFaultException(soapFault);
				}

				// get first header element
				Name name = se.createName(REQUEST_HEADER, "nonce", REQUEST_NS);
				Iterator it = sh.getChildElements(name);
				// check header element
				if (!it.hasNext()) {
					System.out.printf("Header element %s not found.%n", REQUEST_HEADER);
					QName faultCode = new QName(REQUEST_NS, "No Nonce Receiver");
					SOAPFault soapFault = SOAPFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createFault("NONCE MISSING", faultCode);
    				throw new SOAPFaultException(soapFault);
				}
				SOAPHeaderElement element = (SOAPHeaderElement) it.next();
				name = se.createName("Supplier", "sp", PATH_NS);
				it = sh.getChildElements(name);
				// check header element
				if (!it.hasNext()) {
					System.out.printf("Header element %s not found.%n", REQUEST_HEADER);
					QName faultCode = new QName(REQUEST_NS, "No Supplier Id");
					SOAPFault soapFault = SOAPFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createFault("No ID on SOAP", faultCode);
    				throw new SOAPFaultException(soapFault);
				}
				SOAPHeaderElement elementpath = (SOAPHeaderElement) it.next();


				String path=elementpath.getValue();
				if(PRIVATEKEY==null){
				KeyStore store = CryptoUtil.readKeystoreFromResource(path +".jks", STORE_PASSWORD.toCharArray());
				PRIVATEKEY = CryptoUtil.getPrivateKeyFromKeyStore(path.toLowerCase(), KEY_PASSWORD.toCharArray(), store);
				}

				String headerValue = element.getValue();
				byte[] nonceReceived=CryptoUtil.asymDecipher(parseBase64Binary(headerValue),PRIVATEKEY);
				if(CryptoUtil.isSended(nonceReceived)){
					QName faultCode = new QName(REQUEST_NS, "Nonce Already Used");
					SOAPFault soapFault = SOAPFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createFault("Nonce Invalid", faultCode);
    				throw new SOAPFaultException(soapFault);
				}
				CryptoUtil.sendedNonce(nonceReceived);
    			smc.put(REQUEST_PROPERTY, nonceReceived);
				smc.setScope(REQUEST_PROPERTY, Scope.APPLICATION);
				msg.saveChanges();
				
			} catch (SOAPException e) {
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
