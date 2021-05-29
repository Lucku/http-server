package com.github.httpserver.exception;

import com.github.httpserver.protocol.HttpResponse;
import com.github.httpserver.protocol.HttpStatus;

public abstract class HttpException extends Exception {

    public HttpException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpException(Throwable cause) {
        super(cause);
    }

    public HttpException() {
        super();
    }

    public HttpException(String message) {
        super(message);
    }

    public abstract HttpStatus getHttpStatus();

    public abstract HttpResponse toHttpResponse();
}
