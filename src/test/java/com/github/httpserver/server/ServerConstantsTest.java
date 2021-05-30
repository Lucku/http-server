package com.github.httpserver.server;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class ServerConstantsTest {

    @Test
    void shouldCalculateSameHashForSameFile() throws IOException {

        Path testFile = Path.of("src/test/resources", "file1.html");
        byte[] fileBytes1 = Files.readAllBytes(testFile);

        byte[] fileBytes2 = "<h1>This is a test file</h1>".getBytes();

        String hashTestFile1 = null;
        String hashTestFile2 = null;

        try {
            hashTestFile1 = ServerConstants.calculateETag(fileBytes1);
            hashTestFile2 = ServerConstants.calculateETag(fileBytes2);
        } catch (NoSuchAlgorithmException e) {
            fail(e);
        }

        assertEquals(hashTestFile2, hashTestFile1);
    }
}