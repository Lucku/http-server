package com.github.httpserver.helper;

import com.github.httpserver.exception.BadRequestException;
import com.github.httpserver.exception.HttpException;
import com.github.httpserver.protocol.HttpMethod;
import com.github.httpserver.protocol.HttpRequest;

import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

public class HttpRequestParser {

    public HttpRequest parseRequest(byte[] rawRequest) throws HttpException {

        Scanner scanner = new Scanner(new ByteArrayInputStream(rawRequest));

        RequestLine requestLine;
        Map<String, String> headers = new HashMap<>();

        try {
            String line = scanner.nextLine();
            requestLine = parseRequestLine(line);

            while ((line = scanner.nextLine()).length() > 0) {
                HeaderEntry headerEntry = parseHeaderEntry(line);
                headers.put(headerEntry.getKey(), headerEntry.getValue());
            }
        } catch (ParseException e) {
            throw new BadRequestException(e.getMessage(), e);
        }

        return new HttpRequest(requestLine.getMethod(), requestLine.getVersion(),
                requestLine.getPath(), headers);
    }

    private RequestLine parseRequestLine(String line) throws ParseException {
        StringTokenizer tokenizer = new StringTokenizer(line, " ");
        String methodString = tokenizer.nextToken();
        String path = tokenizer.nextToken();
        String version = tokenizer.nextToken();

        if (tokenizer.hasMoreTokens()) {
            throw new ParseException("Request line is longer than expected", 0);
        }

        // strip query params from path
        int index;
        if ((index = path.indexOf("?")) != -1) {
            path = path.substring(0, index);
        }

        HttpMethod method;

        try {
            method = HttpMethod.valueOf(methodString);
        } catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), 0);
        }

        return new RequestLine(method, path, version);
    }

    private HeaderEntry parseHeaderEntry(String line) throws ParseException {
        String[] headerEntry = line.split(":", 2);

        if (headerEntry.length < 2) {
            throw new ParseException(String.format("Invalid header entry format %s (expected Key: Value)", line), 0);
        }

        return new HeaderEntry(headerEntry[0], headerEntry[1].trim());
    }

    private static class RequestLine {
        HttpMethod method;
        String path;
        String version;

        public RequestLine(HttpMethod method, String path, String version) {
            this.method = method;
            this.path = path;
            this.version = version;
        }

        public HttpMethod getMethod() {
            return method;
        }

        public String getPath() {
            return path;
        }

        public String getVersion() {
            return version;
        }
    }

    private static class HeaderEntry {
        String key;
        String value;

        public HeaderEntry(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }
}
