package com.github.httpserver.handler;

import com.github.httpserver.configuration.Configuration;
import com.github.httpserver.exception.BadRequestException;
import com.github.httpserver.exception.HttpException;
import com.github.httpserver.exception.InternalServerErrorException;
import com.github.httpserver.exception.NotFoundException;
import com.github.httpserver.exception.PreconditionFailedException;
import com.github.httpserver.file.FileInfo;
import com.github.httpserver.file.FileInfoRetriever;
import com.github.httpserver.helper.HttpResponseBuilder;
import com.github.httpserver.protocol.HttpHeader;
import com.github.httpserver.protocol.HttpRequest;
import com.github.httpserver.protocol.HttpResponse;
import com.github.httpserver.protocol.HttpStatus;
import com.github.httpserver.server.ServerConstants;
import org.tinylog.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.util.Objects;

/**
 * HttpGetRequestHandler is a concrete request handler for HTTP GET requests.
 */
public class HttpGetRequestHandler implements HttpRequestHandler {

    private final Configuration config;

    /**
     * Constructs a new HTTP GET request handler by taking the application configuration.
     *
     * @param config the application configuration.
     */
    public HttpGetRequestHandler(Configuration config) {
        this.config = Objects.requireNonNull(config);
    }

    /**
     * Creates a response to a HTTP GET request. The resource is retrieved from the file system
     * and the response is built by putting all relevant information about the resource into the response
     * header. If the request succeeds, the response body contains the file contents.
     * <p>
     * In case that the requested resource has not been modified since the last request by the client
     * (determined via 'If-None-Match' and 'If-Modified-Since' headers, a '304 Not Modified' response is
     * returned.
     *
     * @param request       the request to be handled.
     * @param fileRetriever a file retriever to obtain relevant metadata about the requested resource.
     * @return the GET response to the client.
     * @throws HttpException if an error occurs during the processing of the request.
     */
    @Override
    public HttpResponse handleRequest(HttpRequest request, FileInfoRetriever fileRetriever) throws HttpException {

        String requestPath = request.getPath();

        if (requestPath.equals("/")) {
            requestPath = config.getRootResource();
        }

        Path filePath = ServerConstants.getCurrentFilePath().resolve(Paths.get(config.getSourcePath(), requestPath));

        if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
            throw new NotFoundException(String.format("Resource %s can not be found", requestPath));
        }

        try {
            FileInfo fileInfo = fileRetriever.retrieveFileInfo(filePath);

            if (fileInfo.isValid()) {

                if (!fileInfo.isModified()) {
                    return new HttpResponseBuilder()
                            .setStatus(HttpStatus.HTTP_NOT_MODIFIED)
                            .build();
                }

                byte[] fileContents = Files.readAllBytes(fileInfo.getFilePath());

                return new HttpResponseBuilder()
                        .setBody(fileContents)
                        .appendHeader(HttpHeader.HEADER_CONTENT_TYPE, fileInfo.getContentType())
                        .appendETagHeader()
                        .appendContentLengthHeader()
                        .appendHeader(HttpHeader.HEADER_LAST_MODIFIED, fileInfo.getLastModified())
                        .build();
            }

        } catch (IOException e) {
            Logger.error(e, "Unable to read requested file {}", filePath);
            throw new InternalServerErrorException(e);
        } catch (DateTimeException e) {
            throw new BadRequestException(e.getMessage(), e);
        }

        throw new PreconditionFailedException();
    }
}
