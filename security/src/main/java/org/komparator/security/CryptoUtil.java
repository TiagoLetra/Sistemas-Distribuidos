package org.komparator.security;

import java.io.*;
import java.security.*;
import javax.crypto.*;
import java.util.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import java.util.concurrent.CopyOnWriteArrayList;
import java.nio.charset.StandardCharsets;


public class CryptoUtil {

		public static boolean outputFlag = true;
		private static  List<String> NONCE_SET_SENDED = new CopyOnWriteArrayList<String>();
		private static  List<String> NONCE_SET_USED = new CopyOnWriteArrayList<String>();

	public synchronized static boolean isUsed(byte[] nonce){
		String nonceString=new String(nonce,StandardCharsets.UTF_8);

		if(NONCE_SET_USED.contains(nonceString))
			return true;

		return false;
	}

		public synchronized static boolean isSended(byte[] nonce){
		String nonceString=new String(nonce,StandardCharsets.UTF_8);

		if(NONCE_SET_SENDED.contains(nonceString))
			return true;

		return false;
	}


	public synchronized static void sendedNonce(byte[] nonce){
		String nonceString=new String(nonce,StandardCharsets.UTF_8);
		if(NONCE_SET_SENDED.size()>200)	
			NONCE_SET_SENDED.remove(NONCE_SET_SENDED.size()-1);
		NONCE_SET_SENDED.add(nonceString);
	}

	public synchronized static void usedNonce(byte[] nonce){
		String nonceString=new String(nonce,StandardCharsets.UTF_8);
		if(NONCE_SET_USED.size()>200)	
			NONCE_SET_USED.remove(NONCE_SET_USED.size()-1);
		NONCE_SET_USED.add(nonceString);
	}




	public synchronized  static byte[] generateNonce() throws NoSuchAlgorithmException {
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		final byte array[] = new byte[32];
		random.nextBytes(array);
		return array;

	}	

		public static Certificate getX509CertificateFromBytes(byte[] bytes) throws CertificateException {
		InputStream in = new ByteArrayInputStream(bytes);
		return getX509CertificateFromStream(in);
	}

	public static Certificate getX509CertificateFromResource(String certificateResourcePath)
			throws IOException, CertificateException {
		InputStream is = getResourceAsStream(certificateResourcePath);
		return getX509CertificateFromStream(is);
	}


	public static Certificate getCertificateFromString(String certificateString ) throws CertificateException{
		byte[] bytes = certificateString.getBytes(StandardCharsets.UTF_8);
		InputStream in = new ByteArrayInputStream(bytes);
		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		Certificate cert = certFactory.generateCertificate(in);
		return cert;
	}

	public static Certificate getX509CertificateFromStream(InputStream in) throws CertificateException {
		try {
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			Certificate cert = certFactory.generateCertificate(in);
			return cert;
		} finally {
			closeStream(in);
		}
	}

	public static PublicKey getPublicKeyFromCertificate(Certificate certificate) {
		return certificate.getPublicKey();
	}


    // TODO add security helper methods
    public static byte[] asymCipher(byte[] plainBytes,Key publicKey) throws NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException{
    	byte[] dectyptedText;
    	try{
    		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
    		byte[] cipherBytes = cipher.doFinal(plainBytes);
    		return cipherBytes;
   		}catch(InvalidKeyException x){
    		System.out.println("Invalid Key");
    	}
    	return null;
	}


	public static byte[] asymDecipher(byte[] encryptedBytes,Key privateKey)throws NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException,NoSuchPaddingException{
		byte[] dectyptedText = null;
		try{
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			dectyptedText = cipher.doFinal(encryptedBytes);
			return dectyptedText;
   		}catch(InvalidKeyException x){
    		System.out.println("Invalid Key");
    	}
    	return null;
	}


