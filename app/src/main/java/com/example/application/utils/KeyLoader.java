package com.example.application.utils;


import java.io.*;
import java.security.*;
import java.security.cert.*;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.spec.*;
import com.android.apksig.ApkSigner;

public class KeyLoader {
    public static String SIGN_KEY_NAME = "Tusar";

	

    public static ApkSigner.SignerConfig loadTestKey() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, GeneralSecurityException {
        List<X509Certificate> certificates = new LinkedList<>();

        try (InputStream pubKeyStream = KeyLoader.class.getResourceAsStream("/assets/keys/" + SIGN_KEY_NAME + ".x509.pem");
		InputStream privKeyStream = KeyLoader.class.getResourceAsStream("/assets/keys/" + SIGN_KEY_NAME + ".pk8")) {

            if (pubKeyStream == null || privKeyStream == null) {
                throw new FileNotFoundException("Key files not found in assets/keys/");
            }

            certificates.add(readPublicKey(pubKeyStream));
            PrivateKey privateKey = readPrivateKey(privKeyStream);

            return new ApkSigner.SignerConfig.Builder("MrTusarRX", privateKey, certificates).build();
        }
    }

    private static X509Certificate readPublicKey(InputStream input) throws IOException, GeneralSecurityException {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return (X509Certificate) cf.generateCertificate(input);
        } finally {
            input.close();
        }
    }

    private static KeySpec decryptPrivateKey(byte[] encryptedPrivateKey, String keyPassword) throws GeneralSecurityException {
        try {
            EncryptedPrivateKeyInfo epkInfo = new EncryptedPrivateKeyInfo(encryptedPrivateKey);
            char[] password = keyPassword.toCharArray();
            SecretKeyFactory skFactory = SecretKeyFactory.getInstance(epkInfo.getAlgName());
            Key key = skFactory.generateSecret(new PBEKeySpec(password));
            Cipher cipher = Cipher.getInstance(epkInfo.getAlgName());
            cipher.init(Cipher.DECRYPT_MODE, key, epkInfo.getAlgParameters());
            return epkInfo.getKeySpec(cipher);
        } catch (IOException ex) {
            System.err.println("Error decrypting private key: " + ex.getMessage());
            return null;
        }
    }

    private static PrivateKey readPrivateKey(InputStream input) throws IOException, GeneralSecurityException {
        byte[] bytes = readBytes(input);
        KeySpec spec = decryptPrivateKey(bytes, "");
        if (spec == null) {
            spec = new PKCS8EncodedKeySpec(bytes);
        }

        try {
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (InvalidKeySpecException ex) {
            return KeyFactory.getInstance("DSA").generatePrivate(spec);
        }
    }

    private static byte[] readBytes(InputStream in) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buf = new byte[1024];
            int num;
            while ((num = in.read(buf)) != -1) {
                out.write(buf, 0, num);
            }
            return out.toByteArray();
        }
    }
}
