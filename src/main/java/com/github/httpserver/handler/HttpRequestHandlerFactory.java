package com.github.httpserver.handler;

import com.github.httpserver.exception.MethodNotAllowedException;
import com.github.httpserver.protocol.HttpMethod;
import com.github.httpserver.protocol.HttpRequestContext;

public class HttpRequestHandlerFactory {

    public HttpRequestHandler createHttpRequestHandler(HttpRequestContext requestContext)
            throws MethodNotAllowedException {
        switch (requestContext.getMethod()) {
            case HttpMethod.HTTP_GET:
                return new HttpGetRequestHandler(requestContext);
            case HttpMethod.HTTP_HEAD:
                return new HttpHeadRequestHandler();
            default:
                // TODO: Proper error handling
                throw new MethodNotAllowedException();
        }
    }
}
