package com.github.httpserver.protocol;

/**
 * HttpHeader contains constants for relevant HTTP request and response header keys
 * as well as their concrete values in relevant cases where they are fixed.
 */
public final class HttpHeader {

    /**
     * Constant for the 'ETag' header key.
     */
    public static final String HEADER_ETAG = "ETag";
    /**
     * Constant for the 'Content-Length' header key.
     */
    public static final String HEADER_CONTENT_LENGTH = "Content-Length";
    /**
     * Constant for the 'Content-Type' header key.
     */
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    /**
     * Constant for the 'Last-Modified' header key.
     */
    public static final String HEADER_LAST_MODIFIED = "Last-Modified";
    /**
     * Constant for the 'If-Modified-Since' header key.
     */
    public static final String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";
    /**
     * Constant for the 'If-Match' header key.
     */
    public static final String HEADER_IF_MATCH = "If-Match";
    /**
     * Constant for the 'If-None-Match' header key.
     */
    public static final String HEADER_IF_NONE_MATCH = "If-None-Match";
    /**
     * Constant for the 'Connection' header key.
     */
    public static final String HEADER_CONNECTION = "Connection";

    /**
     * Constant for the value 'keep-alive' of the 'Connection' header key.
     */
    public static final String CONNECTION_KEEP_ALIVE = "keep-alive";
    /**
     * Constant for the value 'close' of the 'Connection' header key.
     */
    public static final String CONNECTION_CLOSE = "close";

    private HttpHeader() {
    }
}
