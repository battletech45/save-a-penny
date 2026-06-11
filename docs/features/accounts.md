# Accounts

## Overview

Accounts represent where money is held. Every transaction and transfer is linked to an account. Accounts are user-scoped — each user manages their own set of accounts.

## Account Types

| Type | Use Case |
|------|----------|
| `CASH` | Physical cash / wallet |
| `BANK` | Checking or current account |
| `CREDIT` | Credit card / credit line |
| `SAVINGS` | Savings account |
| `INVESTMENT` | Investment or brokerage account |

## Currency

Each account has a single ISO-4217 currency (e.g., `USD`, `EUR`, `TRY`). All transactions against the account must use the same currency. Currency cannot be changed after the account has been used (non-zero balance, any transaction or transfer history).

## Mutation Rules

| Field | Can change? | Constraint |
|-------|------------|------------|
| Name | Yes | Must be unique per user (including deleted accounts) |
| Type | No | Blocked after account has been used |
| Currency | No | Blocked after account has been used |
| Balance | No | Updated automatically by transactions |

An account is considered "used" if any of these are true:
- `balance != 0`
- `initialBalance != 0`
- Any transaction is linked to the account
- Any transfer references the account

## Deletion

Deleting an account is a soft delete — the record remains but is marked as inactive. The account name stays reserved and cannot be reused by a new account.

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/accounts` | Create an account |
| GET | `/api/v1/accounts` | List accounts (paginated) |
| GET | `/api/v1/accounts/{id}` | Get account details |
| PUT | `/api/v1/accounts/{id}` | Update account name |
| DELETE | `/api/v1/accounts/{id}` | Soft-delete an account |

## Typical Workflow

1. Create a wallet (CASH, USD, 0 balance)
2. Create a checking account (BANK, USD, initial deposit)
3. Record income/expense transactions against the accounts
4. Update account name when needed (e.g., rename "Chase" to "Chase Checking")
