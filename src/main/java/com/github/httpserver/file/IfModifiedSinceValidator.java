package com.github.httpserver.file;

import org.tinylog.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

/**
 * IfModifiedSinceValidator is a concrete file validator that evaluates the 'If-Modified-Since'
 * condition of an HTTP request, meaning that the file's last modified date has to be after
 * the specified date in the request header value.
 */
public class IfModifiedSinceValidator implements FileValidator {

    private final String criterion;

    /**
     * Constructs an If-Modified-Since validator with the given criterion.
     *
     * @param criterion the raw value of the 'If-Modified-Since' HTTP request header entry.
     */
    public IfModifiedSinceValidator(String criterion) {
        this.criterion = criterion;
    }

    /**
     * Evaluates the 'If-Modified-Since' condition on the file at the given path.
     *
     * @param filePath the path to the file on the local file system.
     * @return a boolean indicating if the condition is fulfilled.
     * @throws IOException if the given file cannot be read.
     */
    @Override
    public boolean isValidFile(Path filePath) throws IOException {

        FileTime fileTime;

        try {
            fileTime = Files.getLastModifiedTime(filePath);
        } catch (IOException e) {
            Logger.warn(e, "Failed to determine last modified time of file {} while validating " +
                    "If-Modified-Since header", filePath);
            throw new IOException(e);
        }


        ZonedDateTime zonedModificationDate = fileTime.toInstant().atZone(ZoneId.systemDefault());

        TemporalAccessor queryDate = DateTimeFormatter.RFC_1123_DATE_TIME.withLocale(Locale.US).parse(criterion);
        ZonedDateTime zonedQueryDate = ZonedDateTime.from(queryDate);

        return zonedModificationDate.isAfter(zonedQueryDate);
    }
}
