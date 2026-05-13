package com.saveapenny.automation.repository;

import com.saveapenny.automation.entity.RecurringTransaction;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, UUID> {

    Optional<RecurringTransaction> findByIdAndUserIdAndActiveTrue(UUID id, UUID userId);

    Page<RecurringTransaction> findAllByUserIdAndActiveTrue(UUID userId, Pageable pageable);

    List<RecurringTransaction> findAllByActiveTrueAndNextRunDateLessThanEqual(LocalDate runDate);
}
