package com.github.httpserver.protocol;

/**
 * HttpStatus contains constants for relevant HTTP response status codes and their reason phrases.
 *
 * @see <a href=https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html>Information about
 * status code definitions</a>
 */
public enum HttpStatus {

    /**
     * Constant for HTTP 200 OK.
     */
    HTTP_OK(200, "OK"),
    /**
     * Constant for HTTP 304 Not Modified.
     */
    HTTP_NOT_MODIFIED(304, "Not Modified"),
    /**
     * Constant for HTTP 400 Bad Request.
     */
    HTTP_BAD_REQUEST(400, "Bad Request"),
    /**
     * Constant for HTTP 404 Not Found.
     */
    HTTP_NOT_FOUND(404, "Not Found"),
    /**
     * Constant for HTTP 405 Method Not Allowed.
     */
    HTTP_METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    /**
     * Constant for HTTP 412 Precondition Failed.
     */
    HTTP_PRECONDITION_FAILED(412, "Precondition Failed"),
    /**
     * Constant for HTTP 500 Internal Server Error.
     */
    HTTP_INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int statusCode;

    private final String reasonPhrase;

    /**
     * Constructs an HTTP status by passing its status code and reason phrase.
     *
     * @param statusCode   the 3-digit status code of the HTTP response status.
     * @param reasonPhrase the reason phrase of the HTTP response status.
     */
    HttpStatus(int statusCode, String reasonPhrase) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    /**
     * Returns the 3-digit status code of the HTTP response status.
     *
     * @return the status code as integer.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Returns the reason phrase of the HTTP response status.
     *
     * @return the reason phrase as string.
     */
    public String getReasonPhrase() {
        return reasonPhrase;
    }

    /**
     * Provides a string representation of the HTTP status code constant that is composed
     * in such a way that is resembles the format inside the HTTP response.
     *
     * @return string containing status code and its reason phrase.
     */
    @Override
    public String toString() {
        return String.format("%d %s", statusCode, reasonPhrase);
    }
}
