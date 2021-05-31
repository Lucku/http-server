package com.github.httpserver.file;

import com.github.httpserver.protocol.HttpHeader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * HTTPFileInfoRetriever retrieves information about files from a HTTP view point, meaning
 * that validation conditions are applied based on conditional request headers.
 *
 * @see <a href=https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html>Information about
 * conditional headers</a>
 */
public class HttpFileInfoRetriever implements FileInfoRetriever {

    private FileValidator preconditionValidator;
    private FileValidator modificationValidator;

    /**
     * Constructs an HTTP file info retriever by taking the header entries of an HTTP request.
     * Validation conditions are created based on supported conditional request headers. These
     * are 'If-Match', 'If-None-Match' and 'If-Modified-Since'. If 'If-None-Match' is defined
     * in the header entries, an also present 'If-Modified-Since' condition is ignored.
     *
     * @param requestHeaders the complete list of HTTP request headers to be taken into account
     *                       when creating the file information.
     */
    public HttpFileInfoRetriever(Map<String, String> requestHeaders) {

        Objects.requireNonNull(requestHeaders);

        if (requestHeaders.containsKey(HttpHeader.HEADER_IF_MATCH)) {
            this.preconditionValidator = new IfMatchValidator(HttpHeader.HEADER_IF_MATCH);
        }

        if (requestHeaders.containsKey(HttpHeader.HEADER_IF_NONE_MATCH)) {
            this.modificationValidator = new IfNoneMatchValidator(
                    requestHeaders.get(HttpHeader.HEADER_IF_NONE_MATCH));
        } else if (requestHeaders.containsKey(HttpHeader.HEADER_IF_MODIFIED_SINCE)) {
            this.modificationValidator = new IfModifiedSinceValidator(
                    requestHeaders.get(HttpHeader.HEADER_IF_MODIFIED_SINCE));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileInfo retrieveFileInfo(Path filePath) throws IOException {

        boolean isValid = true;
        boolean isModified = true;

        if (preconditionValidator != null) {
            isValid = preconditionValidator.isValidFile(filePath);
        }

        if (modificationValidator != null) {
            isModified = modificationValidator.isValidFile(filePath);
        }

        String contentType = Files.probeContentType(filePath);

        FileTime lastModified = Files.getLastModifiedTime(filePath);

        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneOffset.UTC)
                .withLocale(Locale.US);
        String formattedTimestamp = formatter.format(lastModified.toInstant());

        return new FileInfo(isValid, isModified, contentType, formattedTimestamp, filePath);
    }
}
