package com.github.httpserver.exception;

import com.github.httpserver.helper.HttpResponseBuilder;
import com.github.httpserver.protocol.HttpResponse;
import com.github.httpserver.protocol.HttpStatus;

public class InternalServerErrorException extends HttpException {

    private final HttpStatus status = HttpStatus.HTTP_INTERNAL_SERVER_ERROR;

    public InternalServerErrorException(Throwable cause) {
        super(cause);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return status;
    }

    @Override
    public HttpResponse toHttpResponse() {
        return new HttpResponseBuilder()
                .setStatus(status)
                .appendBodyAsHTML("h1", status.toString())
                .appendContentLengthHeader()
                .build();
    }
}
