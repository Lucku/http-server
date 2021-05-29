package com.github.httpserver.server;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;

public interface ClientHandler {

    void handleWrite(WritableByteChannel client) throws IOException;

    void acceptClient(SocketChannel client) throws IOException;

    void handleRead(ReadableByteChannel client) throws IOException;

    void cleanupConnections();
}
