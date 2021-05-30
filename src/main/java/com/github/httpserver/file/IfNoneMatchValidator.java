package com.github.httpserver.file;

import com.github.httpserver.server.ServerConstants;
import org.tinylog.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.StringTokenizer;

public class IfNoneMatchValidator implements FileValidator {

    private final String criterion;

    public IfNoneMatchValidator(String criteria) {
        this.criterion = criteria;
    }

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
