package com.saveapenny.imports.exception;

import java.util.UUID;

public class ImportAlreadyRunningException extends RuntimeException {

    public ImportAlreadyRunningException(UUID importId) {
        super("Import is already running: " + importId);
    }
}