    public static PrivateKey getPrivateKeyFromKeyStoreFile(File keyStoreFile, char[] keyStorePassword, String keyAlias,
		char[] keyPassword) throws FileNotFoundException, KeyStoreException, UnrecoverableKeyException {
		KeyStore keystore = readKeystoreFromFile(keyStoreFile, keyStorePassword);
		return getPrivateKeyFromKeyStore(keyAlias, keyPassword, keystore);
	}

	public static PrivateKey getPrivateKeyFromKeyStore(String keyAlias, char[] keyPassword, KeyStore keystore)
			throws KeyStoreException, UnrecoverableKeyException {
		PrivateKey key;
		try {
			key = (PrivateKey) keystore.getKey(keyAlias, keyPassword);
		} catch (NoSuchAlgorithmException e) {
			throw new KeyStoreException(e);
		}
		return key;
	}

		public static KeyStore readKeystoreFromResource(String keyStoreResourcePath, char[] keyStorePassword)
			throws KeyStoreException {
		InputStream is = getResourceAsStream(keyStoreResourcePath);
		return readKeystoreFromStream(is, keyStorePassword);
	}


	private static KeyStore readKeystoreFromFile(File keyStoreFile, char[] keyStorePassword)
			throws FileNotFoundException, KeyStoreException {
		FileInputStream fis = new FileInputStream(keyStoreFile);
		return readKeystoreFromStream(fis, keyStorePassword);
	}

	private static KeyStore readKeystoreFromStream(InputStream keyStoreInputStream, char[] keyStorePassword)
			throws KeyStoreException {
		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		try {
			keystore.load(keyStoreInputStream, keyStorePassword);
		} catch (NoSuchAlgorithmException | CertificateException | IOException e) {
			throw new KeyStoreException("Could not load key store", e);
		} finally {
			closeStream(keyStoreInputStream);
		}
		return keystore;
	}


	public static byte[] makeDigitalSignature( final PrivateKey privateKey,
			final byte[] bytesToSign) {
		try {
			Signature sig = Signature.getInstance("SHA256withRSA");
			sig.initSign(privateKey);
			sig.update(bytesToSign);
			byte[] signatureResult = sig.sign();
			return signatureResult;
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
			if (outputFlag) {
				System.err.println("Caught exception while making signature: " + e);
				System.err.println("Returning null.");
			}
			return null;
		}
	}


	public static boolean verifyDigitalSignature( PublicKey publicKey,
			byte[] bytesToVerify, byte[] signature) {
		try {
			Signature sig = Signature.getInstance("SHA256withRSA");
			sig.initVerify(publicKey);
			sig.update(bytesToVerify);
			return sig.verify(signature);
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
			if (outputFlag) {
				System.err.println("Caught exception while verifying signature " + e);
				System.err.println("Returning false.");
			}
			return false;
		}
	}



	public static boolean verifySignedCertificate(Certificate certificate, PublicKey caPublicKey) {
		try {
			certificate.verify(caPublicKey);
		} catch (InvalidKeyException | CertificateException | NoSuchAlgorithmException | NoSuchProviderException
				| SignatureException e) {
			if (outputFlag) {
				System.err.println("Caught exception while verifying certificate with CA public key : " + e);
				System.err.println("Returning false.");
			}
			return false;
		}
		return true;
	}

	public static boolean verifySignedCertificate(Certificate certificate, Certificate caCertificate) {
		return verifySignedCertificate(certificate, caCertificate.getPublicKey());
	}

	public static boolean verifyDigitalSignature( Certificate publicKeyCertificate,
			byte[] bytesToVerify, byte[] signature) {
		return verifyDigitalSignature( publicKeyCertificate.getPublicKey(), bytesToVerify, signature);
	}


	private static InputStream getResourceAsStream(String resourcePath) {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
		return is;
	}

	private static void closeStream(InputStream in) {
		try {
			if (in != null)
				in.close();
		} catch (IOException e) {
			// ignore
		}
	}
}
