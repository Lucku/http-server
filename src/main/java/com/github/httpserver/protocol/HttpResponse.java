package com.github.httpserver.protocol;

import java.util.Map;

public class HttpResponse {

    private final String version;
    private final HttpStatus status;
    private final Map<String, String> headers;
    private final String body;

    public HttpResponse(String version, HttpStatus status, Map<String, String> headers, String body) {
        this.version = version;
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public String getVersion() {
        return version;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {

        StringBuilder responseBuilder = new StringBuilder();

        String responseLine = String.format("%s %s\r\n", version, status);

        responseBuilder.append(responseLine);

        for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
            String headerLine = String.format("%s: %s\r\n", headerEntry.getKey(), headerEntry.getValue());
            responseBuilder.append(headerLine);
        }

        if (body != null) {
            responseBuilder.append("\r\n");
            responseBuilder.append(body);
        }

        return responseBuilder.toString();

        /* TODO: Delete
        return "HTTP/1.1 200 OK\r\n" +
                "ContentType: " + "text/html" + "\r\n" +
                "\r\n" +
                "<h1>Test</h1>" +
                "\r\n\r\n";
                */
    }
}
