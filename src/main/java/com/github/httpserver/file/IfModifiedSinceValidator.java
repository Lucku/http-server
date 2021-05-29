package com.github.httpserver.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
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

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
            Date queryDate = dateFormat.parse(criteria);
            ZonedDateTime zonedQueryDate = queryDate.toInstant().atZone(ZoneOffset.UTC);

            return zonedModificationDate.isAfter(zonedQueryDate);
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }
}
