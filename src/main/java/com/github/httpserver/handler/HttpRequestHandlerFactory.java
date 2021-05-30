package com.github.httpserver.handler;

import com.github.httpserver.configuration.Configuration;
import com.github.httpserver.exception.MethodNotAllowedException;
import com.github.httpserver.protocol.HttpRequest;

import java.util.Objects;

/**
 * HttpRequestHandlerFactory is a factory class to create an {@link HttpRequestHandler} based
 * on the HTTP method that is requested by the client.
 */
public class HttpRequestHandlerFactory {

    private final Configuration config;

    /**
     * Constructs a request handler factory by taking the application configuration.
     *
     * @param config the application configuration.
     * @throws NullPointerException if the passed configuration is null.
     */
    public HttpRequestHandlerFactory(Configuration config) {
        this.config = Objects.requireNonNull(config);
    }

    /**
     * Creates the correct HTTP request handler based on the HTTP method that is given in the
     * HTTP request.
     *
     * @param request the client request to be handled.
     * @return a request handler for the requested HTTP method.
     * @throws MethodNotAllowedException if the method is not supported by the server. This holds
     *                                   true for everything else than 'HTTP GET' and 'HTTP HEAD'.
     */
    public HttpRequestHandler createHttpRequestHandler(HttpRequest request)
            throws MethodNotAllowedException {
        switch (request.getMethod()) {
            case GET:
                return new HttpGetRequestHandler(config);
            case HEAD:
                return new HttpHeadRequestHandler(config);
            default:
                throw new MethodNotAllowedException();
        }
    }
}
