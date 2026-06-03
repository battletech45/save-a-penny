package com.saveapenny.assistant.tool;

import com.saveapenny.transaction.dto.TransactionResponse;
import com.saveapenny.transaction.service.TransactionService;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class AssistantTransactionTool {

    private final TransactionService transactionService;
    private final AssistantToolContextHolder assistantToolContextHolder;
    private final Clock clock;

    public AssistantTransactionTool(
            TransactionService transactionService,
            AssistantToolContextHolder assistantToolContextHolder,
            Clock clock) {
        this.transactionService = transactionService;
        this.assistantToolContextHolder = assistantToolContextHolder;
        this.clock = clock;
    }

    @Tool(name = "getRecentTransactions", description = "Get the authenticated user's recent transactions from the last 30 days.")
    public String getRecentTransactions(
            @ToolParam(description = "Maximum number of transactions to include.", required = false) int limit) {
        UUID userId = assistantToolContextHolder.requireCurrentUserId();
        LocalDate today = LocalDate.now(clock);
        Page<TransactionResponse> page = transactionService.getAll(
                userId,
                today.minusDays(30),
                today,
                null,
                null,
                null,
                null,
                null,
                null,
                PageRequest.of(0, Math.max(1, limit), Sort.by(Sort.Direction.DESC, "transactionDate", "createdAt")));

        List<TransactionResponse> transactions = page.getContent();
        if (transactions.isEmpty()) {
            return "Recent transactions: none in the last 30 days.";
        }

        StringBuilder builder = new StringBuilder("Recent transactions: ");
        for (int i = 0; i < transactions.size(); i++) {
            TransactionResponse item = transactions.get(i);
            if (i > 0) {
                builder.append("; ");
            }
            builder.append(item.getTransactionDate())
                    .append(' ')
                    .append(item.getType())
                    .append(' ')
                    .append(item.getAmount());
            if (item.getDescription() != null && !item.getDescription().isBlank()) {
                builder.append(" (")
                        .append(item.getDescription().trim())
                        .append(')');
            }
        }
        builder.append('.');
        return builder.toString();
    }
}
