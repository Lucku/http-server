package com.github.httpserver.configuration;

import com.github.httpserver.server.ServerConstants;
import org.tinylog.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

/**
 * Configuration holds the application configuration and contains an accessible field
 * for every configuration parameter.
 */
public class Configuration {

    /**
     * Returns a configuration initialized with default parameters.
     * <p>
     * The default port is set to 8080.
     * <p>
     * The default source path is "www".
     * <p>
     * The default root resource is "index.html".
     */
    public static final Configuration DEFAULT = new Configuration();

    /**
     * The default port at which the server is started.
     */
    public static final int DEFAULT_PORT = 8080;
    /**
     * The default path from where the server serves static files via HTTP.
     */
    private static final String DEFAULT_SOURCE_PATH = "www";
    /**
     * The default resource that the server returns in case that "/" is requested by the client.
     */
    private static final String DEFAULT_ROOT_RESOURCE = "index.html";

    private final int port;
    private final String sourcePath;
    private final String rootResource;

    /**
     * Constructs a configuration by setting the default values for all parameters.
     */
    public Configuration() {
        this.port = DEFAULT_PORT;
        this.sourcePath = DEFAULT_SOURCE_PATH;
        this.rootResource = DEFAULT_ROOT_RESOURCE;
    }

    /**
     * Constructs a configuration by taking all parameters.
     *
     * @param port         the port at which the server is started.
     * @param sourcePath   the path from where the server serves static files via HTTP.
     * @param rootResource the resource that the server returns in case that "/" is requested by the client.
     */
    public Configuration(int port, String sourcePath, String rootResource) {
        this.port = port;
        this.sourcePath = sourcePath;
        this.rootResource = rootResource;
    }

    /**
     * Attempts to read configuration parameters from a YAML file at the specified file path. If
     * the file cannot be read, {@link #DEFAULT} is returned. If the file can be read, only valid
     * parameters are set in the returned configuration and missing parameters are set as in
     * {@link #DEFAULT}.
     *
     * @param filePath the path to the YAML configuration file.
     * @return an initialized configuration.
     */
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

    /**
     * Returns the port at which the server is started.
     *
     * @return the port as integer.
     */
    public int getPort() {
        return port;
    }

    /**
     * Returns the path from where the server serves static files via HTTP.
     *
     * @return the source path as string.
     */
    public String getSourcePath() {
        return sourcePath;
    }

    /**
     * Returns the resource that the server returns in case that "/" is requested by the client.
     *
     * @return the root resource as string.
     */
    public String getRootResource() {
        return rootResource;
    }
}
