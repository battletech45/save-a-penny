package com.saveapenny.automation.exception;

public class RecurringTransactionDependencyNotFoundException extends RuntimeException {

    public RecurringTransactionDependencyNotFoundException(String dependency, Object id) {
        super("Recurring transaction dependency not found: " + dependency + "=" + id);
    }
}
