package com.github.httpserver.handler;

import com.github.httpserver.exception.HttpException;
import com.github.httpserver.file.FileInfoRetriever;
import com.github.httpserver.protocol.HttpRequest;
import com.github.httpserver.protocol.HttpResponse;

/**
 * HttpRequestHandler creates an HTTP response based on a client HTTP request.
 */
public interface HttpRequestHandler {

    /**
     * Creates an HTTP response by taking and processing an HTTP request.
     *
     * @param request           the request to be handled.
     * @param fileInfoRetriever a file info retriever to aid in getting information about the requested resource.
     * @return the response to the client.
     * @throws HttpException if the request cannot be successfully processed.
     */
    HttpResponse handleRequest(HttpRequest request, FileInfoRetriever fileInfoRetriever) throws HttpException;
}
