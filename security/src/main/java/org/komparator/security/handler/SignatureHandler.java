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

public class SignatureHandler implements SOAPHandler<SOAPMessageContext> {

	public static final String REQUEST_PROPERTY = "wsname.request.property";
	public static final String RESPONSE_PROPERTY = "signature.response.property";

	public static final String REQUEST_HEADER = "SignatureRequestHeader";
	public static final String REQUEST_NS = "urn:signaturehadler";

	public static final String STORE_PASSWORD="jBo1oxEt";
	public static final String KEY_PASSWORD="jBo1oxEt";

	public static final String  PATH_KEYSTORE = "T59_Mediator.jks";
	public static final String  PATH_CACERT = "ca.cer";
	public static final String PATH_NS="urn:supplier";

	private static Key PRIVATEKEY = null;
	private static TreeMap<String,PublicKey> PUBLICKEY = new TreeMap<String,PublicKey>();

	public static final String RESPONSE_HEADER = "SignatureResponseHeader";
	public static final String RESPONSE_NS = REQUEST_NS;

	public static final String CLASS_NAME = SignatureHandler.class.getSimpleName();

	public boolean handleMessage(SOAPMessageContext smc) {
		Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (outbound) {
			// outbound message
				try{

				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPBody sb = se.getBody();
				msg.saveChanges();

				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				msg.writeTo(bos);

				// add header
				SOAPHeader sh = se.getHeader();
				if (sh == null)
					sh = se.addHeader();

				// add header element (name, namespace prefix, namespace)
				Name name = se.createName(REQUEST_HEADER, "sing", REQUEST_NS);
				SOAPHeaderElement element = sh.addHeaderElement(name);
				if(PRIVATEKEY==null){
				KeyStore store = CryptoUtil.readKeystoreFromResource(PATH_KEYSTORE, STORE_PASSWORD.toCharArray());
				PRIVATEKEY= CryptoUtil.getPrivateKeyFromKeyStore("T59_Mediator".toLowerCase(), KEY_PASSWORD.toCharArray(), store);
				}
				String newValue = printBase64Binary(CryptoUtil.makeDigitalSignature((PrivateKey)PRIVATEKEY,bos.toByteArray()));
				element.addTextNode(newValue);
				element.setActor(REQUEST_NS);
				name = se.createName("Supplier", "sp", PATH_NS);
				String path=(String) smc.get(REQUEST_PROPERTY);
				if(path==null)
					path="T59_Supplier1";
				System.out.println(path);
				
			} catch (SOAPException e) {
				System.out.printf("Failed to get SOAP header because of %s%n", e);
			}catch(KeyStoreException x){
				throw new RuntimeException("Exception while getting KeyStore");
    		}catch(UnrecoverableKeyException x){
				throw new RuntimeException("Exception while getting PrivateKey");
			}catch(IOException x){
				throw new RuntimeException("CA Certificate not found");
			}

		}  else {
			
			try{
				CAClient caclient = new CAClient("http://sec.sd.rnl.tecnico.ulisboa.pt:8081/ca");
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
				// check header element
				Iterator it=sh.extractHeaderElements(RESPONSE_NS);
				if (!it.hasNext()) {
					System.out.printf("Header element %s not found.%n", REQUEST_HEADER);
					QName faultCode = new QName(REQUEST_NS, "No Signature");
					SOAPFault soapFault = SOAPFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createFault("SIGNATURE MISSING", faultCode);
    				throw new SOAPFaultException(soapFault);
				}
				SOAPElement element = (SOAPElement) it.next();
				String headerValue = element.getValue();
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				msg.writeTo(bos);
				Certificate caCert = CryptoUtil.getX509CertificateFromResource(PATH_CACERT);
				String path=(String) smc.get(REQUEST_PROPERTY);
				if(path==null)
					path="T59_Supplier1";
				if(!PUBLICKEY.containsKey(path)){
				Certificate cert=CryptoUtil.getCertificateFromString(caclient.getCertificate(path));
				if(!CryptoUtil.verifySignedCertificate(cert,caCert)){
					System.out.println("CA Failed.");
					QName faultCode = new QName(REQUEST_NS, "Failed to get Public key from CA");
					SOAPFault soapFault = SOAPFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createFault("Invalid Certificate", faultCode);
    				throw new SOAPFaultException(soapFault);
				}
				PUBLICKEY.put(path,CryptoUtil.getPublicKeyFromCertificate(cert));
				}
				if(!CryptoUtil.verifyDigitalSignature( PUBLICKEY.get(path), bos.toByteArray(), parseBase64Binary(headerValue))){
					System.out.println("The SOAP Message has been changed");
					QName faultCode = new QName(REQUEST_NS, "Signature doesnt bellong to SOAP");
					SOAPFault soapFault = SOAPFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createFault("The content on SOAP Message has been modified", faultCode);
    				throw new SOAPFaultException(soapFault);
				}
			
			} catch (SOAPException e) {
				System.out.printf("Failed to get SOAP header because of %s%n", e);
			}catch(CAClientException x){
				throw new RuntimeException("Failed to Connect CA");
			}catch(CertificateException x){
				throw new RuntimeException("Invalid Certificate from CA Certificate");
			}catch(IOException x){
				throw new RuntimeException("CA Certificate not found");
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
