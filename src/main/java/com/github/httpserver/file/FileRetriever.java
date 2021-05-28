package com.github.httpserver.file;

import java.io.File;

public interface FileRetriever {

    File retrieveFileContents(String path);
}
