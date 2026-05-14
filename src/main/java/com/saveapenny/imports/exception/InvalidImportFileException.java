package com.saveapenny.imports.exception;

public class InvalidImportFileException extends RuntimeException {

    public InvalidImportFileException(String reason) {
        super("Invalid import file: " + reason);
    }
}
