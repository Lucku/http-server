package com.github.httpserver.exception;

import com.github.httpserver.helper.HttpResponseBuilder;
import com.github.httpserver.protocol.HttpResponse;
import com.github.httpserver.protocol.HttpStatus;

/**
 * HttpVersionNotSupportedException represents the HTTP error condition 'HTTP 505 HTTP Version Not Supported'
 * at the server side and contains an utility function to create a corresponding HTTP response model to be
 * returned to the client.
 */
public class HttpVersionNotSupportedException extends HttpException {

    private static final HttpStatus STATUS = HttpStatus.HTTP_VERSION_NOT_SUPPORTED;

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
