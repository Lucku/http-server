package com.github.httpserver.server;

/**
 * ServerState holds constants for the possible states of {@link HttpServer}.
 */
public enum ServerState {
    /**
     * Idle state meaning that the server was not started yet.
     */
    IDLE,
    /**
     * Running state meaning that the server is running actively.
     */
    RUNNING,
    /**
     * Stopped state meaning that the server has been running before and stopped.
     */
    STOPPED
}
