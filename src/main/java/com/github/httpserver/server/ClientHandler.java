package com.github.httpserver.server;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;

/**
 * ClientHandler is responsible for managing all incoming TCP client connections. It offers different
 * lifecycle hooks that the server can rely on. In a usual communication workflow, the provided
 * methods are called in the following order:
 * (1) acceptClient: entry point for new client connections
 * (2) readClient: read client request
 * (3) writeClient: write client response
 * (4) cleanupConnections: cleanup all outdated client connections
 */
public interface ClientHandler {

    /**
     * Performs housekeeping actions after a new client connected to the server. This usually involves
     * creating and storing a client stack.
     *
     * @param client the general channel used for communication with the client.
     * @throws IOException if there is a network or protocol problem that prevented the client connection
     *                     to be correctly handled.
     */
    void acceptClient(SocketChannel client) throws IOException;

    /**
     * Reads and processes the request received from the client.
     *
     * @param client the readable channel used for incoming communication from the client.
     * @throws IOException if there is a network or protocol problem that prevented the request to be
     *                     correctly read from the client.
     */
    void handleRead(ReadableByteChannel client) throws IOException;

    /**
     * Creates responses and sends them over the corresponding TCP socket to the client.
     *
     * @param client the writable channel used for outgoing communication with the client.
     * @throws IOException if there is a network or protocol problem that prevented the response to
     *                     be correctly transmitted to the client.
     */
    void handleWrite(WritableByteChannel client) throws IOException;

    /**
     * Cleans up multiple client connection based on certain criteria. This method should be regularly called
     * as part of the server loop to avoid memory getting cluttered by expired client stacks.
     */
    void cleanupConnections();
}
