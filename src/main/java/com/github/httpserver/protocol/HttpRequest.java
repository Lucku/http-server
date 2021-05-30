package com.github.httpserver.protocol;

import java.util.Map;
import java.util.Objects;

/**
 * HttpRequest represents the model of an HTTP request.
 */
public class HttpRequest {

    private final HttpMethod method;
    private final String version;
    private final String path;
    private final Map<String, String> headers;

    /**
     * Constructs an HTTP request model by taking all the relevant information that is parsed from an HTTP request.
     *
     * @param method  the HTTP method to be performed. See {@link HttpMethod} for possible values.
     * @param version the HTTP version entry as defined in the request.
     * @param path    the requested resource path.
     * @param headers a map of all request header entries.
     * @throws NullPointerException if any of the input parameters are null.
     */
    public HttpRequest(HttpMethod method, String version, String path, Map<String, String> headers) {
        this.method = Objects.requireNonNull(method);
        this.version = Objects.requireNonNull(version);
        this.path = Objects.requireNonNull(path);
        this.headers = Objects.requireNonNull(headers);
    }

    /**
     * Returns the requested HTTP method to be performed.
     *
     * @return the HTTP method as enum constant.
     */
    public HttpMethod getMethod() {
        return method;
    }

    /**
     * Returns the HTTP version entry as defined in the request.
     *
     * @return the HTTP version string.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Returns the path to the requested resource.
     *
     * @return the resource path string.
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the map of header entries, organized as headerKey -> headerValue.
     *
     * @return the complete map of HTTP request headers.
     */
    public Map<String, String> getHeaders() {
        return headers;
    }
}
