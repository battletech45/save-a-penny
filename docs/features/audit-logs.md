# Audit Logs

## Overview

Audit logs track changes to key resources for accountability and debugging. Audit entries are read-only and automatically created by the backend.

## What Is Tracked

| Resource | Events Tracked |
|----------|---------------|
| Transactions | Create, update, delete |
| Accounts | Create, update, delete |
| Categories | Create, update, delete |
| Budgets | Create, update, delete |
| Recurring Transactions | Create, update, delete, pause, resume |
| Users | Profile updates, password changes |

## Audit Entry Contents

Each audit log entry contains:

| Field | Description |
|-------|-------------|
| `id` | UUID |
| `userId` | The user who performed the action |
| `entityType` | The resource type (e.g., `TRANSACTION`, `ACCOUNT`) |
| `entityId` | The UUID of the affected resource |
| `action` | The action performed (e.g., `CREATE`, `UPDATE`, `DELETE`) |
| `changes` | JSON diff of changed fields (for updates) |
| `createdAt` | When the action occurred |

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/audits` | List audit logs (paginated, filterable) |
| GET | `/api/v1/audits/{id}` | Get audit entry details |

## Query Filters

`GET /api/v1/audits` supports:

| Parameter | Type | Description |
|-----------|------|-------------|
| `entityType` | String | Filter by resource type |
| `entityId` | UUID | Filter by resource ID |
| `action` | String | Filter by action |
| `from` | Date | Start date (inclusive) |
| `to` | Date | End date (inclusive) |
| `page` | Integer | Page number (0-based) |
| `size` | Integer | Page size |

## Example

```bash
curl -X GET "http://localhost:8080/api/v1/audits?entityType=TRANSACTION&page=0&size=20" \
  -H "Authorization: Bearer <accessToken>"
```

## Notes

- Audit logs are append-only — entries cannot be modified or deleted via the API
- Diff data is stored as a JSON string for flexibility
- Soft-deleted resources retain their audit history
