# CSV Import

## Overview

The CSV import workflow lets users bulk-import transactions from bank or accounting CSV files. The flow uses a preview-confirm-status pattern to give users control before committing data.

## Workflow

```
1. Upload CSV ──▶ Preview ──▶ 2. Confirm ──▶ 3. Poll Status
                      │                        │
                      └── validation errors     └── complete or failed
```

### Step 1: Preview

`POST /api/v1/imports/transactions/preview`

Upload a CSV file. The endpoint parses the file, returns parsed rows with any validation errors, and returns an `importId`.

```bash
curl -X POST "http://localhost:8080/api/v1/imports/transactions/preview" \
  -H "Authorization: Bearer <accessToken>" \
  -F "file=@transactions.csv"
```

Response includes:
- `importId` — used in subsequent steps
- `rows` — parsed transactions with status (`VALID`, `WARNING`, `ERROR`)
- `totalCount` — total rows parsed
- `validCount` — rows ready to import

### Step 2: Confirm

`POST /api/v1/imports/transactions/confirm`

```json
{
  "importId": "<importId>"
}
```

Starts the async import. Returns immediately — the import runs in the background.

### Step 3: Poll Status

`GET /api/v1/imports/transactions/{importId}/status`

Poll until `status` is `COMPLETED` or `FAILED`.

| Status | Meaning |
|--------|---------|
| `PENDING` | Waiting to be processed |
| `PROCESSING` | Import is running |
| `COMPLETED` | All valid rows imported |
| `FAILED` | Import encountered an unrecoverable error |
| `PARTIALLY_COMPLETED` | Some rows imported, some failed |

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/imports/transactions/preview` | Upload and preview CSV |
| POST | `/api/v1/imports/transactions/confirm` | Confirm and start import |
| GET | `/api/v1/imports/transactions/{importId}/status` | Poll import status |
