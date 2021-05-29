package com.github.httpserver.handler;

import com.github.httpserver.configuration.Configuration;
import com.github.httpserver.exception.HttpException;
import com.github.httpserver.exception.InternalServerErrorException;
import com.github.httpserver.exception.NotFoundException;
import com.github.httpserver.exception.PreconditionFailedException;
import com.github.httpserver.file.FileInfo;
import com.github.httpserver.file.FileInfoRetriever;
import com.github.httpserver.file.HttpFileInfoRetriever;
import com.github.httpserver.helper.HttpResponseBuilder;
import com.github.httpserver.protocol.HttpHeader;
import com.github.httpserver.protocol.HttpRequest;
import com.github.httpserver.protocol.HttpResponse;
import com.github.httpserver.protocol.HttpStatus;
import org.tinylog.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpGetRequestHandler implements HttpRequestHandler {

    private final HttpRequest request;
    private final FileInfoRetriever fileRetriever;
    private final Configuration config;

    public HttpGetRequestHandler(HttpRequest request, Configuration config) {
        this.config = config;
        this.request = request;
        this.fileRetriever = new HttpFileInfoRetriever(request);
    }

    @Override
    public HttpResponse handleRequest() throws HttpException {

        String requestPath = request.getPath();

        if (requestPath.equals("/")) {
            requestPath = config.getRootResource();
        }

        Path filePath = Paths.get(config.getSourcePath(), requestPath);

        if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
            throw new NotFoundException(String.format("Resource %s can not be found", filePath));
        }

        try {
            FileInfo fileInfo = fileRetriever.retrieveFileInfo(filePath);

            if (fileInfo.isValid()) {

                if (!fileInfo.isModified()) {
                    return new HttpResponseBuilder()
                            .setStatus(HttpStatus.HTTP_NOT_MODIFIED)
                            .build();
                }

                String fileContents = Files.readString(fileInfo.getFilePath());

                return new HttpResponseBuilder()
                        .appendBody(fileContents)
                        .appendHeader(HttpHeader.HEADER_CONTENT_TYPE, fileInfo.getContentType())
                        .appendETagHeader()
                        .appendContentLengthHeader()
                        .appendHeader(HttpHeader.HEADER_LAST_MODIFIED, fileInfo.getLastModified())
                        .build();
            }

        } catch (IOException e) {
            Logger.error("Unable to read requested file {}", filePath, e);
            throw new InternalServerErrorException(e);
        }

        throw new PreconditionFailedException();
    }
}
