package com.github.httpserver.protocol;

import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String version;
    private final String path;
    private final Map<String, String> headers;

    private HttpResponse response;

    public HttpRequest(String method, String version, String path, Map<String, String> headers,
                       HttpResponse response) {
        this.method = method;
        this.version = version;
        this.path = path;
        this.headers = headers;
        this.response = response;
    }

    public String getMethod() {
        return method;
    }

    public String getVersion() {
        return version;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public HttpResponse getResponse() {
        return response;
    }

    public void setResponse(HttpResponse response) {
        this.response = response;
    }
}
