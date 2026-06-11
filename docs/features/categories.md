# Categories

## Overview

Categories organize income and expense transactions. The system provides default categories, and users can create their own. Categories are user-scoped (system categories have no userId).

## Category Types

| Type | Description |
|------|-------------|
| `INCOME` | Income categories (e.g., Salary, Freelance) |
| `EXPENSE` | Expense categories (e.g., Groceries, Rent) |

## System vs User Categories

| | System Categories | User Categories |
|--|------------------|-----------------|
| Created by | Backend on startup | User via API |
| `userId` | Null | Set to the creating user |
| Editable | No | Yes |
| Deletable | No | Yes |
| Listed | Always included | Included for the owning user |

## Fields

| Field | Required | Notes |
|-------|----------|-------|
| `name` | Yes | Must be unique per user |
| `type` | Yes | `INCOME` or `EXPENSE` |
| `color` | No | Hex color code for UI display |
| `icon` | No | Icon identifier for UI display |

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/categories` | Create a user category |
| GET | `/api/v1/categories` | List categories (system + user) |
| GET | `/api/v1/categories/{id}` | Get category details |
| PUT | `/api/v1/categories/{id}` | Update a category |
| DELETE | `/api/v1/categories/{id}` | Delete a user category |

## Notes

- Category names must be unique per user (no duplicate names)
- System categories cannot be modified or deleted
- Deleting a category does not delete transactions using it
- Filter categories by type: `GET /api/v1/categories?type=EXPENSE`
