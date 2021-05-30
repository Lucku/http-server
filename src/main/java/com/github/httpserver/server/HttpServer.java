package com.github.httpserver.server;

import com.github.httpserver.configuration.Configuration;
import org.tinylog.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * HttpServer is a web server implementation based on NIO (non-blocking IO). It opens a server socket,
 * accepts and handles incoming HTTP client connections. While this class serves as a frame for NIO
 * socket interactions, it internally makes use of a {@link HttpClientHandler} to take care of HTTP-specific lifecycle
 * operations in the client interaction.
 * <p>
 * The class is managing an internal state that can be queried from the outside. The three possible states are
 * (1) idle: the server object was constructed and is waiting to be used (can transition to state (2))
 * (2) running: {@link #startServer()} was called (can transition to state (3))
 * (3) stopped: {@link #stopServer()} was called either explicitly or implicitly inside of {@link #startServer()}
 * (can transition to state (2))
 * <p>
 * This implementation is not thread safe, i.e. the server instance should never be started/stopped concurrently.
 */
public class HttpServer {

    private final ClientHandler clientHandler;

    private final Configuration config;
    private ServerState serverState;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    /**
     * Constructs a new http server based on an application configuration.
     *
     * @param config an instance of the application configuration that is needed in order for the
     *               server to respect user-defined parameters.
     * @throws IOException          if the TCP server socket cannot be opened.
     * @throws NullPointerException if the passed configuration is null.
     */
    public HttpServer(Configuration config) throws IOException {
        this.config = Objects.requireNonNull(config);
        this.serverState = ServerState.IDLE;
        this.clientHandler = new HttpClientHandler(config);

        try {
            initializeServerSocket();
        } catch (IOException e) {
            Logger.error("Failed to instantiate server socket", e);
            throw new IOException(e);
        }
    }

    /**
     * Opens the TCP server socket and a corresponding NIO selector.
     *
     * @throws IOException if the server socket channel or selector cannot be opened.
     */
    private void initializeServerSocket() throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        int supportedOperations = serverSocketChannel.validOps();
        serverSocketChannel.register(selector, supportedOperations);
        serverSocketChannel.bind(new InetSocketAddress(config.getPort()));
    }

    /**
     * Starts the server's main loop that is listening and processing TCP client connections. This
     * method will fail if the server is already in a running state, i.e. if the method was called
     * before and the server has not been stopped since, or if it was stopped. In case that the
     * server's main loop is interrupted by a critical exception, the latter is handled silently
     * and the server stopped gracefully. For that reason it is recommended to check whether the server
     * is running using {@link #isRunning()} or {@link #isStopped()} before calling this method if there
     * is the risk of calling it subsequently.
     *
     * @throws IllegalStateException if the server is already actively running.
     */
    public void startServer() {

        if (serverState == ServerState.RUNNING) {
            throw new IllegalStateException("The server is already running");
        }

        if (serverState == ServerState.STOPPED) {
            throw new IllegalStateException("The server is stopped");
        }

        Logger.info("Started HTTP server on port {}", config.getPort());

        serverState = ServerState.RUNNING;

        try {
            while (!isStopped()) {
                selector.select();

                if (!selector.isOpen()) {
                    break;
                }

                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                Iterator<SelectionKey> keysIterator = selectionKeySet.iterator();

                while (keysIterator.hasNext()) {
                    SelectionKey selectionKey = keysIterator.next();

                    if (!selectionKey.isValid()) {
                        continue;
                    }

                    try {
                        if (selectionKey.isValid()) {
                            if (selectionKey.isAcceptable()) {
                                SocketChannel clientSocket = serverSocketChannel.accept();
                                clientSocket.configureBlocking(false);
                                clientHandler.acceptClient(clientSocket);
                                clientSocket.register(selector, SelectionKey.OP_READ);

                            } else if (selectionKey.isReadable()) {
                                SocketChannel clientSocket = (SocketChannel) selectionKey.channel();
                                clientSocket.configureBlocking(false);
                                clientHandler.handleRead(clientSocket);
                                selectionKey.interestOps(SelectionKey.OP_WRITE);

                            } else if (selectionKey.isWritable()) {
                                SocketChannel clientSocket = (SocketChannel) selectionKey.channel();
                                clientHandler.handleWrite(clientSocket);
                                selectionKey.interestOps(SelectionKey.OP_READ);
                            }
                        }
                    } catch (IOException | IllegalStateException e) {
                        selectionKey.channel().close();
                        selectionKey.cancel();
                    }

                    keysIterator.remove();
                    clientHandler.cleanupConnections();
                }
            }
        } catch (IOException e) {
            Logger.error("Stopping server due to failure", e);
        } finally {
            stopServer();
        }
    }

    /**
     * Stops the HTTP server, gracefully closing the TCP server socket and NIO selector.
     */
    public void stopServer() {
        try {
            serverSocketChannel.close();
            selector.close();
            clientHandler.cleanupConnections();
        } catch (IOException e) {
            Logger.error("Failed to stop the server", e);
        }
        serverState = ServerState.STOPPED;
        Logger.info("Stopped HTTP server");
    }

    /**
     * Indicates if the server is currently in a stopped state, i.e. if it was running before and
     * stopped since.
     *
     * @return a boolean indicating if the server is in a 'stopped' state.
     */
    public boolean isStopped() {
        return serverState == ServerState.STOPPED;
    }

    /**
     * Indicates if the server is currently in a running state, i.e. if {@link #startServer()} was
     * called before and the server was not stopped since.
     *
     * @return a boolean indicating if the server is in a 'running' state.
     */
    public boolean isRunning() {
        return serverState == ServerState.RUNNING;
    }
}
