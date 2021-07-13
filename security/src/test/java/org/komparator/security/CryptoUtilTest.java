package org.komparator.security;

import java.io.*;
import java.security.*;
import javax.crypto.*;
import java.util.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import java.nio.charset.StandardCharsets;

import org.junit.*;
import static org.junit.Assert.*;

public class CryptoUtilTest {

    private static String CERTIFICATE = "example.cer";
    private static String JKS_FILE = "example.jks";
    private static String STORE_PASSWORD = "1nsecure";
    private static String KEY_PASSWORD = "ins3cur3";
    private static String MENSAGEM = "Hello World!";

    // one-time initialization and clean-up
    @BeforeClass
    public static void oneTimeSetUp() {
        // runs once before all tests in the suite
    }

    @AfterClass
    public static void oneTimeTearDown() {
        // runs once after all tests in the suite
    }

    // members

    // initialization and clean-up for each test
    @Before
    public void setUp() {
        // runs before each test
    }

    @After
    public void tearDown() {
        // runs after each test
    }

    // tests
    @Test
    public void PublictoPrivateEncription() throws KeyStoreException,NoSuchAlgorithmException,IOException,IllegalBlockSizeException,UnrecoverableKeyException,NoSuchPaddingException,BadPaddingException, CertificateException {
        // do something ...
        Certificate cert = CryptoUtil.getX509CertificateFromResource(CERTIFICATE);
        byte[] mensagem =CryptoUtil.asymCipher(MENSAGEM.getBytes(),CryptoUtil.getPublicKeyFromCertificate(cert));
        System.out.println("TEST ");
        String file_string = printBase64Binary(mensagem);
        System.out.println( "Mensagem Encriptada = "+ file_string);
        KeyStore key=CryptoUtil.readKeystoreFromResource(JKS_FILE,STORE_PASSWORD.toCharArray());
        mensagem=parseBase64Binary(file_string);
        byte[] mensagemdecifrada = CryptoUtil.asymDecipher(mensagem,CryptoUtil.getPrivateKeyFromKeyStore("example",KEY_PASSWORD.toCharArray(),key));
        file_string = new String(mensagemdecifrada, StandardCharsets.UTF_8);
        System.out.println( "Mensagem Desincriptada = "+ file_string);
        assertEquals(MENSAGEM,file_string);
    }

        @Test
    public void makeSignature() throws KeyStoreException,NoSuchAlgorithmException,IOException,IllegalBlockSizeException,UnrecoverableKeyException,NoSuchPaddingException,BadPaddingException, CertificateException {
        // do something ...
        KeyStore key=CryptoUtil.readKeystoreFromResource(JKS_FILE,STORE_PASSWORD.toCharArray());
        PrivateKey pk=CryptoUtil.getPrivateKeyFromKeyStore("example",KEY_PASSWORD.toCharArray(),key);
        byte[] sig=CryptoUtil.makeDigitalSignature(pk,MENSAGEM.getBytes());

    }

}
