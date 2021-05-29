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

    private final String criteria;

    public IfModifiedSinceValidator(String criteria) {
        this.criteria = criteria;
    }

    @Override
    public boolean isValidFile(Path filePath) throws IOException {

        FileTime fileTime = Files.getLastModifiedTime(filePath);

        ZonedDateTime zonedModificationDate = fileTime.toInstant().atZone(ZoneId.systemDefault());

        TemporalAccessor queryDate = DateTimeFormatter.RFC_1123_DATE_TIME.withLocale(Locale.US).parse(criteria);
        ZonedDateTime zonedQueryDate = ZonedDateTime.from(queryDate);

        Logger.warn(zonedQueryDate.getZone());

        return zonedModificationDate.isAfter(zonedQueryDate);
    }
}
