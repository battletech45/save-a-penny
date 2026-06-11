# Assistant

## Overview

The assistant provides AI-powered financial guidance through a chat interface. It is an optional feature disabled by default.

## Enabling

Set the required environment variables:

```env
ASSISTANT_ENABLED=true
ASSISTANT_AI_PROVIDER=openrouter
OPENROUTER_API_KEY=<your-api-key>
```

Supported providers: `openrouter`, `openai`.

## Chat Endpoint

`POST /api/v1/assistant/chat`

```json
{
  "message": "Where am I spending the most this month?",
  "sessionId": "<optional-session-uuid>",
  "history": []
}
```

The assistant uses your financial data (transactions, budgets, goals) to provide context-aware answers.

## Example Questions

- "Where am I spending the most this month?"
- "Which categories are over budget?"
- "Why is my cash flow negative?"
- "What should I cut first?"
- "How is my emergency fund goal progressing?"

## Capabilities

- Financial data retrieval (recent transactions, spending by category)
- Budget status and alerts
- Goal progress inquiries
- General budgeting advice
- Multi-turn conversations via `sessionId`

## Limitations

- Not a licensed financial advisor
- Cannot execute trades or manage external accounts
- Responses depend on available data quality
- When disabled, returns `503 ASSISTANT_DISABLED`

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/assistant/chat` | Send a message and get a response |
