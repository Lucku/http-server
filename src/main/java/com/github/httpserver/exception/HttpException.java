package com.github.httpserver.exception;

import com.github.httpserver.protocol.HttpResponse;

public abstract class HttpException extends Exception {

    public HttpException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract int getHttpStatusCode();

    public abstract HttpResponse getHttpResponse();
}
