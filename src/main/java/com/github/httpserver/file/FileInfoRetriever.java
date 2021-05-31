package com.github.httpserver.file;

import java.io.IOException;
import java.nio.file.Path;

/**
 * FileInfoRetriever constructs information of a given file.
 */
public interface FileInfoRetriever {

    /**
     * Creates a file information model based of the file at the given path on the local
     * file system.
     *
     * @param filePath the path to the local file.
     * @return the file info to the given file.
     * @throws IOException if the file cannot be read, e.g. when it doesn't exist.
     */
    FileInfo retrieveFileInfo(Path filePath) throws IOException;
}
