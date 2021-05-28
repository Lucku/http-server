package com.github.httpserver.handler;

import com.github.httpserver.protocol.HttpRequestContext;
import com.github.httpserver.protocol.HttpResponse;

public class HttpGetRequestHandler implements HttpRequestHandler {

    private final HttpRequestContext requestContext;

    public HttpGetRequestHandler(HttpRequestContext requestContext) {
        this.requestContext = requestContext;
    }

    @Override
    public HttpResponse handleRequest() {
        return null;
    }
}
