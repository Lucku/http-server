package com.github.httpserver.configuration;

import org.tinylog.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
            FileInputStream fileInputStream = new FileInputStream(filePath);
            Yaml yaml = new Yaml();
            values = yaml.load(fileInputStream);

        } catch (FileNotFoundException e) {
            Logger.warn("Unable to find configuration file - using default values");
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
