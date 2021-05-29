package com.github.httpserver.handler;

import com.github.httpserver.configuration.Configuration;
import com.github.httpserver.exception.MethodNotAllowedException;
import com.github.httpserver.protocol.HttpRequest;

public class HttpRequestHandlerFactory {

    private Configuration config;

    public HttpRequestHandlerFactory(Configuration config) {
        this.config = config;
    }

    public HttpRequestHandler createHttpRequestHandler(HttpRequest requestContext)
            throws MethodNotAllowedException {
        switch (requestContext.getMethod()) {
            case GET:
                return new HttpGetRequestHandler(requestContext, config);
            case HEAD:
                return new HttpHeadRequestHandler(requestContext, config);
            default:
                throw new MethodNotAllowedException();
        }
    }
}
