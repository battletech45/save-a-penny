# API Reference

## Base URLs

- API base: `/api/v1`
- OCR base: `/api/imports/ocr`

## Authentication

Public endpoints:

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/logout`
- `GET /actuator/health`
- `GET /v3/api-docs`
- `GET /swagger-ui.html`

All other API endpoints require:

```text
Authorization: Bearer <accessToken>
```

## Standard Response Envelope

Success:

```json
{
  "success": true,
  "data": {},
  "error": null,
  "timestamp": "2026-06-09T12:00:00Z"
}
```

Error:

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "VALIDATION_FAILED",
    "message": "Request validation failed.",
    "details": []
  },
  "timestamp": "2026-06-09T12:00:00Z"
}
```

## Common Conventions

- IDs are UUIDs
- dates use ISO-8601 format such as `2026-06-09`
- currencies use 3-letter ISO codes such as `USD`, `EUR`, `TRY`
- most list endpoints use Spring pagination: `page`, `size`, `sort`

## Authentication Endpoints

### Register

`POST /api/v1/auth/register`

```json
{
  "email": "demo@example.com",
  "password": "StrongPass123!",
  "fullName": "Demo User"
}
```

### Login

`POST /api/v1/auth/login`

```json
{
  "email": "demo@example.com",
  "password": "StrongPass123!"
}
```

### Refresh

`POST /api/v1/auth/refresh`

```json
{
  "refreshToken": "<refreshToken>"
}
```

### Logout

`POST /api/v1/auth/logout`

```json
{
  "refreshToken": "<refreshToken>"
}
```

## Endpoint Groups

### Users

- `GET /api/v1/users/me`
- `PUT /api/v1/users/me`
- `PUT /api/v1/users/me/password`

### Accounts

- `POST /api/v1/accounts`
- `GET /api/v1/accounts`
- `GET /api/v1/accounts/{accountId}`
- `PUT /api/v1/accounts/{accountId}`
- `DELETE /api/v1/accounts/{accountId}`

### Categories

- `POST /api/v1/categories`
- `GET /api/v1/categories`
- `GET /api/v1/categories/{categoryId}`
- `PUT /api/v1/categories/{categoryId}`
- `DELETE /api/v1/categories/{categoryId}`

### Transactions

- `POST /api/v1/transactions`
- `POST /api/v1/transactions/transfer`
- `GET /api/v1/transactions`
- `GET /api/v1/transactions/{transactionId}`
- `PUT /api/v1/transactions/{transactionId}`
- `DELETE /api/v1/transactions/{transactionId}`

Transaction query filters:

- `from`
- `to`
- `type`
- `accountId`
- `categoryId`
- `minAmount`
- `maxAmount`
- `keyword`
- `page`
- `size`
- `sort`

### Budgets

- `POST /api/v1/budgets`
- `GET /api/v1/budgets`
- `GET /api/v1/budgets/{budgetId}`
- `GET /api/v1/budgets/{budgetId}/status`
- `PUT /api/v1/budgets/{budgetId}`
- `DELETE /api/v1/budgets/{budgetId}`
- `DELETE /api/v1/budgets/batch`

### Reports

- `GET /api/v1/reports/monthly-summary`
- `GET /api/v1/reports/monthly-summary/export`
- `GET /api/v1/reports/category-spending`
- `GET /api/v1/reports/cash-flow`
- `GET /api/v1/reports/net-worth`

### Recurring Transactions

- `POST /api/v1/automations/recurring-transactions`
- `GET /api/v1/automations/recurring-transactions`
- `GET /api/v1/automations/recurring-transactions/{recurringTransactionId}`
- `PUT /api/v1/automations/recurring-transactions/{recurringTransactionId}`
- `DELETE /api/v1/automations/recurring-transactions/{recurringTransactionId}`

### Notifications

- `POST /api/v1/notifications`
- `GET /api/v1/notifications`
- `GET /api/v1/notifications/{notificationId}`
- `PUT /api/v1/notifications/{notificationId}`
- `DELETE /api/v1/notifications/{notificationId}`
- `GET /api/v1/notifications/unread-count`
- `PATCH /api/v1/notifications/mark-all-read`

### Transaction Imports

- `POST /api/v1/imports/transactions/preview`
- `POST /api/v1/imports/transactions/confirm`
- `GET /api/v1/imports/transactions/{importId}/status`

### OCR Imports

- `POST /api/imports/ocr`
- `GET /api/imports/ocr/{jobId}`

### Audits

- `GET /api/v1/audits`
- `GET /api/v1/audits/{auditLogId}`

### Assistant

- `POST /api/v1/assistant/chat`

### Insights

- `GET /api/v1/insights`
- `GET /api/v1/insights/{id}`
- `PATCH /api/v1/insights/{id}/read`
- `PATCH /api/v1/insights/{id}/dismiss`
- `POST /api/v1/insights/generate`

### Goals

- `POST /api/v1/goals`
- `GET /api/v1/goals`
- `GET /api/v1/goals/{goalId}`
- `PATCH /api/v1/goals/{goalId}`
- `DELETE /api/v1/goals/{goalId}`
- `PATCH /api/v1/goals/{goalId}/status`
- `POST /api/v1/goals/{goalId}/scenarios`
- `GET /api/v1/goals/{goalId}/scenarios`
- `GET /api/v1/goals/{goalId}/runs`
- `POST /api/v1/goals/simulate`
- `POST /api/v1/goals/simulate/draft`
- `POST /api/v1/goals/{goalId}/simulate`
- `POST /api/v1/goals/{goalId}/scenarios/compare`
- `POST /api/v1/goals/{goalId}/what-if`

## Example Protected Request

```bash
curl -X GET "http://localhost:8080/api/v1/accounts?page=0&size=20&sort=name,asc" \
  -H "Authorization: Bearer <accessToken>"
```

## Common Error Cases

- `401 ACCESS_DENIED`: missing or invalid authentication
- `400 VALIDATION_FAILED`: request body or query parameter validation failed
- `404 *_NOT_FOUND`: resource does not exist or is not owned by the caller
- `409 *_ALREADY_EXISTS`: duplicate resource or conflicting request
- `503 ASSISTANT_DISABLED`: assistant feature is disabled

## Source Of Truth For Schemas

Use Swagger UI and the OpenAPI document for the most complete field-level schema details:

- `http://localhost:8080/swagger-ui.html`
- `http://localhost:8080/v3/api-docs`
