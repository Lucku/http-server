package com.github.httpserver.server;

import com.github.httpserver.Application;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * ServerConstants encapsulates server-internal constants that can not be configured
 * from the outside.
 */
public final class ServerConstants {

    /**
     * The hashing algorithm that is used to calculate the ETag header.
     */
    public static final String HASHING_ALGORITHM = "MD5";
    /**
     * The supported HTTP version of the server.
     */
    public static final String SUPPORTED_HTTP_VERSION = "HTTP/1.1";

    private ServerConstants() {
    }

    /**
     * Returns the path of the current execution context as needed when reading files. The
     * main purpose of this method is to return the correct file path in case the server is
     * being executed from inside a JAR file.
     *
     * @return an environment-specific path to read files from.
     */
    public static Path getCurrentFilePath() {
        String path = Application.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (path.endsWith(".jar")) {
            return Paths.get(path).getParent();
        }
        return Paths.get(".");
    }

    /**
     * Calculates the hash of a file that is used as the value for the ETag header.
     *
     * @param body the byte contents to be hashed.
     * @return the hex representation of the hash value as string. This result can be directly
     * used as the ETag value of the corresponding HTTP response.
     * @throws NoSuchAlgorithmException if the configured algorithm {@link #HASHING_ALGORITHM} is
     *                                  not defined.
     */
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
