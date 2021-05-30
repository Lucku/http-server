package com.github.httpserver.exception;

import com.github.httpserver.protocol.HttpResponse;
import com.github.httpserver.protocol.HttpStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotFoundExceptionTest {

    @Test
    void shouldCorrectlyCreateHttpResponse() {

        NotFoundException exception = new NotFoundException("File not found");

        HttpResponse response = exception.toHttpResponse();

        assertAll(
                () -> assertEquals(HttpStatus.HTTP_NOT_FOUND, exception.getHttpStatus()),
                () -> assertEquals("File not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.HTTP_NOT_FOUND, response.getStatus()),
                () -> assertTrue(new String(response.getBody()).contains(HttpStatus.HTTP_NOT_FOUND.toString())),
                () -> assertTrue(new String(response.getBody()).contains(exception.getMessage()))
        );
    }
}