package com.github.httpserver.server;

import com.github.httpserver.configuration.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class HttpServerTest {

    private final String md5HashFile1 = "0AC8992824ABEB4A7062AE4A99FA0905".toLowerCase();
    private HttpServer httpServer;

    @BeforeEach()
    void setUp() throws IOException {
        Configuration testConfiguration = new Configuration(8081, "src/test/resources", "file1.html");
        httpServer = new HttpServer(testConfiguration);

        new Thread(httpServer::startServer).start();
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        httpServer.stopServer();
    }

    @Test
    @Timeout(value = 5)
    void shouldAcceptReadAndWriteClientConnection() throws InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/"))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertAll(
                    () -> assertEquals(200, response.statusCode()),
                    () -> assertEquals("<h1>This is a test file</h1>", response.body()),
                    () -> assertTrue(response.headers().firstValue("Content-Length").isPresent()),
                    () -> assertTrue(response.headers().firstValue("ETag").isPresent()),
                    () -> assertTrue(response.headers().firstValue("Last-Modified").isPresent())
            );

        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void shouldNotRunServerMultipleTimes() throws InterruptedException {
        // running multiple servers in different threads create concurrency issues,
        // so we have to wait a little
        TimeUnit.SECONDS.sleep(1);
        assertThrows(IllegalStateException.class, httpServer::startServer);
    }

    @Test
    void shouldNotRunServerAfterIsStopped() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        httpServer.stopServer();
        TimeUnit.SECONDS.sleep(1);
        assertThrows(IllegalStateException.class, httpServer::startServer);
    }
}