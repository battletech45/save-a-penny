package com.saveapenny.imports.exception;

import java.util.UUID;

public class ImportNotFoundException extends RuntimeException {

    public ImportNotFoundException(UUID importId) {
        super("Import not found: " + importId);
    }
}
