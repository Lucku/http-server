package com.github.httpserver;

import com.github.httpserver.server.HttpServer;

import java.io.IOException;

public class Application {

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = new HttpServer();
        httpServer.startServer();
    }
}
