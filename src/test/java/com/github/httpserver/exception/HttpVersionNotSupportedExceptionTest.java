package com.github.httpserver.exception;

import com.github.httpserver.protocol.HttpResponse;
import com.github.httpserver.protocol.HttpStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpVersionNotSupportedExceptionTest {

    @Test
    void shouldCorrectlyCreateHttpResponse() {

        HttpVersionNotSupportedException exception = new HttpVersionNotSupportedException();

        HttpResponse response = exception.toHttpResponse();

        assertAll(
                () -> assertEquals(HttpStatus.HTTP_VERSION_NOT_SUPPORTED, exception.getHttpStatus()),
                () -> assertEquals(HttpStatus.HTTP_VERSION_NOT_SUPPORTED, response.getStatus()),
                () -> assertTrue(new String(response.getBody()).contains(HttpStatus.HTTP_VERSION_NOT_SUPPORTED.toString()))
        );
    }
}