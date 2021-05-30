package com.github.httpserver.file;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class IfModifiedSinceValidatorTest {

    private final Path resourcePath = Path.of("src/test/resources");

    @Test
    void shouldThrowExceptionIfFileNotPresent() {
        IfModifiedSinceValidator validator = new IfModifiedSinceValidator("");
        Path testFile = resourcePath.resolve("fileX.html");
        assertThrows(IOException.class, () -> validator.isValidFile(testFile));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Thu, 21 Oct 2010 07:28:00 GMT",
            "Fri, 12 Apr 1996 23:45:00 GMT",
            "Sun, 23 May 2021 20:00:00 GMT"
    })
    void shouldMatchIfLastModifiedDateIsAfterCriterion(String criterion) {

        IfModifiedSinceValidator validator = new IfModifiedSinceValidator(criterion);
        Path testFile = resourcePath.resolve("file1.html");

        try {
            assertTrue(validator.isValidFile(testFile));
        } catch (IOException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Fri, 21 Jun 2030 15:10:00 GMT",
            "Mon, 06 Jan 2048 10:00:00 GMT",
    })
    void shouldNotMatchIfLastModifiedDateIsBeforeCriterion(String criterion) {

        IfModifiedSinceValidator validator = new IfModifiedSinceValidator(criterion);
        Path testFile = resourcePath.resolve("file1.html");

        try {
            assertFalse(validator.isValidFile(testFile));
        } catch (IOException e) {
            fail(e);
        }
    }
}