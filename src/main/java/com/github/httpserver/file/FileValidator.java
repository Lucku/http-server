package com.github.httpserver.file;

import java.io.IOException;
import java.nio.file.Path;

/**
 * FileValidator validates a file input on a specific condition.
 */
public interface FileValidator {

    /**
     * Evaluates the file at the given file path based on specific condition.
     *
     * @param filePath the path to the file on the local file system.
     * @return a boolean indicating if the file matches the condition.
     * @throws IOException if the file cannot be read, e.g. when it doesn't exist.
     */
    boolean isValidFile(Path filePath) throws IOException;
}
