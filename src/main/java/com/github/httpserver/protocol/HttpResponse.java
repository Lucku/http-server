package com.github.httpserver.protocol;

import java.nio.ByteBuffer;
import java.util.Map;

public class HttpResponse {

    private final String version;
    private final HttpStatus status;
    private final Map<String, String> headers;
    private final byte[] body;

    public HttpResponse(String version, HttpStatus status, Map<String, String> headers, byte[] body) {
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

    public ByteBuffer toByteBuffer() {

        StringBuilder responseBuilder = new StringBuilder();

        String responseLine = String.format("%s %s\r\n", version, status);

        responseBuilder.append(responseLine);

        for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
            String headerLine = String.format("%s: %s\r\n", headerEntry.getKey(), headerEntry.getValue());
            responseBuilder.append(headerLine);
        }

        responseBuilder.append("\r\n");

        int bodyLength = 0;

        if (body != null) {
            bodyLength = body.length;
        }

        byte[] response = responseBuilder.toString().getBytes();
        byte[] allByteArray = new byte[response.length + bodyLength];
        ByteBuffer buff = ByteBuffer.wrap(allByteArray);
        buff.put(response);

        if (body != null) {
            buff.put(body);
        }

        buff.flip();

        return buff;
    }
}
