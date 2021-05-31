package com.github.httpserver.exception;

import com.github.httpserver.helper.HttpResponseBuilder;
import com.github.httpserver.protocol.HttpResponse;
import com.github.httpserver.protocol.HttpStatus;

/**
 * InternalServerErrorException represents the HTTP error condition 'HTTP 500 Internal Server Error'
 * at the server side and contains an utility function to create a corresponding HTTP response model to be
 * returned to the client.
 */
public class InternalServerErrorException extends HttpException {

    private static final HttpStatus STATUS = HttpStatus.HTTP_INTERNAL_SERVER_ERROR;

    public InternalServerErrorException(Throwable cause) {
        super(cause);
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
     * The exception message is not included in the body.
     */
    @Override
    public HttpResponse toHttpResponse() {
        return new HttpResponseBuilder()
                .setStatus(STATUS)
                .appendBodyAsHTML("h1", STATUS.toString())
                .appendContentLengthHeader()
                .build();
    }
}
