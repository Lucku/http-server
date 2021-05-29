package com.github.httpserver;

import com.github.httpserver.configuration.Configuration;
import com.github.httpserver.server.HttpServer;

import java.io.IOException;

public class Application {

    private static final String CONFIG_FILE = "config.yml";

    public static void main(String[] args) throws IOException {
        Configuration config = Configuration.fromYamlFile(CONFIG_FILE);
        HttpServer httpServer = new HttpServer(config);
        httpServer.startServer();
    }
}
