package com.github.httpserver.protocol;

import com.github.httpserver.exception.BadRequestException;
import com.github.httpserver.exception.HttpException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

public class HttpRequestParser {

    public HttpRequestContext parseRequest(byte[] rawRequest) throws HttpException {

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
        } catch (IOException e) {
            // TODO: Proper exception handling
            throw new BadRequestException("Bad Request", e);
        }

        return new HttpRequestContext(requestLine.getMethod(), requestLine.getVersion(),
                requestLine.getPath(), headers, null);
    }

    private RequestLine parseRequestLine(String line) throws IOException {
        StringTokenizer tokenizer = new StringTokenizer(line, " ");
        String method = tokenizer.nextToken();
        String path = tokenizer.nextToken();
        String version = tokenizer.nextToken();

        if (tokenizer.hasMoreTokens()) {
            throw new IOException("Invalid request line format");
        }

        return new RequestLine(method, path, version);
    }

    private HeaderEntry parseHeaderEntry(String line) throws IOException {
        String[] headerEntry = line.split(":");

        if (headerEntry.length < 2) {
            throw new IOException("Invalid header entry format");
        }

        return new HeaderEntry(headerEntry[0], headerEntry[1].trim());
    }

    private static class RequestLine {
        String method;
        String path;
        String version;

        public RequestLine(String method, String path, String version) {
            this.method = method;
            this.path = path;
            this.version = version;
        }

        public String getMethod() {
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
