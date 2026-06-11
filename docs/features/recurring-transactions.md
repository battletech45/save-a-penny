# Recurring Transactions

## Overview

Recurring transactions automate regular income or expense entries. Create one with a frequency, and the scheduler automatically generates transactions on the scheduled dates.

## Status Lifecycle

| Status | Description |
|--------|-------------|
| `ACTIVE` | Normal scheduling — transactions are generated on each `nextRunDate` |
| `PAUSED` | Temporarily suspended. Use `resume` to reactivate |
| `EXPIRED` | Past `endDate` or explicitly deleted. No further processing |
| `FAILED` | Last execution attempt failed. Will be retried on next run |

Status transitions:

```
ACTIVE ──pause──▶ PAUSED
PAUSED ──resume─▶ ACTIVE
ACTIVE ──delete──▶ EXPIRED
ACTIVE ──error──▶ FAILED
```

## Frequencies

| Frequency | Description |
|-----------|-------------|
| `DAILY` | Every day |
| `WEEKLY` | Every 7 days |
| `MONTHLY` | Same day each month |
| `YEARLY` | Same date each year |

## Classification

Optional metadata for UI display:

| Value | Example Use |
|-------|-------------|
| `PAYCHECK` | Monthly salary |
| `SUBSCRIPTION` | Netflix, Spotify |
| `RENT` | Monthly rent payment |
| `UTILITY` | Electricity, water |
| `LOAN_PAYMENT` | Car loan, mortgage |
| `SAVINGS_CONTRIBUTION` | Monthly savings transfer |
| `OTHER` | Default |

## Fields

| Field | Required | Notes |
|-------|----------|-------|
| `accountId` | Yes | Transaction goes to this account |
| `categoryId` | Yes | Transaction category |
| `type` | Yes | `INCOME` or `EXPENSE` |
| `amount` | Yes | Positive decimal value |
| `frequency` | Yes | `DAILY`, `WEEKLY`, `MONTHLY`, `YEARLY` |
| `nextRunDate` | Yes | First scheduled date |
| `name` | No | Display name |
| `description` | No | Copied into generated transactions |
| `startDate` | No | When the recurring item takes effect |
| `endDate` | No | Auto-expires after this date |
| `classification` | No | Metadata for UI grouping |

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/automations/recurring-transactions` | Create |
| GET | `/api/v1/automations/recurring-transactions` | List (paginated) |
| GET | `/api/v1/automations/recurring-transactions/{id}` | Get details |
| PUT | `/api/v1/automations/recurring-transactions/{id}` | Update |
| DELETE | `/api/v1/automations/recurring-transactions/{id}` | Soft-delete (→ EXPIRED) |
| PATCH | `/api/v1/automations/recurring-transactions/{id}/pause` | Pause |
| PATCH | `/api/v1/automations/recurring-transactions/{id}/resume` | Resume |
| GET | `/api/v1/automations/recurring-transactions/{id}/history` | Execution history |
| GET | `/api/v1/automations/recurring-transactions/upcoming?limit=10` | Upcoming projections |

## Execution History

The `history` endpoint returns per-run records with:
- Status: `SUCCESS`, `FAILED`, or `SKIPPED`
- Generated transaction ID (if successful)
- Failure reason (if failed)
- Run date

The scheduler is idempotent — it skips dates that already have a `SUCCESS` history entry.

## Upcoming Preview

The `upcoming` endpoint projects future runs for all active recurring items, showing expected dates and amounts. Useful for cash flow forecasting.
