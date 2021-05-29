package com.github.httpserver.helper;

import com.github.httpserver.protocol.HttpHeader;
import com.github.httpserver.protocol.HttpResponse;
import com.github.httpserver.protocol.HttpStatus;
import org.tinylog.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class HttpResponseBuilder {

    public static final String HTTP_VERSION = "HTTP/1.1";

    private String version;
    private HttpStatus status;
    private Map<String, String> headers;
    private String body;

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

    public HttpResponseBuilder appendBody(String body) {

        if (body == null) {
            return this;
        }

        if (this.body == null) {
            this.body = body + "\r\n";
        } else {
            this.body += body;
        }

        return this;
    }

    public HttpResponseBuilder appendBodyAsHTML(String tag, String text) {
        String htmlText = String.format("<%1$s>%2$s</%1$s>", tag, text);
        return appendBody(htmlText);
    }

    public HttpResponseBuilder appendContentLengthHeader() {
        if (this.body != null) {
            headers.put(HttpHeader.HEADER_CONTENT_LENGTH, String.valueOf(body.length()));
        } else {
            Logger.debug("No HTTP body set for response - not setting Content-Length header");
        }

        return this;
    }

    public HttpResponseBuilder appendETagHeader() {
        if (this.body != null) {

            MessageDigest messageDigest = null;
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
