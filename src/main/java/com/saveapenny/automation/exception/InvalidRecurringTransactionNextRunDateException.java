package com.saveapenny.automation.exception;

import java.time.LocalDate;

public class InvalidRecurringTransactionNextRunDateException extends RuntimeException {

    public InvalidRecurringTransactionNextRunDateException(LocalDate nextRunDate) {
        super("Invalid recurring transaction next run date: " + nextRunDate);
    }
}
