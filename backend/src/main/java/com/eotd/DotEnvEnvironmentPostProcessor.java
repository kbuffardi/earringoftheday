package com.eotd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DotEnvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String DOT_ENV_PROPERTY_SOURCE_NAME = "dotEnvFile";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                       SpringApplication application) {
        Path dotEnvPath = Paths.get(System.getProperty("user.dir"), ".env");
        if (!Files.exists(dotEnvPath)) {
            return;
        }

        Map<String, Object> properties = new LinkedHashMap<>();
        List<String> loadedNames = new ArrayList<>();

        try {
            for (String line : Files.readAllLines(dotEnvPath)) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                int equalsIndex = line.indexOf('=');
                if (equalsIndex < 0) {
                    continue;
                }
                String key = line.substring(0, equalsIndex).trim();
                String value = line.substring(equalsIndex + 1);
                if (!key.isEmpty()) {
                    properties.put(key, value);
                    loadedNames.add(key);
                }
            }
        } catch (IOException e) {
            System.err.println("[DotEnv] Failed to read .env file: " + e.getMessage());
            return;
        }

        if (!properties.isEmpty()) {
            environment.getPropertySources().addLast(
                    new MapPropertySource(DOT_ENV_PROPERTY_SOURCE_NAME, properties));
        }

        boolean isProduction = false;
        for (String profile : environment.getActiveProfiles()) {
            if ("prod".equalsIgnoreCase(profile) || "production".equalsIgnoreCase(profile)) {
                isProduction = true;
                break;
            }
        }

        if (!isProduction && !loadedNames.isEmpty()) {
            System.out.println("[DotEnv] Loaded environment variables from .env: " + loadedNames);
        }
    }
}
