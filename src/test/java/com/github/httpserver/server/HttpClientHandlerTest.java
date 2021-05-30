package com.github.httpserver.server;

import com.github.httpserver.configuration.Configuration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HttpClientHandlerTest {

    @Test
    void shouldKeepClientConnectionAlive() throws Exception {

        Configuration testConfiguration = new Configuration(8081, "src/test/resources", "file1.html");

        HttpClientHandler clientHandler = new HttpClientHandler(testConfiguration);
        HttpClientHandler spyHandler = spy(clientHandler);

        Field reader = HttpServer.class.getDeclaredField("clientHandler");
        reader.setAccessible(true);
        HttpServer httpServer = null;
        try {
            httpServer = new HttpServer(testConfiguration);
        } catch (IOException e) {
            fail(e);
        }
        reader.set(httpServer, spyHandler);

        new Thread(httpServer::startServer).start();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/"))
                .build();

        for (int i = 0; i < 10; i++) {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        try {
            verify(spyHandler, Mockito.times(1)).acceptClient(any());
        } catch (IOException e) {
            fail(e);
        }

        httpServer.stopServer();
    }

    @Test
    void testKeepAliveActive() throws NoSuchFieldException, IllegalAccessException, InterruptedException {

        Configuration testConfiguration = new Configuration(8081, "src/test/resources", "file1.html");

        HttpClientHandler clientHandler = new HttpClientHandler(testConfiguration);
        HttpClientHandler spyHandler = spy(clientHandler);

        Field reader = HttpServer.class.getDeclaredField("clientHandler");
        reader.setAccessible(true);
        HttpServer httpServer = null;
        try {
            httpServer = new HttpServer(testConfiguration);
        } catch (IOException e) {
            fail(e);
        }
        reader.set(httpServer, spyHandler);

        new Thread(httpServer::startServer).start();

        HttpURLConnection connection = null;

        int repetitions = 10;

        for (int i = 0; i < repetitions; i++) {
            try {
                URL url = new URL("http://localhost:8081/");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Connection", "close");
                connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            verify(spyHandler, Mockito.times(repetitions)).acceptClient(any());
        } catch (IOException e) {
            fail(e);
        }

        httpServer.stopServer();
    }

}