package com.saveapenny.assistant.tool;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.saveapenny.transaction.dto.TransactionResponse;
import com.saveapenny.transaction.entity.TransactionType;
import com.saveapenny.transaction.service.TransactionService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class AssistantTransactionToolTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private AssistantToolContextHolder assistantToolContextHolder;

    @Test
    void buildRecentTransactionContext_returnsCompactRecentTransactions() {
        UUID userId = UUID.randomUUID();
        when(assistantToolContextHolder.requireCurrentUserId()).thenReturn(userId);
        when(transactionService.getAll(
                        eq(userId),
                        eq(LocalDate.now().minusDays(30)),
                        eq(LocalDate.now()),
                        eq(null),
                        eq(null),
                        eq(null),
                        eq(null),
                        eq(null),
                        eq(null),
                        org.mockito.ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(
                        TransactionResponse.builder()
                                .transactionDate(LocalDate.now().minusDays(1))
                                .type(TransactionType.EXPENSE)
                                .amount(new BigDecimal("45.00"))
                                .description("Coffee")
                                .build(),
                        TransactionResponse.builder()
                                .transactionDate(LocalDate.now().minusDays(2))
                                .type(TransactionType.INCOME)
                                .amount(new BigDecimal("1200.00"))
                                .description("Salary")
                                .build())));

        AssistantTransactionTool tool = new AssistantTransactionTool(transactionService, assistantToolContextHolder);

        String context = tool.getRecentTransactions(5);

        assertTrue(context.contains("EXPENSE 45.00 (Coffee)"));
        assertTrue(context.contains("INCOME 1200.00 (Salary)"));
    }
}
