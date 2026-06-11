# Budgets

## Overview

Budgets track spending against defined limits. A budget is linked to a category and a period (monthly or yearly). Budgets are user-scoped.

## Budget Periods

| Period | Description | Example |
|--------|-------------|---------|
| `MONTHLY` | Resets each calendar month | $500/month on groceries |
| `YEARLY` | Resets each calendar year | $6,000/year on dining out |

## Fields

| Field | Required | Notes |
|-------|----------|-------|
| `categoryId` | Yes | UUID of the category to budget |
| `period` | Yes | `MONTHLY` or `YEARLY` |
| `limitAmount` | Yes | Maximum spending limit |
| `month` | No | Month/year for the budget (e.g., `2026-06`) |
| `name` | No | Display name |

## Budget Status

`GET /api/v1/budgets/{budgetId}/status` returns the current spending against the budget:

```json
{
  "budgetId": "<uuid>",
  "spent": 320.50,
  "limitAmount": 500.00,
  "remaining": 179.50,
  "percentageUsed": 64.1,
  "period": "MONTHLY"
}
```

Spent amount is calculated from transactions matching the category and period.

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/budgets` | Create a budget |
| GET | `/api/v1/budgets` | List budgets (paginated) |
| GET | `/api/v1/budgets/{id}` | Get budget details |
| GET | `/api/v1/budgets/{id}/status` | Get spending status |
| PUT | `/api/v1/budgets/{id}` | Update budget |
| DELETE | `/api/v1/budgets/{id}` | Delete a budget |
| DELETE | `/api/v1/budgets/batch` | Delete multiple budgets |

## Notes

- Budget amounts are in the account currency
- A budget can only be created for categories the user owns or system categories
- The same category cannot have duplicate budgets for the same period
