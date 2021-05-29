package com.github.httpserver.exception;

import com.github.httpserver.helper.HttpResponseBuilder;
import com.github.httpserver.protocol.HttpResponse;
import com.github.httpserver.protocol.HttpStatus;

public class NotFoundException extends HttpException {

    private final HttpStatus status = HttpStatus.HTTP_NOT_FOUND;

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return status;
    }

    @Override
    public HttpResponse toHttpResponse() {
        HttpResponseBuilder responseBuilder = new HttpResponseBuilder();
        responseBuilder.setStatus(status);
        responseBuilder.appendBodyAsHTML("h1", status.toString());
        responseBuilder.appendBodyAsHTML("p", getMessage());
        return responseBuilder.build();
    }
}
