package com.github.httpserver.exception;

import com.github.httpserver.helper.HttpResponseBuilder;
import com.github.httpserver.protocol.HttpResponse;
import com.github.httpserver.protocol.HttpStatus;

public class InternalServerErrorException extends HttpException {

    private final HttpStatus status = HttpStatus.HTTP_INTERNAL_SERVER_ERROR;

    // TODO: Check if necessary
    public InternalServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalServerErrorException(Throwable cause) {
        super(cause);
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
        return responseBuilder.build();
    }
}
