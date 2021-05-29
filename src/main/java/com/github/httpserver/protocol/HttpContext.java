package com.github.httpserver.protocol;

public class HttpContext {

    public static final HttpContext EMPTY_CONTEXT = new HttpContext();
    private HttpRequest request;
    private HttpResponse response;

    public HttpContext(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;
    }

    private HttpContext() {
    }

    public HttpRequest getRequest() {
        return request;
    }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    public HttpResponse getResponse() {
        return response;
    }

    public void setResponse(HttpResponse response) {
        this.response = response;
    }

    public boolean isPersistentConnection() {

        if (request == null) {
            return false;
        }

        if (request.getHeaders().containsKey(HttpHeader.HEADER_CONNECTION)) {
            String connectionParam = request.getHeaders().get(HttpHeader.HEADER_CONNECTION);
            return connectionParam.equals(HttpHeader.CONNECTION_KEEP_ALIVE);
        }

        return false;
    }
}
