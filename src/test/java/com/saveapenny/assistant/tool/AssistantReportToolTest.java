package com.saveapenny.assistant.tool;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.saveapenny.report.dto.CategorySpendingResponse;
import com.saveapenny.report.dto.MonthlySummaryResponse;
import com.saveapenny.report.service.ReportService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssistantReportToolTest {

    @Mock
    private ReportService reportService;

    @Mock
    private AssistantToolContextHolder assistantToolContextHolder;

    @Test
    void buildCurrentMonthContext_returnsCompactSummaryAndTopCategories() {
        UUID userId = UUID.randomUUID();
        LocalDate from = LocalDate.now().withDayOfMonth(1);
        LocalDate to = LocalDate.now();
        when(assistantToolContextHolder.requireCurrentUserId()).thenReturn(userId);
        when(reportService.getMonthlySummary(userId, from, to)).thenReturn(MonthlySummaryResponse.builder()
                .startDate(from)
                .endDate(to)
                .totalIncome(new BigDecimal("3000.00"))
                .totalExpense(new BigDecimal("1800.00"))
                .netSavings(new BigDecimal("1200.00"))
                .build());
        when(reportService.getCategorySpending(userId, from, to)).thenReturn(List.of(
                CategorySpendingResponse.builder()
                        .categoryName("Food")
                        .totalAmount(new BigDecimal("600.00"))
                        .usagePercentage(new BigDecimal("33.33"))
                        .build(),
                CategorySpendingResponse.builder()
                        .categoryName("Transport")
                        .totalAmount(new BigDecimal("200.00"))
                        .usagePercentage(new BigDecimal("11.11"))
                        .build()));

        AssistantReportTool tool = new AssistantReportTool(reportService, assistantToolContextHolder);

        String summary = tool.getCurrentMonthSummary();
        String categories = tool.getTopSpendingCategories(2);

        assertTrue(summary.contains("income=3000.00"));
        assertTrue(categories.contains("Food=600.00 (33.33%)"));
        assertTrue(categories.contains("Transport=200.00 (11.11%)"));
    }
}
