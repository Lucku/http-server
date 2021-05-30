package com.github.httpserver.handler;

import com.github.httpserver.configuration.Configuration;
import com.github.httpserver.exception.HttpException;
import com.github.httpserver.exception.NotFoundException;
import com.github.httpserver.exception.PreconditionFailedException;
import com.github.httpserver.file.FileInfo;
import com.github.httpserver.file.FileInfoRetriever;
import com.github.httpserver.file.HttpFileInfoRetriever;
import com.github.httpserver.protocol.HttpMethod;
import com.github.httpserver.protocol.HttpRequest;
import com.github.httpserver.protocol.HttpResponse;
import com.github.httpserver.protocol.HttpStatus;
import com.github.httpserver.server.ServerConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HttpHeadRequestHandlerTest {

    private final Path resourcePath = Path.of("src/test/resources");
    private Configuration testConfiguration = Configuration.DEFAULT;

    @BeforeEach
    void setUp() {
        testConfiguration = new Configuration(8080, "src/test/resources", "file1.html");
    }

    @Test
    void shouldCreateOKResponseWhenFileReadableAndNoPreconditions() {

        HttpRequest testRequest = new HttpRequest(HttpMethod.HEAD, ServerConstants.SUPPORTED_HTTP_VERSION, "/",
                new HashMap<>());
        FileInfo testFileInfo = new FileInfo(true, true, "application/html",
                "Wed, 21 Oct 2015 07:28:00 GMT", resourcePath.resolve("file1.html"));
        FileInfoRetriever fileRetrieverMock = mock(FileInfoRetriever.class);

        try {
            when(fileRetrieverMock.retrieveFileInfo(any())).thenReturn(testFileInfo);
        } catch (IOException e) {
            fail(e);
        }

        HttpHeadRequestHandler requestHandler = new HttpHeadRequestHandler(testConfiguration);

        try {
            HttpResponse response = requestHandler.handleRequest(testRequest, fileRetrieverMock);

            assertAll(
                    () -> assertEquals(HttpStatus.HTTP_OK, response.getStatus()),
                    () -> assertNull(response.getBody()),
                    () -> assertEquals("application/html", response.getHeaders().get("Content-Type")),
                    () -> assertTrue(response.getHeaders().containsKey("ETag")),
                    () -> assertEquals("Wed, 21 Oct 2015 07:28:00 GMT", response.getHeaders().get("Last-Modified"))
            );
        } catch (HttpException e) {
            fail(e);
        }
    }

    @Test
    void shouldThrowExceptionIfFileMissing() {

        HttpRequest testRequest = new HttpRequest(HttpMethod.GET, ServerConstants.SUPPORTED_HTTP_VERSION,
                "/fileX.html", new HashMap<>());
        FileInfo testFileInfo = new FileInfo(true, true, "application/html",
                "Wed, 21 Oct 2015 07:28:00 GMT", resourcePath.resolve("fileX.html"));
        FileInfoRetriever fileRetrieverMock = mock(FileInfoRetriever.class);
        HttpHeadRequestHandler requestHandler = new HttpHeadRequestHandler(testConfiguration);

        assertThrows(NotFoundException.class, () -> requestHandler.handleRequest(testRequest, fileRetrieverMock));
    }

    @Test
    void shouldThrowExceptionWhenPreconditionFails() {

        HttpRequest testRequest = new HttpRequest(HttpMethod.GET, ServerConstants.SUPPORTED_HTTP_VERSION, "/",
                new HashMap<>());
        FileInfo testFileInfo = new FileInfo(false, true, "application/html",
                "Wed, 21 Oct 2015 07:28:00 GMT", resourcePath.resolve("file1.html"));
        FileInfoRetriever fileRetrieverMock = mock(FileInfoRetriever.class);

        try {
            when(fileRetrieverMock.retrieveFileInfo(any())).thenReturn(testFileInfo);
        } catch (IOException e) {
            fail(e);
        }

        HttpHeadRequestHandler requestHandler = new HttpHeadRequestHandler(testConfiguration);

        assertThrows(PreconditionFailedException.class, () -> requestHandler.handleRequest(testRequest,
                fileRetrieverMock));
    }

    @Test
    void shouldCreateNotModifiedResponseWhenConditionMatches() {

        HttpRequest testRequest = new HttpRequest(HttpMethod.HEAD, ServerConstants.SUPPORTED_HTTP_VERSION, "/",
                new HashMap<>());
        FileInfo testFileInfo = new FileInfo(true, false, "application/html",
                "Wed, 21 Oct 2015 07:28:00 GMT", resourcePath.resolve("file1.html"));
        FileInfoRetriever fileRetrieverMock = mock(FileInfoRetriever.class);

        try {
            when(fileRetrieverMock.retrieveFileInfo(any())).thenReturn(testFileInfo);
        } catch (IOException e) {
            fail(e);
        }

        HttpHeadRequestHandler requestHandler = new HttpHeadRequestHandler(testConfiguration);

        try {
            HttpResponse response = requestHandler.handleRequest(testRequest, fileRetrieverMock);
            assertEquals(HttpStatus.HTTP_NOT_MODIFIED, response.getStatus());
        } catch (HttpException e) {
            fail(e);
        }
    }

    @Test
    void shouldReturnRootResourceWhenRequestingRoot() {

        HttpRequest testRequest = new HttpRequest(HttpMethod.HEAD, ServerConstants.SUPPORTED_HTTP_VERSION, "/",
                new HashMap<>());
        FileInfoRetriever fileInfoRetriever = new HttpFileInfoRetriever(testRequest.getHeaders());
        HttpHeadRequestHandler requestHandler = new HttpHeadRequestHandler(testConfiguration);

        try {
            HttpResponse response = requestHandler.handleRequest(testRequest, fileInfoRetriever);

            assertAll(
                    () -> assertEquals(HttpStatus.HTTP_OK, response.getStatus()),
                    () -> assertNull(response.getBody()),
                    () -> assertEquals("text/html", response.getHeaders().get("Content-Type"))
            );
        } catch (HttpException e) {
            fail(e);
        }
    }
}