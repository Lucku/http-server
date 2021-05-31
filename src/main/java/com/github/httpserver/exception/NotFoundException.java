package com.github.httpserver.exception;

import com.github.httpserver.helper.HttpResponseBuilder;
import com.github.httpserver.protocol.HttpResponse;
import com.github.httpserver.protocol.HttpStatus;

/**
 * MethodNotAllowedException represents the HTTP error condition 'HTTP 404 Not Found'
 * at the server side and contains an utility function to create a corresponding HTTP response model to be
 * returned to the client.
 */
public class NotFoundException extends HttpException {

    private static final HttpStatus STATUS = HttpStatus.HTTP_NOT_FOUND;

    public NotFoundException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpStatus getHttpStatus() {
        return STATUS;
    }

    /**
     * {@inheritDoc}
     * A general exception message for this error is included in the body.
     */
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
