package com.github.httpserver.file;

import java.io.IOException;
import java.nio.file.Path;
import java.time.DateTimeException;

public interface FileValidator {

    boolean isValidFile(Path filePath) throws IOException, DateTimeException;
}
