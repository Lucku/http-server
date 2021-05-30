package com.github.httpserver.exception;

import com.github.httpserver.protocol.HttpResponse;
import com.github.httpserver.protocol.HttpStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PreconditionFailedExceptionTest {

    @Test
    void shouldCorrectlyCreateHttpResponse() {

        PreconditionFailedException exception = new PreconditionFailedException();

        HttpResponse response = exception.toHttpResponse();

        assertAll(
                () -> assertEquals(HttpStatus.HTTP_PRECONDITION_FAILED, exception.getHttpStatus()),
                () -> assertEquals("The resource doesn't match one of the request conditions",
                        exception.getMessage()),
                () -> assertEquals(HttpStatus.HTTP_PRECONDITION_FAILED, response.getStatus()),
                () -> assertTrue(new String(response.getBody()).contains(HttpStatus.HTTP_PRECONDITION_FAILED.toString())),
                () -> assertTrue(new String(response.getBody()).contains(exception.getMessage()))
        );
    }

}