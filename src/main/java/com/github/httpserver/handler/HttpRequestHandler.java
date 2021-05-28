package com.github.httpserver.handler;

import com.github.httpserver.protocol.HttpResponse;

public interface HttpRequestHandler {

    public HttpResponse handleRequest();
}
