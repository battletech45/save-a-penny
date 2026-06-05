package com.saveapenny.mcp.adapter.springai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.saveapenny.assistant.tool.AssistantToolContextHolder;
import com.saveapenny.mcp.budget.MonthlyBudgetStatusToolInput;
import com.saveapenny.mcp.budget.MonthlyBudgetStatusToolResult;
import com.saveapenny.mcp.execution.ToolExecutionContext;
import com.saveapenny.mcp.execution.ToolHandler;
import com.saveapenny.mcp.execution.ToolResult;
import com.saveapenny.mcp.registry.ToolRegistry;
import com.saveapenny.mcp.report.CurrentMonthSummaryToolInput;
import com.saveapenny.mcp.report.CurrentMonthSummaryToolResult;
import com.saveapenny.mcp.report.TopSpendingCategoriesToolInput;
import com.saveapenny.mcp.report.TopSpendingCategoriesToolResult;
import com.saveapenny.mcp.transaction.RecentTransactionsToolInput;
import com.saveapenny.mcp.transaction.RecentTransactionsToolResult;
import com.saveapenny.transaction.entity.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SpringAiMcpToolAdapterTest {

    @Mock
    private ToolRegistry toolRegistry;

    @Mock
    private AssistantToolContextHolder assistantToolContextHolder;

    @Mock
    private ToolHandler<CurrentMonthSummaryToolInput, CurrentMonthSummaryToolResult> currentMonthSummaryHandler;

    @Mock
    private ToolHandler<TopSpendingCategoriesToolInput, TopSpendingCategoriesToolResult> topSpendingCategoriesHandler;

    @Mock
    private ToolHandler<MonthlyBudgetStatusToolInput, MonthlyBudgetStatusToolResult> monthlyBudgetStatusHandler;

    @Mock
    private ToolHandler<RecentTransactionsToolInput, RecentTransactionsToolResult> recentTransactionsHandler;

    private SpringAiMcpToolAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new SpringAiMcpToolAdapter(toolRegistry, assistantToolContextHolder);
    }

    @Test
    void getCurrentMonthSummary_formatsHandlerResultAndPassesUserContext() {
        UUID userId = UUID.randomUUID();
        when(assistantToolContextHolder.requireCurrentUserId()).thenReturn(userId);
        when(toolRegistry.findByName("getCurrentMonthSummary")).thenReturn(Optional.of(currentMonthSummaryHandler));
        when(currentMonthSummaryHandler.execute(any(ToolExecutionContext.class), any(CurrentMonthSummaryToolInput.class)))
                .thenReturn(ToolResult.of(new CurrentMonthSummaryToolResult(
                        LocalDate.of(2026, 5, 1),
                        LocalDate.of(2026, 5, 18),
                        new BigDecimal("3000.00"),
                        new BigDecimal("1800.00"),
                        new BigDecimal("1200.00"))));

        String result = adapter.getCurrentMonthSummary();

        ArgumentCaptor<ToolExecutionContext> contextCaptor = ArgumentCaptor.forClass(ToolExecutionContext.class);
        verify(currentMonthSummaryHandler).execute(contextCaptor.capture(), any(CurrentMonthSummaryToolInput.class));
        assertEquals(userId, contextCaptor.getValue().userId());
        assertTrue(result.contains("income=3000.00"));
        assertTrue(result.contains("expense=1800.00"));
        assertTrue(result.contains("netSavings=1200.00"));
    }

    @Test
    void getTopSpendingCategories_returnsFormattedCategoryLines() {
        when(assistantToolContextHolder.requireCurrentUserId()).thenReturn(UUID.randomUUID());
        when(toolRegistry.findByName("getTopSpendingCategories")).thenReturn(Optional.of(topSpendingCategoriesHandler));
        when(topSpendingCategoriesHandler.execute(any(ToolExecutionContext.class), any(TopSpendingCategoriesToolInput.class)))
                .thenReturn(ToolResult.of(new TopSpendingCategoriesToolResult(
                        LocalDate.of(2026, 5, 1),
                        LocalDate.of(2026, 5, 18),
                        List.of(
                                new TopSpendingCategoriesToolResult.TopSpendingCategoryItem(
                                        UUID.randomUUID(),
                                        "Food",
                                        new BigDecimal("600.00"),
                                        new BigDecimal("33.33")),
                                new TopSpendingCategoriesToolResult.TopSpendingCategoryItem(
                                        UUID.randomUUID(),
                                        "Transport",
                                        new BigDecimal("200.00"),
                                        new BigDecimal("11.11"))))));

        String result = adapter.getTopSpendingCategories(2);

        assertTrue(result.contains("Food=600.00 (33.33%)"));
        assertTrue(result.contains("Transport=200.00 (11.11%)"));
    }

    @Test
    void getMonthlyBudgetStatus_returnsNoneWhenNoBudgets() {
        when(assistantToolContextHolder.requireCurrentUserId()).thenReturn(UUID.randomUUID());
        when(toolRegistry.findByName("getMonthlyBudgetStatus")).thenReturn(Optional.of(monthlyBudgetStatusHandler));
        when(monthlyBudgetStatusHandler.execute(any(ToolExecutionContext.class), any(MonthlyBudgetStatusToolInput.class)))
                .thenReturn(ToolResult.of(new MonthlyBudgetStatusToolResult(List.of())));

        String result = adapter.getMonthlyBudgetStatus(3);

        assertEquals("Monthly budget status: none.", result);
    }

    @Test
    void getRecentTransactions_returnsFormattedTransactions() {
        when(assistantToolContextHolder.requireCurrentUserId()).thenReturn(UUID.randomUUID());
        when(toolRegistry.findByName("getRecentTransactions")).thenReturn(Optional.of(recentTransactionsHandler));
        when(recentTransactionsHandler.execute(any(ToolExecutionContext.class), any(RecentTransactionsToolInput.class)))
                .thenReturn(ToolResult.of(new RecentTransactionsToolResult(
                        LocalDate.of(2026, 4, 18),
                        LocalDate.of(2026, 5, 18),
                        List.of(
                                new RecentTransactionsToolResult.RecentTransactionItem(
                                        UUID.randomUUID(),
                                        UUID.randomUUID(),
                                        UUID.randomUUID(),
                                        TransactionType.EXPENSE,
                                        new BigDecimal("45.00"),
                                        "TRY",
                                        "Coffee",
                                        LocalDate.of(2026, 5, 17))))));

        String result = adapter.getRecentTransactions(5);

        assertTrue(result.contains("EXPENSE 45.00 (Coffee)"));
    }

    @Test
    void throwsWhenRegistryDoesNotContainRequestedTool() {
        when(toolRegistry.findByName("getCurrentMonthSummary")).thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(IllegalStateException.class, adapter::getCurrentMonthSummary);

        assertTrue(exception.getMessage().contains("could not resolve tool"));
    }
}
