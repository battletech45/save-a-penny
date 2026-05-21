package com.saveapenny.service.ocr;

import com.saveapenny.imports.dto.OcrTransactionCandidateResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class OcrTextParserService {

    private static final Pattern DATE_PATTERN = Pattern.compile("\\b(\\d{4}-\\d{2}-\\d{2}|\\d{2}/\\d{2}/\\d{4})\\b");
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("[-+]?\\d{1,6}(?:[.,]\\d{2})");

    public List<OcrTransactionCandidateResponse> parseTransactionCandidates(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            return List.of();
        }

        String[] lines = rawText.split("\\R");
        List<OcrTransactionCandidateResponse> results = new ArrayList<>();
        for (String line : lines) {
            OcrTransactionCandidateResponse candidate = parseLine(line);
            if (candidate != null) {
                results.add(candidate);
            }
        }
        return results;
    }

    private OcrTransactionCandidateResponse parseLine(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }

        Matcher dateMatcher = DATE_PATTERN.matcher(line);
        Matcher amountMatcher = AMOUNT_PATTERN.matcher(line);

        LocalDate date = dateMatcher.find() ? parseDate(dateMatcher.group(1)) : null;
        BigDecimal amount = amountMatcher.find() ? parseAmount(amountMatcher.group()) : null;

        if (date == null || amount == null) {
            return null;
        }

        return OcrTransactionCandidateResponse.builder()
                .date(date)
                .amount(amount)
                .description(line.trim())
                .categoryHint(detectCategoryHint(line))
                .build();
    }

    private LocalDate parseDate(String value) {
        if (value.contains("-")) {
            return LocalDate.parse(value);
        }
        try {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private BigDecimal parseAmount(String value) {
        try {
            return new BigDecimal(value.replace(",", "."));
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private String detectCategoryHint(String line) {
        String normalized = line.toLowerCase(Locale.ROOT);
        if (normalized.contains("market") || normalized.contains("grocery") || normalized.contains("restaurant")) {
            return "FOOD";
        }
        if (normalized.contains("uber") || normalized.contains("taxi") || normalized.contains("fuel")) {
            return "TRANSPORT";
        }
        if (normalized.contains("salary") || normalized.contains("payroll")) {
            return "INCOME";
        }
        return "UNCATEGORIZED";
    }
}
