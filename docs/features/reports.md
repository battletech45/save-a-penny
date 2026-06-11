# Reports

## Overview

Reports transform transaction history into financial summaries. All reports are date-range based and user-scoped.

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/reports/monthly-summary?from=&to=` | Monthly income/expense summary |
| GET | `/api/v1/reports/monthly-summary/export?from=&to=` | CSV download |
| GET | `/api/v1/reports/category-spending?from=&to=` | Spending by category |
| GET | `/api/v1/reports/cash-flow?from=&to=` | Daily cash flow |
| GET | `/api/v1/reports/net-worth?snapshotDate=` | Net worth snapshot |

## Monthly Summary

Returns income, expense, and net totals grouped by month within the date range.

### CSV Export

The `export` endpoint returns the same data as a downloadable CSV file with `Content-Type: text/csv`.

```bash
curl -O -J "http://localhost:8080/api/v1/reports/monthly-summary/export?from=2026-01-01&to=2026-12-31" \
  -H "Authorization: Bearer <accessToken>"
```

## Category Spending

Returns total spending grouped by category for the given date range. Useful for identifying where money is going.

```json
[
  {
    "categoryId": "<uuid>",
    "categoryName": "Groceries",
    "total": 450.00,
    "transactionCount": 12
  }
]
```

## Cash Flow

Returns daily cash flow data (income and expense totals per day) within the date range. Useful for identifying spending patterns and low-cash periods.

## Net Worth

Returns total assets minus total liabilities as of the given `snapshotDate`. Net worth is computed as:

- Assets: sum of all account balances with type `CASH`, `BANK`, `SAVINGS`, or `INVESTMENT`
- Liabilities: sum of all account balances with type `CREDIT` (credit balances represent debt)

### Snapshots

Net worth results are persisted on first access per (user, date). A daily scheduled job pre-computes snapshots for all active users so historical queries return stable, previously captured values.

```json
{
  "snapshotDate": "2026-06-10",
  "totalAssets": 15000.00,
  "totalLiabilities": 2000.00,
  "netWorth": 13000.00
}
```

## Best Practices

- Use date ranges of 3-6 months for meaningful trends
- Monthly summary is suitable for budget reviews
- Category spending helps identify overspending
- Cash flow is useful for short-term liquidity planning
- Net worth snapshots provide a long-term wealth trend
