package com.github.httpserver.protocol;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Objects;

/**
 * HttpResponse represents the model of an HTTP response.
 */
public class HttpResponse {

    private final String version;
    private final HttpStatus status;
    private final Map<String, String> headers;
    private final byte[] body;

    /**
     * Constructs an HTTP response model by taking all the relevant information needed to construct a response.
     *
     * @param version the HTTP version entry as defined in the request.
     * @param status  the HTTP response status. See {@link HttpStatus} for possible values.
     * @param headers a map of all response header entries.
     * @param body    the response body as byte array.
     * @throws NullPointerException if either HTTP version, status or headers are null values.
     */
    public HttpResponse(String version, HttpStatus status, Map<String, String> headers, byte[] body) {
        this.version = Objects.requireNonNull(version);
        this.status = Objects.requireNonNull(status);
        this.headers = Objects.requireNonNull(headers);
        this.body = body;
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
     * Returns the HTTP response status.
     *
     * @return the response status as enum constant containing status code and reason phrase.
     */
    public HttpStatus getStatus() {
        return status;
    }

    /**
     * Returns the map of header entries, organized as headerKey -> headerValue.
     *
     * @return the complete map of HTTP response headers.
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Assembles the whole response model into a {@link ByteBuffer} representation that can be utilized as a
     * transportation format towards a {@link java.nio.channels.SocketChannel} of a TCP client.
     *
     * @return a byte buffer containing the correctly formatted HTTP response content.
     */
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
