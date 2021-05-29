package com.github.httpserver.exception;

import com.github.httpserver.helper.HttpResponseBuilder;
import com.github.httpserver.protocol.HttpResponse;
import com.github.httpserver.protocol.HttpStatus;

public class BadRequestException extends HttpException {

    private final HttpStatus status = HttpStatus.HTTP_BAD_REQUEST;

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
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
