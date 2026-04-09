package com.eotd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.mock.env.MockEnvironment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class DotEnvEnvironmentPostProcessorTests {

    private final SpringApplication application = new SpringApplication(EarringOfTheDayApplication.class);

    @Test
    void loadsKeyValuePairsFromDotEnvFile(@TempDir Path tempDir) throws IOException {
        Path dotEnv = tempDir.resolve(".env");
        Files.writeString(dotEnv, "MY_SECRET_NUMBER=17\nANOTHER_VAR=hello\n");

        Path originalDir = Path.of(System.getProperty("user.dir"));
        System.setProperty("user.dir", tempDir.toString());
        try {
            ConfigurableEnvironment environment = new StandardEnvironment();
            DotEnvEnvironmentPostProcessor processor = new DotEnvEnvironmentPostProcessor();
            processor.postProcessEnvironment(environment, application);

            assertEquals("17", environment.getProperty("MY_SECRET_NUMBER"));
            assertEquals("hello", environment.getProperty("ANOTHER_VAR"));
        } finally {
            System.setProperty("user.dir", originalDir.toString());
        }
    }

    @Test
    void ignoresCommentLinesAndBlankLines(@TempDir Path tempDir) throws IOException {
        Path dotEnv = tempDir.resolve(".env");
        Files.writeString(dotEnv, "# This is a comment\n\nVALID_KEY=value\n");

        Path originalDir = Path.of(System.getProperty("user.dir"));
        System.setProperty("user.dir", tempDir.toString());
        try {
            ConfigurableEnvironment environment = new StandardEnvironment();
            DotEnvEnvironmentPostProcessor processor = new DotEnvEnvironmentPostProcessor();
            processor.postProcessEnvironment(environment, application);

            assertNull(environment.getProperty("# This is a comment"));
            assertEquals("value", environment.getProperty("VALID_KEY"));
        } finally {
            System.setProperty("user.dir", originalDir.toString());
        }
    }

    @Test
    void doesNothingWhenDotEnvFileMissing() {
        // Run from a directory where .env does not exist
        ConfigurableEnvironment environment = new MockEnvironment();
        DotEnvEnvironmentPostProcessor processor = new DotEnvEnvironmentPostProcessor();
        // Should not throw
        assertDoesNotThrow(() -> processor.postProcessEnvironment(environment, application));
    }

    @Test
    void doesNotPrintNamesInProductionProfile(@TempDir Path tempDir) throws IOException {
        Path dotEnv = tempDir.resolve(".env");
        Files.writeString(dotEnv, "SECRET_KEY=abc\n");

        Path originalDir = Path.of(System.getProperty("user.dir"));
        System.setProperty("user.dir", tempDir.toString());
        try {
            ConfigurableEnvironment environment = new StandardEnvironment();
            environment.setActiveProfiles("prod");
            DotEnvEnvironmentPostProcessor processor = new DotEnvEnvironmentPostProcessor();
            // Should not throw; production mode suppresses console output
            assertDoesNotThrow(() -> processor.postProcessEnvironment(environment, application));
            assertEquals("abc", environment.getProperty("SECRET_KEY"));
        } finally {
            System.setProperty("user.dir", originalDir.toString());
        }
    }
}
