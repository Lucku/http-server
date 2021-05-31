package com.github.httpserver.file;

import com.github.httpserver.server.ServerConstants;
import org.tinylog.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.StringTokenizer;

/**
 * IfNoneMatchValidator is a concrete file validator that evaluates the 'If-None-Match'
 * condition of an HTTP request, meaning that the file's calculated ETag must not match
 * any one of the given ones from the request header value.
 */
public class IfNoneMatchValidator implements FileValidator {

    private final String criterion;

    /**
     * Constructs an If-None-Match validator with the given criterion.
     *
     * @param criterion the raw value of the 'If-Match' HTTP request header entry.
     */
    public IfNoneMatchValidator(String criterion) {
        this.criterion = criterion;
    }

    /**
     * Evaluates the 'If-None-Match' condition on the file at the given path.
     *
     * @param filePath the path to the file on the local file system.
     * @return a boolean indicating if the condition is fulfilled.
     * @throws IOException if the given file cannot be read.
     */
    @Override
    public boolean isValidFile(Path filePath) throws IOException {

        // ETag "*" always fails
        if (criterion.equals("*")) {
            return false;
        }

        String fileETag;
        try {
            byte[] fileContents = Files.readAllBytes(filePath);
            fileETag = ServerConstants.calculateETag(fileContents);
        } catch (IOException | NoSuchAlgorithmException e) {
            Logger.warn(e, "Failed to calculate ETag of file {} while validating If-None-Match header",
                    filePath);
            throw new IOException(e);
        }

        StringTokenizer tokenizer = new StringTokenizer(criterion, ",");

        while (tokenizer.hasMoreTokens()) {
            String queryETag = tokenizer
                    .nextToken()
                    .trim()
                    // weak comparison is always used with If-None-Match
                    .replace("W/", "")
                    .replace("\"", "");

            if (queryETag.equals(fileETag)) {
                return false;
            }
        }

        return true;
    }
}
