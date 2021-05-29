package com.github.httpserver.file;

import com.github.httpserver.helper.HttpUtils;
import com.github.httpserver.protocol.HttpHeader;
import com.github.httpserver.protocol.HttpRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Map;

public class HttpFileInfoRetriever implements FileInfoRetriever {

    private FileValidator preconditionValidator;
    private FileValidator modificationValidator;

    public HttpFileInfoRetriever(HttpRequest request) {

        Map<String, String> requestHeaders = request.getHeaders();

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
        String formattedTimestamp = HttpUtils.fileTimeToUTCDateString(lastModified);

        return new FileInfo(isValid, isModified, contentType, formattedTimestamp, filePath);
    }
}
