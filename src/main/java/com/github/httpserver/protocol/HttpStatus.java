package com.github.httpserver.protocol;

public enum HttpStatus {

    HTTP_OK(200, "OK"),
    HTTP_NOT_MODIFIED(304, "Not Modified"),
    HTTP_BAD_REQUEST(400, "Bad Request"),
    HTTP_NOT_FOUND(404, "Not Found"),
    HTTP_METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    HTTP_PRECONDITION_FAILED(412, "Precondition Failed"),
    HTTP_INTERNAL_SERVER_ERROR(500, "Internal Server Error");
    // TODO Add the rest

    private final int statusCode;
    private final String label;

    HttpStatus(int statusCode, String label) {
        this.statusCode = statusCode;
        this.label = label;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return String.format("%d %s", statusCode, label);
    }
}
