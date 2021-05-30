package com.github.httpserver;

import com.github.httpserver.configuration.Configuration;
import com.github.httpserver.server.HttpServer;

import java.io.IOException;

public class Application {

    /**
     * Local file path to the server configuration file in YAML format.
     */
    private static final String CONFIG_FILE = "config.yml";

    /**
     * Constructs and starts an HTTP server, passing it a configuration object that is built based
     * on a YAML file input.
     *
     * @param args command line arguments. These are ignored.
     * @throws IOException if the HTTP server could not be initialized.
     */
    public static void main(String[] args) throws IOException {
        Configuration config = Configuration.fromYamlFile(CONFIG_FILE);
        HttpServer httpServer = new HttpServer(config);
        httpServer.startServer();
    }
}
