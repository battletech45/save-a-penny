package com.saveapenny.imports.exception;

import java.util.UUID;

public class OcrJobNotFoundException extends RuntimeException {

    public OcrJobNotFoundException(UUID jobId) {
        super("OCR job not found for id: " + jobId);
    }
}
