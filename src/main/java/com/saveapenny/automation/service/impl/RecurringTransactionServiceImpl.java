package com.saveapenny.automation.service.impl;

import com.saveapenny.account.repository.AccountRepository;
import com.saveapenny.automation.dto.CreateRecurringTransactionRequest;
import com.saveapenny.automation.dto.RecurringTransactionResponse;
import com.saveapenny.automation.dto.UpdateRecurringTransactionRequest;
import com.saveapenny.automation.entity.RecurringTransaction;
import com.saveapenny.automation.exception.InvalidRecurringTransactionNextRunDateException;
import com.saveapenny.automation.exception.InvalidRecurringTransactionTypeException;
import com.saveapenny.automation.exception.RecurringTransactionDependencyNotFoundException;
import com.saveapenny.automation.exception.RecurringTransactionNotFoundException;
import com.saveapenny.automation.mapper.RecurringTransactionMapper;
import com.saveapenny.automation.repository.RecurringTransactionRepository;
import com.saveapenny.automation.service.RecurringTransactionService;
import com.saveapenny.category.entity.Category;
import com.saveapenny.category.repository.CategoryRepository;
import com.saveapenny.transaction.entity.TransactionType;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RecurringTransactionServiceImpl implements RecurringTransactionService {

    private final RecurringTransactionRepository recurringTransactionRepository;
    private final RecurringTransactionMapper recurringTransactionMapper;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    public RecurringTransactionServiceImpl(
            RecurringTransactionRepository recurringTransactionRepository,
            RecurringTransactionMapper recurringTransactionMapper,
            AccountRepository accountRepository,
            CategoryRepository categoryRepository) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.recurringTransactionMapper = recurringTransactionMapper;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public RecurringTransactionResponse create(UUID currentUserId, CreateRecurringTransactionRequest request) {
        validateType(request.getType());
        validateNextRunDate(request.getNextRunDate());
        ensureOwnedActiveAccount(currentUserId, request.getAccountId());
        ensureCategoryVisible(currentUserId, request.getCategoryId());

        RecurringTransaction recurringTransaction = recurringTransactionMapper.toEntity(request);
        recurringTransaction.setUserId(currentUserId);

        RecurringTransaction saved = recurringTransactionRepository.save(recurringTransaction);
        return recurringTransactionMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RecurringTransactionResponse> getAll(UUID currentUserId, Pageable pageable) {
        return recurringTransactionRepository.findAllByUserIdAndActiveTrue(currentUserId, pageable)
                .map(recurringTransactionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public RecurringTransactionResponse getById(UUID currentUserId, UUID recurringTransactionId) {
        RecurringTransaction recurringTransaction = findOwnedActiveRecurringTransaction(currentUserId, recurringTransactionId);
        return recurringTransactionMapper.toResponse(recurringTransaction);
    }

    @Override
    public RecurringTransactionResponse update(
            UUID currentUserId,
            UUID recurringTransactionId,
            UpdateRecurringTransactionRequest request) {
        validateType(request.getType());
        validateNextRunDate(request.getNextRunDate());
        ensureOwnedActiveAccount(currentUserId, request.getAccountId());
        ensureCategoryVisible(currentUserId, request.getCategoryId());

        RecurringTransaction recurringTransaction = findOwnedActiveRecurringTransaction(currentUserId, recurringTransactionId);
        recurringTransactionMapper.updateEntity(recurringTransaction, request);

        RecurringTransaction saved = recurringTransactionRepository.save(recurringTransaction);
        return recurringTransactionMapper.toResponse(saved);
    }

    @Override
    public void delete(UUID currentUserId, UUID recurringTransactionId) {
        RecurringTransaction recurringTransaction = findOwnedActiveRecurringTransaction(currentUserId, recurringTransactionId);
        recurringTransaction.setActive(false);
        recurringTransactionRepository.save(recurringTransaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecurringTransactionResponse> getDueRecurringTransactions(LocalDate runDate) {
        LocalDate effectiveRunDate = runDate == null ? LocalDate.now() : runDate;
        return recurringTransactionRepository.findAllByActiveTrueAndNextRunDateLessThanEqual(effectiveRunDate)
                .stream()
                .map(recurringTransactionMapper::toResponse)
                .toList();
    }

    private RecurringTransaction findOwnedActiveRecurringTransaction(UUID currentUserId, UUID recurringTransactionId) {
        return recurringTransactionRepository.findByIdAndUserIdAndActiveTrue(recurringTransactionId, currentUserId)
                .orElseThrow(() -> new RecurringTransactionNotFoundException(recurringTransactionId));
    }

    private void ensureOwnedActiveAccount(UUID currentUserId, UUID accountId) {
        boolean exists = accountRepository.existsByIdAndUserIdAndActiveTrue(accountId, currentUserId);
        if (!exists) {
            throw new RecurringTransactionDependencyNotFoundException("account", accountId);
        }
    }

    private void ensureCategoryVisible(UUID currentUserId, UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RecurringTransactionDependencyNotFoundException("category", categoryId));
        if (category.getUserId() != null && !category.getUserId().equals(currentUserId)) {
            throw new RecurringTransactionDependencyNotFoundException("category", categoryId);
        }
    }

    private void validateType(TransactionType type) {
        if (type == TransactionType.TRANSFER) {
            throw new InvalidRecurringTransactionTypeException(type);
        }
    }

    private void validateNextRunDate(LocalDate nextRunDate) {
        if (nextRunDate == null || nextRunDate.isBefore(LocalDate.now())) {
            throw new InvalidRecurringTransactionNextRunDateException(nextRunDate);
        }
    }
}
