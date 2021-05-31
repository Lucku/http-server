package com.github.httpserver.exception;

import com.github.httpserver.protocol.HttpResponse;
import com.github.httpserver.protocol.HttpStatus;

/**
 * HttpException is an abstract HTTP error condition (server or client error).
 */
public abstract class HttpException extends Exception {

    /**
     * Constructs an HTTP exception by taking an error message and a cause exception.
     *
     * @param message the message of the exception.
     * @param cause   the exception that caused this exception to arise.
     */
    protected HttpException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an HTTP exception by taking a cause exception.
     *
     * @param cause the exception that caused this exception to arise.
     */
    protected HttpException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs an HTTP exception by taking an error message.
     *
     * @param message the message of the exception.
     */
    protected HttpException(String message) {
        super(message);
    }

    /**
     * Constructs an HTTP exception without cause exception or message.
     */
    protected HttpException() {
        super();
    }

    /**
     * Returns the specific HTTP status code for the exception.
     *
     * @return the status code as enum constant.
     */
    public abstract HttpStatus getHttpStatus();

    /**
     * Converts this exception into an HTTP response model, considering error-specific
     * contents in the response contents.
     *
     * @return an HTTP response that corresponds to the error condition of this exception.
     */
    public abstract HttpResponse toHttpResponse();
}
