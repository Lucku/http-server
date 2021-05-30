package com.github.httpserver.exception;

import com.github.httpserver.protocol.HttpResponse;
import com.github.httpserver.protocol.HttpStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InternalServerErrorExceptionTest {

    @Test
    void shouldCorrectlyCreateHttpResponse() {

        Exception testException = new Exception("Something went wrong");

        InternalServerErrorException exception = new InternalServerErrorException(testException);

        HttpResponse response = exception.toHttpResponse();

        assertAll(
                () -> assertEquals(HttpStatus.HTTP_INTERNAL_SERVER_ERROR, exception.getHttpStatus()),
                () -> assertEquals(HttpStatus.HTTP_INTERNAL_SERVER_ERROR, response.getStatus()),
                () -> assertTrue(new String(response.getBody()).contains(HttpStatus.HTTP_INTERNAL_SERVER_ERROR.toString()))
        );
    }
}