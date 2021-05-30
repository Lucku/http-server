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

public class IfModifiedSinceValidator implements FileValidator {

    private final String criterion;

    public IfModifiedSinceValidator(String criteria) {
        this.criterion = criteria;
    }

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
