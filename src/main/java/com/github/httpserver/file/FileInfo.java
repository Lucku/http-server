package com.github.httpserver.file;

import java.nio.file.Path;
import java.util.Objects;

/**
 * FileInfo is a data model for information on a local file that is requested by a client.
 */
public class FileInfo {

    private final boolean isValid;
    private final boolean isModified;
    private final String contentType;
    private final String lastModified;
    private final Path filePath;

    /**
     * Constructs a file info by taking all parameters.
     *
     * @param isValid      an indicator if the file is considered valid after checking validator conditions.
     * @param isModified   an indicator if the file is considered modified after checking validator conditions.
     * @param contentType  the content type of the file.
     * @param lastModified the last modified date of the file in RFC1123 format.
     * @param filePath     the file path.
     * @throws NullPointerException if any of the string input parameters or the file path is null.
     */
    public FileInfo(boolean isValid, boolean isModified, String contentType, String lastModified, Path filePath) {
        this.isValid = isValid;
        this.isModified = isModified;
        this.contentType = Objects.requireNonNull(contentType);
        this.lastModified = Objects.requireNonNull(lastModified);
        this.filePath = Objects.requireNonNull(filePath);
    }

    /**
     * Returns an indicator if the file is considered valid after checking validator conditions.
     *
     * @return a boolean indicating if the file is valid.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Returns an indicator if the file is considered modified after checking validator conditions.
     *
     * @return a boolean indicating if the file was modified.
     */
    public boolean isModified() {
        return isModified;
    }

    /**
     * Returns the content type of the file.
     *
     * @return the content type as string.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Returns the last modified date of the file in RFC1123 format.
     *
     * @return the last modified date as string.
     */
    public String getLastModified() {
        return lastModified;
    }

    /**
     * Returns the file path.
     *
     * @return the file path as {@link Path}.
     */
    public Path getFilePath() {
        return filePath;
    }
}
