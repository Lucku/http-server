package com.github.httpserver.exception;

import com.github.httpserver.protocol.HttpResponse;
import com.github.httpserver.protocol.HttpStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MethodNotAllowedExceptionTest {

    @Test
    void shouldCorrectlyCreateHttpResponse() {

        MethodNotAllowedException exception = new MethodNotAllowedException();

        HttpResponse response = exception.toHttpResponse();

        assertAll(
                () -> assertEquals(HttpStatus.HTTP_METHOD_NOT_ALLOWED, exception.getHttpStatus()),
                () -> assertEquals("The HTTP method is not supported by the resource", exception.getMessage()),
                () -> assertEquals(HttpStatus.HTTP_METHOD_NOT_ALLOWED, response.getStatus()),
                () -> assertTrue(new String(response.getBody()).contains(HttpStatus.HTTP_METHOD_NOT_ALLOWED.toString())),
                () -> assertTrue(new String(response.getBody()).contains(exception.getMessage()))
        );
    }
}