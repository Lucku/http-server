package com.github.httpserver.helper;

import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class HttpUtils {

    public static final String HASHING_ALGORITHM = "MD5";

    private HttpUtils() {
    }

    public static String calculateETag(String body) throws NoSuchAlgorithmException {

        MessageDigest messageDigest = MessageDigest.getInstance(HASHING_ALGORITHM);
        messageDigest.update(body.getBytes(StandardCharsets.UTF_8));
        byte[] hash = messageDigest.digest();

        StringBuilder hexValue = new StringBuilder();

        for (byte b : hash) {
            hexValue.append(String.format("%02x", b));
        }

        return hexValue.toString();
    }

    public static String fileTimeToUTCDateString(FileTime fileTime) {
        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneOffset.UTC)
                .withLocale(Locale.US);
        return formatter.format(fileTime.toInstant());
    }
}
