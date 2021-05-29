package com.github.httpserver.file;

import java.io.IOException;
import java.nio.file.Path;

public interface FileInfoRetriever {

    FileInfo retrieveFileInfo(Path filePath) throws IOException;
}
