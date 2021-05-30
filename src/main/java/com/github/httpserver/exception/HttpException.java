package com.github.httpserver.exception;

import com.github.httpserver.protocol.HttpResponse;
import com.github.httpserver.protocol.HttpStatus;

public abstract class HttpException extends Exception {

    protected HttpException(String message, Throwable cause) {
        super(message, cause);
    }

    protected HttpException(Throwable cause) {
        super(cause);
    }

    protected HttpException(String message) {
        super(message);
    }

    protected HttpException() {
        super();
    }

    public abstract HttpStatus getHttpStatus();

    public abstract HttpResponse toHttpResponse();
}
