package com.github.httpserver.protocol;

/**
 * HttpContext is a utility wrapper around a HTTP request and response pair and serves
 * as a model to hold the context of a specific HTTP exchange. It also provides the utility
 * function {@link #isTransientConnection()} to obtain information about requested connection
 * parameters.
 */
public class HttpContext {

    /**
     * An empty context that is useful if there was still no read or write interaction between
     * client and server.
     */
    public static final HttpContext EMPTY_CONTEXT = new HttpContext();

    private HttpRequest request;
    private HttpResponse response;

    /**
     * Constructs a new HTTP context by taking a pair of HTTP request and response model.
     * Both values can be set as null.
     *
     * @param request  the HTTP request model.
     * @param response the HTTP response model.
     */
    public HttpContext(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;
    }

    private HttpContext() {
    }

    /**
     * Returns the HTTP request model.
     *
     * @return the request model.
     */
    public HttpRequest getRequest() {
        return request;
    }

    /**
     * Sets the HTTP request model. It is allowed to set a null value.
     *
     * @param request the request model.
     */
    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    /**
     * Returns the HTTP response model.
     *
     * @return the response model.
     */
    public HttpResponse getResponse() {
        return response;
    }

    /**
     * Sets the HTTP response model. It is allowed to set a null value.
     *
     * @param response the response model.
     */
    public void setResponse(HttpResponse response) {
        this.response = response;
    }

    /**
     * Determines, based on the request headers, if client requested the current HTTP exchange to be
     * closed. If this function returns false, the connection can be assumed to be persistent.
     *
     * @return a boolean indicating if a connection close was requested by the client.
     * @see <a href=https://www.w3.org/Protocols/rfc2616/rfc2616-sec8.html>Information about
     * status code definitions</a>
     */
    public boolean isTransientConnection() {

        if (request == null) {
            return false;
        }

        if (request.getHeaders().containsKey(HttpHeader.HEADER_CONNECTION)) {
            String connectionParam = request.getHeaders().get(HttpHeader.HEADER_CONNECTION);
            return connectionParam.equals(HttpHeader.CONNECTION_CLOSE);
        }

        return false;
    }
}
