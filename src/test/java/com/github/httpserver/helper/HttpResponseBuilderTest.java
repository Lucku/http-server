package com.github.httpserver.helper;

import com.github.httpserver.protocol.HttpResponse;
import com.github.httpserver.protocol.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HttpResponseBuilderTest {

    private HttpResponseBuilder responseBuilder;

    @BeforeEach
    void setUp() {
        responseBuilder = new HttpResponseBuilder();
    }

    @Test
    void shouldBuildCorrectlyWhenConstructedWithDefaults() {
        HttpResponse response = responseBuilder.build();

        assertAll(
                () -> assertEquals("HTTP/1.1", response.getVersion()),
                () -> assertEquals(HttpStatus.HTTP_OK, response.getStatus()),
                () -> assertEquals(0, response.getHeaders().size()),
                () -> assertNull(response.getBody())
        );
    }

    @Test
    void shouldBuildCorrectlyWhenValuesSetManually() {

        responseBuilder.setVersion("HTTP/2")
                .setStatus(HttpStatus.HTTP_BAD_REQUEST)
                .setHeaders(Map.of(
                        "Connection", "keep-alive",
                        "Accept", "application/json",
                        "If-Match", "*"
                ))
                .setBody("Test".getBytes());

        HttpResponse response = responseBuilder.build();

        assertAll(
                () -> assertEquals(HttpStatus.HTTP_BAD_REQUEST, response.getStatus()),
                () -> assertEquals("HTTP/2", response.getVersion()),
                () -> assertEquals("keep-alive", response.getHeaders().get("Connection")),
                () -> assertEquals("application/json", response.getHeaders().get("Accept")),
                () -> assertEquals("*", response.getHeaders().get("If-Match")),
                () -> assertArrayEquals("Test".getBytes(), response.getBody())
        );
    }

    @Test
    void shouldBuildCorrectlyWhenContextHeadersAppended() {

        responseBuilder.setBody("<h1>Title</h1>".getBytes())
                .appendHeader("Connection", "keep-alive")
                .appendETagHeader()
                .appendContentLengthHeader();

        String md5Hash = "DFA453DF07DB2C20F8B513B572AFACF3".toLowerCase();

        HttpResponse response = responseBuilder.build();

        assertAll(
                () -> assertEquals("keep-alive", response.getHeaders().get("Connection")),
                () -> assertEquals(md5Hash, response.getHeaders().get("ETag")),
                () -> assertEquals("14", response.getHeaders().get("Content-Length")),
                () -> assertArrayEquals("<h1>Title</h1>".getBytes(), response.getBody())
        );
    }

    @Test
    void shouldBuildCorrectlyWhenBodyAppended() {

        responseBuilder.appendTextBody("This")
                .appendTextBody("is")
                .appendTextBody("a")
                .appendTextBody("test")
                .appendBodyAsHTML("a", "link")
                .build();

        HttpResponse response = responseBuilder.build();

        assertArrayEquals("This\r\nis\r\na\r\ntest\r\n<a>link</a>\r\n".getBytes(), response.getBody());
    }

    @Test
    void shouldNotAppendETagWhenBodyMissing() {
        responseBuilder.appendETagHeader().build();
        HttpResponse response = responseBuilder.build();
        assertFalse(response.getHeaders().containsKey("ETag"));
    }
}