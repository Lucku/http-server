package com.github.httpserver.helper;

import com.github.httpserver.protocol.HttpHeader;
import com.github.httpserver.protocol.HttpResponse;
import com.github.httpserver.protocol.HttpStatus;
import com.github.httpserver.server.ServerConstants;
import org.tinylog.Logger;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * HttpResponseBuilder is a builder class for HTTP response models. It contains methods to set raw
 * values of the response as well as utility methods to calculate and set common header entries and
 * body contents.
 */
public class HttpResponseBuilder {

    private String version;
    private HttpStatus status;
    private Map<String, String> headers;
    private byte[] body;

    /**
     * Constructs a new HTTP response builder and sets the default values for
     * a 200 OK response with empty body and no header entries.
     */
    public HttpResponseBuilder() {
        this.version = ServerConstants.SUPPORTED_HTTP_VERSION;
        this.status = HttpStatus.HTTP_OK;
        this.headers = new HashMap<>();
        this.body = null;
    }

    /**
     * Sets the version string of the response in construction.
     *
     * @param version the HTTP version string.
     * @return the builder.
     */
    public HttpResponseBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    /**
     * Sets the HTTP response status code in construction.
     *
     * @param status the status code as enum constant.
     * @return the builder.
     */
    public HttpResponseBuilder setStatus(HttpStatus status) {
        this.status = status;
        return this;
    }

    /**
     * Sets all HTTP header entries of the response in construction.
     *
     * @param headers the complete map of header entries.
     * @return the builder.
     */
    public HttpResponseBuilder setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    /**
     * Appends a header entry to the entries that are already set inside the builder.
     *
     * @param key   the key of the header entry.
     * @param value the value of the header entry.
     * @return the builder.
     */
    public HttpResponseBuilder appendHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    /**
     * Sets the HTTP body of the response in construction.
     *
     * @param body the body as a byte array.
     * @return the builder.
     */
    public HttpResponseBuilder setBody(byte[] body) {
        this.body = body;
        return this;
    }

    /**
     * Appends the given text to the HTTP response body already set inside the builder.
     *
     * @param text the text to be appended to the response body.
     * @return the builder.
     */
    public HttpResponseBuilder appendTextBody(String text) {

        StringBuilder textBuilder = new StringBuilder();

        if (this.body != null) {
            textBuilder.append(new String(body));
        }

        textBuilder.append(text);
        textBuilder.append("\r\n");

        this.body = textBuilder.toString().getBytes();

        return this;
    }

    /**
     * Constructs an HTML expression from the given tag and text and appends it to the
     * response body already set inside the builder.
     *
     * @param tag  the HTML tag that the text is to be wrapped in.
     * @param text the text to be appended.
     * @return the builder.
     */
    public HttpResponseBuilder appendBodyAsHTML(String tag, String text) {
        String htmlText = String.format("<%1$s>%2$s</%1$s>", tag, text);
        return appendTextBody(htmlText);
    }

    /**
     * Calculates the content length from the body inside the builder and appends the
     * 'Content-Length' header with the calculated value to the header entries of the
     * response.
     *
     * @return the builder.
     */
    public HttpResponseBuilder appendContentLengthHeader() {

        int contentLength = 0;

        if (this.body != null) {
            contentLength = body.length;
        }

        headers.put(HttpHeader.HEADER_CONTENT_LENGTH, String.valueOf(contentLength));

        return this;
    }

    /**
     * Calculates the etag from the body inside the builder and appends the 'ETag'
     * header with the calculated value to the header entries of the response.
     *
     * @return the builder.
     */
    public HttpResponseBuilder appendETagHeader() {

        if (this.body != null) {

            try {
                String eTag = ServerConstants.calculateETag(body);
                headers.put(HttpHeader.HEADER_ETAG, eTag);
            } catch (NoSuchAlgorithmException e) {
                Logger.warn("Unable to calculate ETag due to unknown hashing algorithm");
            }
        } else {
            Logger.debug("No HTTP body set for response - not setting Etag header");
        }

        return this;
    }

    /**
     * Builds the HTTP response model from the information set inside the builder.
     *
     * @return the HTTP response constructed from the builder.
     */
    public HttpResponse build() {
        return new HttpResponse(version, status, headers, body);
    }
}
