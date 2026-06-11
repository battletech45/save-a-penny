# Transactions

## Overview

Transactions are the core ledger records. Each transaction records a financial event — income or expense — linked to an account and category. Transactions are user-scoped.

## Transaction Types

| Type | Description | Balance Impact |
|------|-------------|----------------|
| `INCOME` | Money received | Increases account balance |
| `EXPENSE` | Money spent | Decreases account balance |

## Currency Validation

The transaction currency must exactly match the currency of the linked account. A transaction with `USD` cannot be posted to an account configured with `EUR`. This validation runs on both create and update.

## Fields

| Field | Required | Notes |
|-------|----------|-------|
| `accountId` | Yes | UUID of the account |
| `categoryId` | Yes | UUID of the category |
| `type` | Yes | `INCOME` or `EXPENSE` |
| `amount` | Yes | Positive decimal value |
| `currency` | Yes | ISO-4217 code, must match account |
| `description` | No | Free text |
| `date` | No | Defaults to current date |
| `merchant` | No | Merchant or payee name |

## Transfers

Transfers move money between two accounts owned by the same user. A transfer creates two transactions:
- An expense on the source account
- An income on the destination account

Both transactions share the same amount and currency. The source and destination currencies must match.

Endpoint: `POST /api/v1/transactions/transfer`

```json
{
  "fromAccountId": "<source-account-uuid>",
  "toAccountId": "<destination-account-uuid>",
  "amount": 500.00,
  "currency": "USD",
  "description": "Transfer to savings"
}
```

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/transactions` | Create a transaction |
| POST | `/api/v1/transactions/transfer` | Transfer between accounts |
| GET | `/api/v1/transactions` | List transactions (paginated, filterable) |
| GET | `/api/v1/transactions/{id}` | Get transaction details |
| PUT | `/api/v1/transactions/{id}` | Update a transaction |
| DELETE | `/api/v1/transactions/{id}` | Delete a transaction |

## Query Filters

`GET /api/v1/transactions` supports:

| Parameter | Type | Description |
|-----------|------|-------------|
| `from` | Date | Start date (inclusive) |
| `to` | Date | End date (inclusive) |
| `type` | String | `INCOME` or `EXPENSE` |
| `accountId` | UUID | Filter by account |
| `categoryId` | UUID | Filter by category |
| `minAmount` | Decimal | Minimum amount |
| `maxAmount` | Decimal | Maximum amount |
| `keyword` | String | Search in description and merchant |
| `page` | Integer | Page number (0-based) |
| `size` | Integer | Page size |
| `sort` | String | Sort field and direction |

## Balance Impact

Creating or deleting a transaction adjusts the linked account's balance:
- Income → balance increases by the amount
- Expense → balance decreases by the amount
- Deleting a transaction reverses the impact
- Updating a transaction adjusts the difference
