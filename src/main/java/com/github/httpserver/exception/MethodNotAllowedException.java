package com.github.httpserver.exception;

import com.github.httpserver.helper.HttpResponseBuilder;
import com.github.httpserver.protocol.HttpResponse;
import com.github.httpserver.protocol.HttpStatus;

public class MethodNotAllowedException extends HttpException {

    private final HttpStatus status = HttpStatus.HTTP_METHOD_NOT_ALLOWED;

    public MethodNotAllowedException() {
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
                .appendBodyAsHTML("p", "The HTTP method is not supported by the resource")
                .appendContentLengthHeader()
                .build();
    }
}
