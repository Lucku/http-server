package com.github.httpserver.helper;

import com.github.httpserver.protocol.HttpHeader;
import com.github.httpserver.protocol.HttpResponse;
import com.github.httpserver.protocol.HttpStatus;
import org.tinylog.Logger;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class HttpResponseBuilder {

    public static final String HTTP_VERSION = "HTTP/1.1";

    private String version;
    private HttpStatus status;
    private Map<String, String> headers;
    private byte[] body;

    public HttpResponseBuilder() {
        this.version = HTTP_VERSION;
        this.status = HttpStatus.HTTP_OK;
        this.headers = new HashMap<>();
        this.body = null;
    }

    public HttpResponseBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    public HttpResponseBuilder setStatus(HttpStatus status) {
        this.status = status;
        return this;
    }

    public HttpResponseBuilder setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public HttpResponseBuilder appendHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public HttpResponseBuilder setBody(byte[] body) {
        this.body = body;
        return this;
    }

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

    public HttpResponseBuilder appendBodyAsHTML(String tag, String text) {
        String htmlText = String.format("<%1$s>%2$s</%1$s>", tag, text);
        return appendTextBody(htmlText);
    }

    public HttpResponseBuilder appendContentLengthHeader() {

        int contentLength = 0;

        if (this.body != null) {
            contentLength = body.length;
        }

        headers.put(HttpHeader.HEADER_CONTENT_LENGTH, String.valueOf(contentLength));

        return this;
    }

    public HttpResponseBuilder appendETagHeader() {

        if (this.body != null) {

            try {
                String eTag = HttpUtils.calculateETag(body);
                headers.put(HttpHeader.HEADER_ETAG, eTag);
            } catch (NoSuchAlgorithmException e) {
                Logger.warn("Unable to calculate ETag due to unknown hashing algorithm");
            }
        } else {
            Logger.debug("No HTTP body set for response - not setting Etag header");
        }

        return this;
    }

    public HttpResponse build() {
        return new HttpResponse(version, status, headers, body);
    }
}
