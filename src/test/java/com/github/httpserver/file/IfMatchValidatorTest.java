package com.github.httpserver.file;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class IfMatchValidatorTest {

    private final static String MD5_HASH_FILE1 = "0AC8992824ABEB4A7062AE4A99FA0905".toLowerCase();
    private final Path resourcePath = Path.of("src/test/resources");

    private static Stream<Arguments> provideValidCriteria() {
        return Stream.of(
                Arguments.of("\"" + MD5_HASH_FILE1 + "\""),
                Arguments.of("\"" + MD5_HASH_FILE1 + "\"" + ", \"b\", \"c\""),
                Arguments.of("\"a\", \"b\", \"c\", \"d\", " + MD5_HASH_FILE1)
        );
    }

    @Test
    void shouldMatchIfAsteriskCriteria() {
        String criteria = "*";
        IfMatchValidator validator = new IfMatchValidator(criteria);

        Path testFile = resourcePath.resolve("file1.html");

        try {
            assertTrue(validator.isValidFile(testFile));
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void shouldThrowExceptionIfFileNotPresent() {
        IfMatchValidator validator = new IfMatchValidator("");
        Path testFile = resourcePath.resolve("fileX.html");
        assertThrows(IOException.class, () -> validator.isValidFile(testFile));
    }

    @ParameterizedTest
    @MethodSource("provideValidCriteria")
    void shouldMatchIfETagInCriteria(String criterion) {

        IfMatchValidator validator = new IfMatchValidator(criterion);
        Path testFile = resourcePath.resolve("file1.html");

        try {
            assertTrue(validator.isValidFile(testFile));
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void shouldNotMatchIfEtagNotInCritera() {
        String criteria = "\"a\", \"b\", \"c\", \"d\", \"e\"";
        IfMatchValidator validator = new IfMatchValidator(criteria);

        Path testFile = resourcePath.resolve("file1.html");

        try {
            assertFalse(validator.isValidFile(testFile));
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void shouldNotMatchIfWeakETagInCriteria() {
        String criteria = "W/" + "\"" + MD5_HASH_FILE1 + "\"";
        IfMatchValidator validator = new IfMatchValidator(criteria);

        Path testFile = resourcePath.resolve("file1.html");

        try {
            assertFalse(validator.isValidFile(testFile));
        } catch (IOException e) {
            fail(e);
        }
    }
}