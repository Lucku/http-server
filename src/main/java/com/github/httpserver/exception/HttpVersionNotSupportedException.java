package com.github.httpserver.exception;

import com.github.httpserver.helper.HttpResponseBuilder;
import com.github.httpserver.protocol.HttpResponse;
import com.github.httpserver.protocol.HttpStatus;

public class HttpVersionNotSupportedException extends HttpException {

    private static final HttpStatus STATUS = HttpStatus.HTTP_VERSION_NOT_SUPPORTED;

    @Override
    public HttpStatus getHttpStatus() {
        return STATUS;
    }

    @Override
    public HttpResponse toHttpResponse() {
        return new HttpResponseBuilder()
                .setStatus(STATUS)
                .appendBodyAsHTML("h1", STATUS.toString())
                .appendContentLengthHeader()
                .build();
    }
}
