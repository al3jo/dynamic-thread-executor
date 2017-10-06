package com.securelink.ssh.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

public class KeyGenerator {

    private static final int KEY_SIZE = 2048;
    private static final Logger logger = LoggerFactory.getLogger(KeyGenerator.class);

    public static String generate() {
        String key = null;
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(KEY_SIZE, new SecureRandom());
            KeyPair pair = kpg.genKeyPair();

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            key = Base64.getEncoder().encodeToString(digest.digest((RSAPublicKey.class.cast(pair.getPublic())).getEncoded()));
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error generating fingerprint", e.getMessage());
        }
        return key;
    }

    public static int getAvailableCores() {
        return Runtime.getRuntime( ).availableProcessors( );
    }
}
