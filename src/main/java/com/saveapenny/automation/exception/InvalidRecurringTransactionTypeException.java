package com.saveapenny.automation.exception;

import com.saveapenny.transaction.entity.TransactionType;

public class InvalidRecurringTransactionTypeException extends RuntimeException {

    public InvalidRecurringTransactionTypeException(TransactionType type) {
        super("Invalid recurring transaction type: " + type);
    }
}
