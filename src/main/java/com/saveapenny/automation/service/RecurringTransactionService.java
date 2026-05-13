package com.saveapenny.automation.service;

import com.saveapenny.automation.dto.CreateRecurringTransactionRequest;
import com.saveapenny.automation.dto.RecurringTransactionResponse;
import com.saveapenny.automation.dto.UpdateRecurringTransactionRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecurringTransactionService {

    RecurringTransactionResponse create(UUID currentUserId, CreateRecurringTransactionRequest request);

    Page<RecurringTransactionResponse> getAll(UUID currentUserId, Pageable pageable);

    RecurringTransactionResponse getById(UUID currentUserId, UUID recurringTransactionId);

    RecurringTransactionResponse update(
            UUID currentUserId,
            UUID recurringTransactionId,
            UpdateRecurringTransactionRequest request);

    void delete(UUID currentUserId, UUID recurringTransactionId);

    List<RecurringTransactionResponse> getDueRecurringTransactions(LocalDate runDate);
}
