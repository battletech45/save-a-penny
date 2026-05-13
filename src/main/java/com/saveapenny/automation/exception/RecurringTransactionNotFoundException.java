package com.saveapenny.automation.exception;

import java.util.UUID;

public class RecurringTransactionNotFoundException extends RuntimeException {

    public RecurringTransactionNotFoundException(UUID recurringTransactionId) {
        super("Recurring transaction not found: " + recurringTransactionId);
    }
}
