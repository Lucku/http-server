package com.github.httpserver.helper;

import com.github.httpserver.exception.BadRequestException;
import com.github.httpserver.exception.HttpException;
import com.github.httpserver.protocol.HttpMethod;
import com.github.httpserver.protocol.HttpRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestParserTest {

    private final HttpRequestParser parser = new HttpRequestParser();

    @Test
    void shouldParseRequestWhenCorrectRequestLineAndHeaders() {
        String requestLine = "GET /test.html HTTP/1.1";
        String[] headers = new String[]{
                "Connection: keep-alive",
                "Accept: application/json",
                "If-Modified-Since: Wed, 21 Oct 2015 07:28:00 GMT"
        };
        byte[] rawRequest = createRawRequest(requestLine, headers);

        try {
            HttpRequest request = parser.parseRequest(rawRequest);
            Assertions.assertAll(
                    () -> assertEquals("HTTP/1.1", request.getVersion()),
                    () -> assertEquals(HttpMethod.GET, request.getMethod()),
                    () -> assertEquals("/test.html", request.getPath()),
                    () -> assertEquals("keep-alive", request.getHeaders().get("Connection")),
                    () -> assertEquals("application/json", request.getHeaders().get("Accept")),
                    () -> assertEquals("Wed, 21 Oct 2015 07:28:00 GMT", request.getHeaders().get("If-Modified-Since"))
            );
        } catch (HttpException e) {
            fail(e);
        }
    }

    @Test
    void shouldParseRequestWhenOnlyRequestLine() {
        String requestLine = "HEAD /path/to/resource.html HTTP/1.1";
        byte[] rawRequest = createRawRequest(requestLine);

        try {
            HttpRequest request = parser.parseRequest(rawRequest);
            Assertions.assertAll(
                    () -> assertEquals("HTTP/1.1", request.getVersion()),
                    () -> assertEquals(HttpMethod.HEAD, request.getMethod()),
                    () -> assertEquals("/path/to/resource.html", request.getPath())
            );
        } catch (HttpException e) {
            fail(e);
        }
    }

    @Test
    void shouldStripQueryParamsWhenAppearingInPath() {

        String requestLine = "HEAD /app.json?param=value HTTP/1.1";
        byte[] rawRequest = createRawRequest(requestLine);

        try {
            HttpRequest request = parser.parseRequest(rawRequest);
            Assertions.assertAll(
                    () -> assertEquals("HTTP/1.1", request.getVersion()),
                    () -> assertEquals(HttpMethod.HEAD, request.getMethod()),
                    () -> assertEquals("/app.json", request.getPath())
            );
        } catch (HttpException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "HEAD / HTTP/1.1 Other",
            "RETRIEVE /",
            "HEAD / HTTP/1.1 Other"
    })
    void shouldThrowExceptionWhenRequestLineMalformed(String requestLine) {
        byte[] rawRequest = createRawRequest(requestLine);
        assertThrows(BadRequestException.class, () -> parser.parseRequest(rawRequest));
    }

    @Test
    void shouldThrowExceptionWhenRequestLineIncomplete() {
        String requestLine = "HEAD /app.json?param=value";
        byte[] rawRequest = createRawRequest(requestLine);
        assertThrows(BadRequestException.class, () -> parser.parseRequest(rawRequest));
    }

    @Test
    void shouldThrowExceptionWhenMalformedMethod() {
        String requestLine = "RETRIEVE /app.json?param=value HTTP/1.1 Other";
        byte[] rawRequest = createRawRequest(requestLine);
        assertThrows(BadRequestException.class, () -> parser.parseRequest(rawRequest));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Connection keep-alive",
            "Connection",
            ": keepAlive",
    })
    void shouldThrowExceptionWhenMalformedHeaderEntry(String headerEntry) {
        String requestLine = "GET / HTTP/1.1";
        byte[] rawRequest = createRawRequest(requestLine, headerEntry);
        assertThrows(BadRequestException.class, () -> parser.parseRequest(rawRequest));
    }

    private byte[] createRawRequest(String requestLine, String... headers) {
        StringBuilder requestBuilder = new StringBuilder();
        requestBuilder.append(requestLine).append("\r\n");
        for (String header : headers) {
            requestBuilder.append(header).append("\r\n");
        }
        requestBuilder.append("\r\n");
        return requestBuilder.toString().getBytes();
    }
}