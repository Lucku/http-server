package com.github.httpserver.protocol;

/**
 * Constants for names of HTTP request methods.
 *
 * @see <a href=https://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html>Information about
 * request methods</a>
 */
public enum HttpMethod {
    /**
     * Constant for the name of the HTTP GET method
     */
    GET,
    /**
     * Constant for the name of the HTTP HEAD method
     */
    HEAD,
    /**
     * Constant for the name of the HTTP POST method
     */
    POST,
    /**
     * Constant for the name of the HTTP PUT method
     */
    PUT,
    /**
     * Constant for the name of the HTTP DELETE method
     */
    DELETE,
    /**
     * Constant for the name of the HTTP CONNECT method
     */
    CONNECT,
    /**
     * Constant for the name of the HTTP OPTIONS method
     */
    OPTIONS,
    /**
     * Constant for the name of the HTTP TRACE method
     */
    TRACE,
    /**
     * Constant for the name of the HTTP PATCH method
     */
    PATCH;
}
