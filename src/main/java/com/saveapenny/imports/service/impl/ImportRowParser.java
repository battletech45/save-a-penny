package com.saveapenny.imports.service.impl;

import com.saveapenny.transaction.dto.CreateTransactionRequest;
import com.saveapenny.transaction.entity.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ImportRowParser {

    private static final int MIN_TRANSACTION_COLUMNS = 6;
    private static final int TYPE_INDEX = 0;
    private static final int DATE_INDEX = 1;
    private static final int AMOUNT_INDEX = 2;
    private static final int CURRENCY_INDEX = 3;
    private static final int ACCOUNT_ID_INDEX = 4;
    private static final int CATEGORY_ID_INDEX = 5;
    private static final int DESCRIPTION_INDEX = 6;
    private static final int MAX_DESCRIPTION_LENGTH = 500;

    public CreateTransactionRequest parse(String rawData) {
        String[] parts = rawData == null ? new String[0] : rawData.split(",", -1);
        if (parts.length < MIN_TRANSACTION_COLUMNS) {
            throw new IllegalArgumentException("Expected at least " + MIN_TRANSACTION_COLUMNS + " columns");
        }

        TransactionType type = parseType(parts[TYPE_INDEX]);
        LocalDate transactionDate = parseDate(parts[DATE_INDEX]);
        BigDecimal amount = parseAmount(parts[AMOUNT_INDEX]);
        String currency = parseCurrency(parts[CURRENCY_INDEX]);
        UUID accountId = parseUuid(parts[ACCOUNT_ID_INDEX], "Account ID");
        UUID categoryId = parseUuid(parts[CATEGORY_ID_INDEX], "Category ID");
        String description = parseDescription(parts.length > DESCRIPTION_INDEX ? parts[DESCRIPTION_INDEX] : null);

        return CreateTransactionRequest.builder()
                .accountId(accountId)
                .categoryId(categoryId)
                .type(type)
                .amount(amount)
                .currency(currency)
                .description(description)
                .transactionDate(transactionDate)
                .build();
    }

    public String validate(String rawData) {
        try {
            parse(rawData);
            return null;
        } catch (IllegalArgumentException ex) {
            return ex.getMessage();
        }
    }

    private TransactionType parseType(String value) {
        String normalized = value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Transaction type is required");
        }

        try {
            TransactionType type = TransactionType.valueOf(normalized);
            if (type == TransactionType.TRANSFER) {
                throw new IllegalArgumentException("TRANSFER rows are not supported in transaction imports");
            }
            return type;
        } catch (IllegalArgumentException ex) {
            if ("TRANSFER rows are not supported in transaction imports".equals(ex.getMessage())) {
                throw ex;
            }
            throw new IllegalArgumentException("Transaction type is invalid");
        }
    }

    private LocalDate parseDate(String value) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Transaction date is required");
        }

        try {
            return LocalDate.parse(normalized);
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Transaction date is invalid");
        }
    }

    private BigDecimal parseAmount(String value) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Amount is required");
        }

        try {
            BigDecimal amount = new BigDecimal(normalized);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Amount must be greater than 0");
            }
            return amount;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Amount is invalid");
        }
    }

    private String parseCurrency(String value) {
        String normalized = value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Currency is required");
        }
        if (!normalized.matches("^[A-Z]{3}$")) {
            throw new IllegalArgumentException("Currency must be a 3-letter ISO currency code");
        }
        return normalized;
    }

    private UUID parseUuid(String value, String fieldName) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        try {
            return UUID.fromString(normalized);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(fieldName + " is invalid");
        }
    }

    private String parseDescription(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();
        if (normalized.isEmpty()) {
            return null;
        }
        if (normalized.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException("Description must be at most " + MAX_DESCRIPTION_LENGTH + " characters");
        }
        return normalized;
    }
}
