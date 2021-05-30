package com.github.httpserver.file;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class IfNoneMatchValidatorTest {

    private final static String MD5_HASH_FILE1 = "0AC8992824ABEB4A7062AE4A99FA0905".toLowerCase();
    private final Path resourcePath = Path.of("src/test/resources");

    private static Stream<Arguments> provideInvalidCriteria() {
        return Stream.of(
                Arguments.of("\"" + MD5_HASH_FILE1 + "\""),
                Arguments.of("W/\"" + MD5_HASH_FILE1 + "\""),
                Arguments.of("\"" + MD5_HASH_FILE1 + "\"" + ", \"b\", \"c\""),
                Arguments.of("\"a\", \"b\", \"c\", \"d\", " + MD5_HASH_FILE1)
        );
    }

    @Test
    void shouldNotMatchIfAsteriskCriterion() {
        String criterion = "*";
        IfNoneMatchValidator validator = new IfNoneMatchValidator(criterion);

        Path testFile = resourcePath.resolve("file1.html");

        try {
            assertFalse(validator.isValidFile(testFile));
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void shouldThrowExceptionIfFileNotPresent() {
        IfNoneMatchValidator validator = new IfNoneMatchValidator("");
        Path testFile = resourcePath.resolve("fileX.html");
        assertThrows(IOException.class, () -> validator.isValidFile(testFile));
    }

    @Test
    void shouldMatchIfEtagNotInCriterion() {
        String criterion = "\"a\", \"b\", \"c\", \"d\", \"e\"";
        IfNoneMatchValidator validator = new IfNoneMatchValidator(criterion);

        Path testFile = resourcePath.resolve("file1.html");

        try {
            assertTrue(validator.isValidFile(testFile));
        } catch (IOException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCriteria")
    void shouldNotMatchIfETagInCriterion(String criterion) {

        IfNoneMatchValidator validator = new IfNoneMatchValidator(criterion);
        Path testFile = resourcePath.resolve("file1.html");

        try {
            assertFalse(validator.isValidFile(testFile));
        } catch (IOException e) {
            fail(e);
        }
    }
}