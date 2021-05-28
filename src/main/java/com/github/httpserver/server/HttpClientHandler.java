package com.github.httpserver.server;

import com.github.httpserver.exception.HttpException;
import com.github.httpserver.exception.MethodNotAllowedException;
import com.github.httpserver.handler.HttpRequestHandler;
import com.github.httpserver.handler.HttpRequestHandlerFactory;
import com.github.httpserver.protocol.HttpRequestContext;
import com.github.httpserver.protocol.HttpRequestParser;
import com.github.httpserver.protocol.HttpResponse;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class HttpClientHandler implements ClientHandler {

    private static final int READ_BUFFER_SIZE = 4096;

    private final Map<SocketChannel, HttpRequestContext> clientSockets;
    private final HttpRequestParser requestParser;
    private final HttpRequestHandlerFactory requestHandlerFactory;

    public HttpClientHandler() {
        clientSockets = new ConcurrentHashMap<>();
        requestParser = new HttpRequestParser();
        requestHandlerFactory = new HttpRequestHandlerFactory();
    }

    public void acceptClient(SocketChannel client) {
    }

    public void handleRead(ReadableByteChannel client) throws IOException {

        ByteBuffer readBuffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
        int contentLength = client.read(readBuffer);

        if (contentLength < 0) {
            client.close();
            return;
        }

        if (!clientSockets.containsKey((SocketChannel) client)) {
            try {
                HttpRequestContext requestContext = requestParser.parseRequest(readBuffer.array());
                clientSockets.put((SocketChannel) client, requestContext);
            } catch (HttpException e) {
                // TODO: Proper exception handling
                e.printStackTrace();
            }
        }
    }

    public void handleWrite(WritableByteChannel client) throws IOException {

        HttpRequestContext requestContext = clientSockets.get((SocketChannel) client);

        if (requestContext == null) {
            throw new IOException("Tried to write to client that was not read before");
        }

        if (requestContext.getResponse() == null) {
            try {
                HttpRequestHandler requestHandler = requestHandlerFactory.createHttpRequestHandler(requestContext);
                HttpResponse response = requestHandler.handleRequest();
                requestContext.setResponse(response);
            } catch (MethodNotAllowedException e) {
                e.printStackTrace();
            }
        }

        String responseContent =
                "HTTP/1.1 200 OK\r\n" +
                        "ContentType: " + "text/html" + "\r\n" +
                        "\r\n" +
                        "<h1>Test</h1>" +
                        "\r\n\r\n";
        ByteBuffer writeBuffer = ByteBuffer.wrap(responseContent.getBytes());
        client.write(writeBuffer);
    }

    @Override
    public void cleanUp() {
        clientSockets.keySet().removeIf(socketChannel -> !socketChannel.isOpen());
    }

    /*
    private void handleClient(SelectionKey selectionKey) throws IOException {
        Path filePath = getFilePath(path);
        if (Files.exists(filePath)) {
            String contentType = guessContentType(filePath);
            sendResponse(client, "200 OK", contentType, Files.readAllBytes(filePath));
        } else {
            byte[] notFoundContent = "<h1>Not found</h1>".getBytes();
            sendResponse(client, "404 Not Found", "text/html", notFoundContent);
        }
    }
    */

    private void sendResponse(SocketChannel client, String status, String contentType, byte[] payload) throws IOException {
        String responseContent =
                "HTTP/1.1 200 OK\r\n" +
                        "ContentType: " + contentType + "\r\n" +
                        "\r\n" +
                        Arrays.toString(payload) +
                        "\r\n\r\n";
        ByteBuffer writeBuffer = ByteBuffer.wrap(responseContent.getBytes());
        client.write(writeBuffer);
    }

    private String guessContentType(Path filePath) throws IOException {
        return Files.probeContentType(filePath);
    }

    private Path getFilePath(String path) {
        if ("/".equals(path)) {
            path = "/index.html"; // TODO: default root resource configuration parameter
        }

        return Paths.get(path); // TODO: configurable path
    }
}
