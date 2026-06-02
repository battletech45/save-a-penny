package com.saveapenny.assistant.tool;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.saveapenny.budget.dto.BudgetResponse;
import com.saveapenny.budget.dto.BudgetStatusResponse;
import com.saveapenny.budget.entity.BudgetPeriod;
import com.saveapenny.budget.service.BudgetService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class AssistantBudgetToolTest {

    @Mock
    private BudgetService budgetService;

    @Mock
    private AssistantToolContextHolder assistantToolContextHolder;

    @Test
    void buildCurrentBudgetContext_returnsCompactBudgetStatusLines() {
        UUID userId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();
        when(assistantToolContextHolder.requireCurrentUserId()).thenReturn(userId);
        when(budgetService.getAll(userId, BudgetPeriod.MONTHLY, PageRequest.of(0, 3)))
                .thenReturn(new PageImpl<>(List.of(BudgetResponse.builder()
                        .id(budgetId)
                        .amount(new BigDecimal("800.00"))
                        .period(BudgetPeriod.MONTHLY)
                        .startDate(LocalDate.now().withDayOfMonth(1))
                        .endDate(LocalDate.now())
                        .build())));
        when(budgetService.getStatus(userId, budgetId)).thenReturn(BudgetStatusResponse.builder()
                .category("Food")
                .budgetAmount(new BigDecimal("800.00"))
                .spentAmount(new BigDecimal("500.00"))
                .remainingAmount(new BigDecimal("300.00"))
                .status("ON_TRACK")
                .build());

        AssistantBudgetTool tool = new AssistantBudgetTool(budgetService, assistantToolContextHolder);

        String context = tool.getMonthlyBudgetStatus(3);

        assertTrue(context.contains("Food spent=500.00 of 800.00"));
        assertTrue(context.contains("status=ON_TRACK"));
    }
}
