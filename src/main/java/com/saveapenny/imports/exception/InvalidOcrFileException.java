package com.saveapenny.imports.exception;

public class InvalidOcrFileException extends RuntimeException {

    public InvalidOcrFileException(String reason) {
        super("Invalid OCR file: " + reason);
    }
}
