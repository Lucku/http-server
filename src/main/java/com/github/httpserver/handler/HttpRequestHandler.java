package com.github.httpserver.handler;

import com.github.httpserver.exception.HttpException;
import com.github.httpserver.protocol.HttpResponse;

public interface HttpRequestHandler {

    public HttpResponse handleRequest() throws HttpException;
}
