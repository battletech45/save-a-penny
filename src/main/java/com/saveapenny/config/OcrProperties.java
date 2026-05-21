package com.saveapenny.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ocr")
public record OcrProperties(
        boolean enabled,
        String tessdataPath,
        String language,
        int psm,
        long maxFileSizeBytes,
        long jobTimeoutMillis,
        int maxRetries,
        boolean debugLogging) {
}
