package com.github.httpserver.exception;

import com.github.httpserver.helper.HttpResponseBuilder;
import com.github.httpserver.protocol.HttpResponse;
import com.github.httpserver.protocol.HttpStatus;

/**
 * BadRequestException represents the HTTP error condition 'HTTP 400 Bad Request' at the server
 * side and contains an utility function to create a corresponding HTTP response model to be
 * returned to the client.
 */
public class BadRequestException extends HttpException {

    private static final HttpStatus STATUS = HttpStatus.HTTP_BAD_REQUEST;

    /**
     * Constructs a new Bad Request exception by taking a message and a cause exception.
     *
     * @param message the message of the exception. This message will be included in the
     *                body of the HTTP response model constructed by this class.
     * @param cause   the exception that caused this exception to arise.
     */
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
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
     * The exception message is included in the response body.
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
