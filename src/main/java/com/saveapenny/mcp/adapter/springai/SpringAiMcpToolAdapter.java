package com.saveapenny.mcp.adapter.springai;

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
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class SpringAiMcpToolAdapter {

    private static final String CURRENT_MONTH_SUMMARY_TOOL = "getCurrentMonthSummary";
    private static final String TOP_SPENDING_CATEGORIES_TOOL = "getTopSpendingCategories";
    private static final String MONTHLY_BUDGET_STATUS_TOOL = "getMonthlyBudgetStatus";
    private static final String RECENT_TRANSACTIONS_TOOL = "getRecentTransactions";

    private final ToolRegistry toolRegistry;
    private final AssistantToolContextHolder assistantToolContextHolder;

    public SpringAiMcpToolAdapter(
            ToolRegistry toolRegistry,
            AssistantToolContextHolder assistantToolContextHolder) {
        this.toolRegistry = toolRegistry;
        this.assistantToolContextHolder = assistantToolContextHolder;
    }

    @Tool(name = CURRENT_MONTH_SUMMARY_TOOL, description = "Get the authenticated user's current month income, expense, and net savings summary.")
    public String getCurrentMonthSummary() {
        CurrentMonthSummaryToolResult summary = execute(
                CURRENT_MONTH_SUMMARY_TOOL,
                new CurrentMonthSummaryToolInput(),
                CurrentMonthSummaryToolResult.class);
        return "Current month summary: income="
                + summary.totalIncome()
                + ", expense="
                + summary.totalExpense()
                + ", netSavings="
                + summary.netSavings()
                + '.';
    }

    @Tool(name = TOP_SPENDING_CATEGORIES_TOOL, description = "Get the authenticated user's top expense categories for the current month.")
    public String getTopSpendingCategories(
            @ToolParam(description = "Maximum number of categories to return.", required = false) int topCategoriesLimit) {
        TopSpendingCategoriesToolResult topCategories = execute(
                        TOP_SPENDING_CATEGORIES_TOOL,
                        new TopSpendingCategoriesToolInput(topCategoriesLimit),
                        TopSpendingCategoriesToolResult.class);

        if (topCategories.categories().isEmpty()) {
            return "Top spending categories: none for the current month.";
        }

        StringBuilder builder = new StringBuilder("Top spending categories: ");
        for (int i = 0; i < topCategories.categories().size(); i++) {
            TopSpendingCategoriesToolResult.TopSpendingCategoryItem item = topCategories.categories().get(i);
            if (i > 0) {
                builder.append("; ");
            }
            builder.append(item.categoryName())
                    .append('=')
                    .append(item.totalAmount())
                    .append(" (")
                    .append(item.usagePercentage())
                    .append("%)");
        }
        builder.append('.');
        return builder.toString();
    }

    @Tool(name = MONTHLY_BUDGET_STATUS_TOOL, description = "Get the authenticated user's monthly budget status overview.")
    public String getMonthlyBudgetStatus(
            @ToolParam(description = "Maximum number of budget entries to include.", required = false) int limit) {
        MonthlyBudgetStatusToolResult budgets = execute(
                MONTHLY_BUDGET_STATUS_TOOL,
                new MonthlyBudgetStatusToolInput(limit),
                MonthlyBudgetStatusToolResult.class);

        if (budgets.budgets().isEmpty()) {
            return "Monthly budget status: none.";
        }

        StringBuilder builder = new StringBuilder("Monthly budget status: ");
        for (int i = 0; i < budgets.budgets().size(); i++) {
            MonthlyBudgetStatusToolResult.BudgetStatusItem status = budgets.budgets().get(i);
            if (i > 0) {
                builder.append("; ");
            }
            builder.append(status.category())
                    .append(" spent=")
                    .append(status.spentAmount())
                    .append(" of ")
                    .append(status.budgetAmount())
                    .append(", remaining=")
                    .append(status.remainingAmount())
                    .append(", status=")
                    .append(status.status());
        }
        builder.append('.');
        return builder.toString();
    }

    @Tool(name = RECENT_TRANSACTIONS_TOOL, description = "Get the authenticated user's recent transactions from the last 30 days.")
    public String getRecentTransactions(
            @ToolParam(description = "Maximum number of transactions to include.", required = false) int limit) {
        RecentTransactionsToolResult transactions = execute(
                RECENT_TRANSACTIONS_TOOL,
                new RecentTransactionsToolInput(limit),
                RecentTransactionsToolResult.class);
        if (transactions.transactions().isEmpty()) {
            return "Recent transactions: none in the last 30 days.";
        }

        StringBuilder builder = new StringBuilder("Recent transactions: ");
        for (int i = 0; i < transactions.transactions().size(); i++) {
            RecentTransactionsToolResult.RecentTransactionItem item = transactions.transactions().get(i);
            if (i > 0) {
                builder.append("; ");
            }
            builder.append(item.transactionDate())
                    .append(' ')
                    .append(item.type())
                    .append(' ')
                    .append(item.amount());
            if (item.description() != null && !item.description().isBlank()) {
                builder.append(" (")
                        .append(item.description().trim())
                        .append(')');
            }
        }
        builder.append('.');
        return builder.toString();
    }

    @SuppressWarnings("unchecked")
    private <I, O> O execute(String toolName, I input, Class<O> outputType) {
        ToolHandler<I, O> handler = (ToolHandler<I, O>) toolRegistry.findByName(toolName)
                .orElseThrow(() -> new IllegalStateException("Spring AI MCP adapter could not resolve tool: " + toolName));
        ToolResult<O> result = handler.execute(currentContext(), input);
        return outputType.cast(result.data());
    }

    private ToolExecutionContext currentContext() {
        return new ToolExecutionContext(assistantToolContextHolder.requireCurrentUserId());
    }
}
