package com.github.httpserver.file;

import com.github.httpserver.server.ServerConstants;
import org.tinylog.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.StringTokenizer;

/**
 * IfMatchValidator is a concrete file validator that evaluates the 'If-Match' condition
 * of an HTTP request, meaning that the file's calculated ETag has to match one of the
 * given ones from the request header value.
 */
public class IfMatchValidator implements FileValidator {

    private final String criterion;

    /**
     * Constructs an If-Match validator with the given criterion.
     *
     * @param criterion the raw value of the 'If-Match' HTTP request header entry.
     */
    public IfMatchValidator(String criterion) {
        this.criterion = criterion;
    }

    /**
     * Evaluates the 'If-Match' condition on the file at the given path.
     *
     * @param filePath the path to the file on the local file system.
     * @return a boolean indicating if the condition is fulfilled.
     * @throws IOException if the given file cannot be read.
     */
    @Override
    public boolean isValidFile(Path filePath) throws IOException {

        // ETag "*" always matches
        if (criterion.equals("*")) {
            return true;
        }

        String fileETag;
        try {
            byte[] fileContents = Files.readAllBytes(filePath);
            fileETag = ServerConstants.calculateETag(fileContents);
        } catch (IOException | NoSuchAlgorithmException e) {
            Logger.warn(e, "Failed to calculate ETag of file {} while validating If-Match header", filePath);
            throw new IOException(e);
        }

        StringTokenizer tokenizer = new StringTokenizer(criterion, ",");
        while (tokenizer.hasMoreTokens()) {

            String queryETag = tokenizer.nextToken().trim();

            // weak entity tags should never match
            if (queryETag.startsWith("W/")) {
                continue;
            }

            queryETag = queryETag.replace("\"", "");

            if (queryETag.equals(fileETag)) {
                return true;
            }
        }

        return false;
    }
}
