package com.github.httpserver.configuration;

import com.github.httpserver.server.ServerConstants;
import org.tinylog.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class Configuration {

    public static final Configuration DEFAULT = new Configuration();

    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_SOURCE_PATH = "www";
    private static final String DEFAULT_ROOT_RESOURCE = "index.html";

    private final int port;
    private final String sourcePath;
    private final String rootResource;

    public Configuration() {
        this.port = DEFAULT_PORT;
        this.sourcePath = DEFAULT_SOURCE_PATH;
        this.rootResource = DEFAULT_ROOT_RESOURCE;
    }

    public Configuration(int port, String sourcePath, String rootResource) {
        this.port = port;
        this.sourcePath = sourcePath;
        this.rootResource = rootResource;
    }

    public static Configuration fromYamlFile(String filePath) {

        Map<String, Object> values;

        try {
            BufferedReader fileReader = Files.newBufferedReader(ServerConstants.getCurrentFilePath().resolve(filePath));
            Yaml yaml = new Yaml();
            values = yaml.load(fileReader);
        } catch (IOException e) {
            Logger.warn("Unable to read configuration file - using default values");
            return DEFAULT;
        }

        int port = (int) values.getOrDefault("port", DEFAULT_PORT);
        String sourcePath = (String) values.getOrDefault("sourcePath", DEFAULT_SOURCE_PATH);
        String rootResource = (String) values.getOrDefault("rootResource", DEFAULT_ROOT_RESOURCE);

        return new Configuration(port, sourcePath, rootResource);
    }

    public int getPort() {
        return port;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public String getRootResource() {
        return rootResource;
    }
}
