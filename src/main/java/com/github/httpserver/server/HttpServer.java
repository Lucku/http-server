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
import java.util.Set;

public class HttpServer {

    private final ClientHandler clientHandler;

    private Configuration config;
    private ServerState serverState;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    public HttpServer(Configuration config) throws IOException {
        this.config = config;
        this.serverState = ServerState.IDLE;
        this.clientHandler = new HttpClientHandler(config);

        try {
            initializeServerSocket();
        } catch (IOException e) {
            Logger.error("Failed to instantiate server socket", e);
            throw new IOException(e);
        }
    }

    private void initializeServerSocket() throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        int supportedOperations = serverSocketChannel.validOps();
        serverSocketChannel.register(selector, supportedOperations);
        serverSocketChannel.bind(new InetSocketAddress("localhost", config.getPort()));
    }

    public void startServer() {

        if (serverState == ServerState.RUNNING) {
            throw new IllegalStateException("The server is already running");
        }

        Logger.info("Started HTTP server on port {}", config.getPort());

        try {
            serverState = ServerState.RUNNING;

            while (!isStopped()) {
                selector.select();
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
                                // TODO This is a test, hat scheinbar keine Auswirkungen
                                clientSocket.socket().setKeepAlive(true);
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

    public void stopServer() {
        try {
            serverSocketChannel.close();
            selector.close();
        } catch (IOException e) {
            Logger.error("Failed to stop the server", e);
        }
        serverState = ServerState.STOPPED;
        Logger.info("Stopped HTTP server");
    }

    public boolean isStopped() {
        return serverState == ServerState.STOPPED;
    }
}
