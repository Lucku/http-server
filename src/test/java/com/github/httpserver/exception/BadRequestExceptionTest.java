package com.github.httpserver.exception;

import com.github.httpserver.protocol.HttpResponse;
import com.github.httpserver.protocol.HttpStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BadRequestExceptionTest {

    @Test
    void shouldCorrectlyCreateHttpResponse() {

        Exception testException = new Exception("Something went wrong");

        BadRequestException exception = new BadRequestException(testException.getMessage(), testException);

        HttpResponse response = exception.toHttpResponse();

        assertAll(
                () -> assertEquals(HttpStatus.HTTP_BAD_REQUEST, exception.getHttpStatus()),
                () -> assertEquals("Something went wrong", exception.getMessage()),
                () -> assertEquals(HttpStatus.HTTP_BAD_REQUEST, response.getStatus()),
                () -> assertTrue(new String(response.getBody()).contains(HttpStatus.HTTP_BAD_REQUEST.toString())),
                () -> assertTrue(new String(response.getBody()).contains("Something went wrong"))
        );
    }
}