package com.github.httpserver.file;

import java.nio.file.Path;

public class FileInfo {

    private final boolean isValid;
    private final boolean isModified;
    private final String contentType;
    private final String lastModified;
    private final Path filePath;

    public FileInfo(boolean isValid, boolean isModified, String contentType, String lastModified, Path filePath) {
        this.isValid = isValid;
        this.isModified = isModified;
        this.contentType = contentType;
        this.lastModified = lastModified;
        this.filePath = filePath;
    }

    public boolean isValid() {
        return isValid;
    }

    public boolean isModified() {
        return isModified;
    }

    public String getContentType() {
        return contentType;
    }

    public String getLastModified() {
        return lastModified;
    }

    public Path getFilePath() {
        return filePath;
    }
}
