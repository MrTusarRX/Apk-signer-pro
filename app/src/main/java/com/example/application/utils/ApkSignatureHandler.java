

package com.example.application.utils;

import android.sun.security.provider.JavaKeyStoreProvider;
import com.android.apksig.ApkSigner;
import com.android.apksig.apk.ApkFormatException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import com.android.apksig.Utils;
import java.util.ArrayList;
import java.io.FileNotFoundException;


import java.io.*;
import java.security.*;
import java.security.cert.*;
import java.util.*;

public class ApkSignatureHandler {
    public static final String TAG = "Signer";
    public static boolean setV1SigningEnabled = true;
    public static boolean setV2SigningEnabled = true;
    public static boolean setV3SigningEnabled = true;

    public ApkSignatureHandler() {
    }

    public void calculateSignature(String inputApkFile, String outputApkFile)
	throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException,
	UnrecoverableKeyException, ApkFormatException, InvalidKeyException, SignatureException, GeneralSecurityException {

        Security.addProvider(new JavaKeyStoreProvider());

        ApkSigner apkSigner = new ApkSigner.Builder(Collections.singletonList(KeyLoader.loadTestKey()))
			.setV1SigningEnabled(setV1SigningEnabled)
			.setV2SigningEnabled(setV2SigningEnabled)
			.setV3SigningEnabled(setV3SigningEnabled)
			.setCreatedBy("MrTusarRX")
			.setInputApk(new File(inputApkFile))
			.setOutputApk(new File(outputApkFile))
			.build();

        apkSigner.sign();
    }
}
