package com.github.httpserver.server;

import com.github.httpserver.configuration.Configuration;
import com.github.httpserver.exception.HttpException;
import com.github.httpserver.exception.HttpVersionNotSupportedException;
import com.github.httpserver.file.HttpFileInfoRetriever;
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

/**
 * HttpClientHandler is a concrete ClientHandler managing HTTP client connections.
 */
class HttpClientHandler implements ClientHandler {

    private static final int READ_BUFFER_SIZE = 4096;

    private final Map<SocketChannel, HttpContext> clientSockets;
    private final HttpRequestParser requestParser;
    private final HttpRequestHandlerFactory requestHandlerFactory;

    /**
     * Constructs a new HttpClientHandler based on an application configuration.
     *
     * @param config an instance of the application configuration that is needed in order for the
     *               client handler to respect user-defined parameters.
     */
    public HttpClientHandler(Configuration config) {
        clientSockets = new ConcurrentHashMap<>();
        requestParser = new HttpRequestParser();
        requestHandlerFactory = new HttpRequestHandlerFactory(config);
    }

    /**
     * Handles new client connections by creating new client stacks with empty HTTP context.
     *
     * @param client the general channel used for communication with the client.
     * @throws IOException if the remote address of the client cannot be determined due to connectivity
     *                     issues.
     */
    @Override
    public void acceptClient(SocketChannel client) throws IOException {
        Logger.debug("Established client connection from {}", client.getRemoteAddress());
        clientSockets.put(client, HttpContext.EMPTY_CONTEXT);
    }

    /**
     * Reads the incoming HTTP request of the client and creates and parses it in order to append
     * the information to the internally hold client stack. If the client request cannot be read
     * at this point, an HTTP response is already created and also directly appended for the
     * future response.
     *
     * @param client the readable channel used for incoming communication from the client.
     * @throws IOException if there are connectivity issues with the client.
     */
    @Override
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

            if (!request.getVersion().equals(ServerConstants.SUPPORTED_HTTP_VERSION)) {
                throw new HttpVersionNotSupportedException();
            }

            Logger.debug("Received {} request at path {}", request.getMethod(), request.getPath());
        } catch (HttpException e) {
            context.setResponse(e.toHttpResponse());
        }
    }

    /**
     * Creates and eventually writes HTTP response to the client. If the client stack already contains
     * a defined response set by {@link #handleRead(ReadableByteChannel)}}, this response is directly written to the client socket.
     * If not, a response is created by regularly processing the client's HTTP request. After wiring the
     * response, the client stack is preserved but only the response object is removed from its stack. That
     * allows for future requests to be processed over the same socket connection, e.g. when using persistent
     * connections.
     *
     * @param client the writable channel used for outgoing communication with the client.
     * @throws IOException if there are connectivity issues with the client.
     */
    @Override
    public void handleWrite(WritableByteChannel client) throws IOException {

        HttpContext context = clientSockets.get((SocketChannel) client);

        if (context == null) {
            throw new IOException("Tried to write to client that was not read before");
        }

        if (context.getResponse() == null) {
            try {
                HttpRequestHandler requestHandler = requestHandlerFactory.createHttpRequestHandler(context.getRequest());
                HttpResponse response = requestHandler.handleRequest(context.getRequest(),
                        new HttpFileInfoRetriever(context.getRequest().getHeaders()));
                context.setResponse(response);
            } catch (HttpException e) {
                context.setResponse(e.toHttpResponse());
            }
        }

        // set connection header based on client request
        if (context.isTransientConnection()) {
            context.getResponse().getHeaders().put(HttpHeader.HEADER_CONNECTION, HttpHeader.CONNECTION_CLOSE);
        } else {
            context.getResponse().getHeaders().put(HttpHeader.HEADER_CONNECTION, HttpHeader.CONNECTION_KEEP_ALIVE);
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

    /**
     * Checks all client stacks for already closed connections and removes them if that is the case.
     */
    @Override
    public void cleanupConnections() {
        clientSockets.keySet().removeIf(socketChannel -> !socketChannel.isOpen());
    }
}
