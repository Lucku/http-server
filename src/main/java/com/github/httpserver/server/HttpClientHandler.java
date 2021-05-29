package com.github.httpserver.server;

import com.github.httpserver.configuration.Configuration;
import com.github.httpserver.exception.HttpException;
import com.github.httpserver.handler.HttpRequestHandler;
import com.github.httpserver.handler.HttpRequestHandlerFactory;
import com.github.httpserver.helper.HttpRequestParser;
import com.github.httpserver.protocol.HttpContext;
import com.github.httpserver.protocol.HttpHeader;
import com.github.httpserver.protocol.HttpRequest;
import com.github.httpserver.protocol.HttpResponse;
import org.tinylog.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class HttpClientHandler implements ClientHandler {

    private static final int READ_BUFFER_SIZE = 4096;

    private final Map<SocketChannel, HttpContext> clientSockets;
    private final HttpRequestParser requestParser;
    private final HttpRequestHandlerFactory requestHandlerFactory;

    public HttpClientHandler(Configuration config) {
        clientSockets = new ConcurrentHashMap<>();
        requestParser = new HttpRequestParser();
        requestHandlerFactory = new HttpRequestHandlerFactory(config);
    }

    public void acceptClient(SocketChannel client) throws IOException {
        Logger.debug("Established client connection from {}", client.getRemoteAddress());
        clientSockets.put(client, HttpContext.EMPTY_CONTEXT);
    }

    public void handleRead(ReadableByteChannel client) throws IOException {

        HttpContext context = clientSockets.get((SocketChannel) client);

        if (context == null) {
            throw new IOException("Tried to read from client that was not registered as connected before");
        }

        ByteBuffer readBuffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
        int contentLength = client.read(readBuffer);

        if (contentLength < 0) {
            client.close();
            clientSockets.remove((SocketChannel) client);
            return;
        }

        try {
            HttpRequest request = requestParser.parseRequest(readBuffer.array());
            context.setRequest(request);
            Logger.debug("Received {} request at path {}", request.getMethod(), request.getPath());
        } catch (HttpException e) {
            context.setResponse(e.toHttpResponse());
        }
    }

    public void handleWrite(WritableByteChannel client) throws IOException {

        HttpContext context = clientSockets.get((SocketChannel) client);

        if (context == null) {
            throw new IOException("Tried to write to client that was not read before");
        }

        if (context.getResponse() == null) {
            try {
                HttpRequestHandler requestHandler = requestHandlerFactory.createHttpRequestHandler(context.getRequest());
                HttpResponse response = requestHandler.handleRequest();
                context.setResponse(response);
            } catch (HttpException e) {
                context.setResponse(e.toHttpResponse());
            }
        }

        // set connection header based on client request
        if (context.isPersistentConnection()) {
            context.getResponse().getHeaders().put(HttpHeader.HEADER_CONNECTION, HttpHeader.CONNECTION_KEEP_ALIVE);
        } else {
            context.getResponse().getHeaders().put(HttpHeader.HEADER_CONNECTION, HttpHeader.CONNECTION_CLOSE);
        }

        ByteBuffer writeBuffer = context.getResponse().toByteBuffer();
        while (writeBuffer.hasRemaining()) {
            client.write(writeBuffer);
        }
        Logger.debug("Responded request at path {} with {}", context.getRequest().getPath(),
                context.getResponse().getStatus());
        // reset response for future requests from client
        context.setResponse(null);
    }

    @Override
    public void cleanupConnections() {
        clientSockets.keySet().removeIf(socketChannel -> !socketChannel.isOpen());
    }
}
