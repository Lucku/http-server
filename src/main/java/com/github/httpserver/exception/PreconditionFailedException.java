package com.github.httpserver.exception;

import com.github.httpserver.helper.HttpResponseBuilder;
import com.github.httpserver.protocol.HttpResponse;
import com.github.httpserver.protocol.HttpStatus;

public class PreconditionFailedException extends HttpException {

    private final HttpStatus status = HttpStatus.HTTP_PRECONDITION_FAILED;

    @Override
    public HttpStatus getHttpStatus() {
        return status;
    }

    @Override
    public HttpResponse toHttpResponse() {
        HttpResponseBuilder responseBuilder = new HttpResponseBuilder()
                .setStatus(status)
                .appendBodyAsHTML("h1", status.toString())
                .appendBodyAsHTML("p", "The requested resource doesn't match resource conditions");
        return responseBuilder.build();
    }
}
