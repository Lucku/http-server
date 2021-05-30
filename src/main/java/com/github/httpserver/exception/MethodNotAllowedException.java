package com.github.httpserver.exception;

import com.github.httpserver.helper.HttpResponseBuilder;
import com.github.httpserver.protocol.HttpResponse;
import com.github.httpserver.protocol.HttpStatus;

public class MethodNotAllowedException extends HttpException {

    private static final HttpStatus STATUS = HttpStatus.HTTP_METHOD_NOT_ALLOWED;

    public MethodNotAllowedException() {
        super("The HTTP method is not supported by the resource");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return STATUS;
    }

    @Override
    public HttpResponse toHttpResponse() {
        return new HttpResponseBuilder()
                .setStatus(STATUS)
                .appendBodyAsHTML("h1", STATUS.toString())
                .appendBodyAsHTML("p", getMessage())
                .appendContentLengthHeader()
                .build();
    }
}
