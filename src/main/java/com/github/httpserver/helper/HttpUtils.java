package com.github.httpserver.helper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HttpUtils {

    public static final String HASHING_ALGORITHM = "MD5";

    private HttpUtils() {
    }

    public static String calculateETag(byte[] body) throws NoSuchAlgorithmException {

        MessageDigest messageDigest = MessageDigest.getInstance(HASHING_ALGORITHM);
        messageDigest.update(body);
        byte[] hash = messageDigest.digest();

        StringBuilder hexValue = new StringBuilder();

        for (byte b : hash) {
            hexValue.append(String.format("%02x", b));
        }

        return hexValue.toString();
    }
}
