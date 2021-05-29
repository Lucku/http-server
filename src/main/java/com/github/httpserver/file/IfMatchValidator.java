package com.github.httpserver.file;

import com.github.httpserver.helper.HttpUtils;
import org.tinylog.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.StringTokenizer;

public class IfMatchValidator implements FileValidator {

    private final String criteria;

    public IfMatchValidator(String criteria) {
        this.criteria = criteria;
    }

    @Override
    public boolean isValidFile(Path filePath) throws IOException {

        // ETag "*" always matches
        if (criteria.equals("*")) {
            return true;
        }

        String fileETag;
        try {
            byte[] fileContents = Files.readAllBytes(filePath);
            fileETag = HttpUtils.calculateETag(fileContents);
        } catch (IOException | NoSuchAlgorithmException e) {
            Logger.warn(e, "Failed to calculate ETag of file {} while validating If-Match header", filePath);
            throw new IOException(e);
        }

        StringTokenizer tokenizer = new StringTokenizer(criteria, ",");
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
