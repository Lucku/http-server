package com.github.httpserver.exception;

import com.github.httpserver.protocol.HttpResponse;

public class BadRequestException extends HttpException {

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public int getHttpStatusCode() {
        return 403;
    }

    @Override
    public HttpResponse getHttpResponse() {
        return null;
    }
}
