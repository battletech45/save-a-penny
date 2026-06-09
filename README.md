# SaveAPenny

SaveAPenny is a Spring Boot personal finance backend for teams building or operating budgeting, transaction tracking, imports, OCR-based receipt parsing, notifications, AI-assisted guidance, and goal simulation workflows.

## What The Product Covers

- JWT-based authentication and session refresh
- accounts, categories, transactions, and transfers
- monthly and yearly budgets
- financial reports and monthly-summary CSV export
- recurring transactions
- transaction CSV imports
- OCR receipt and document processing
- notifications and audit history
- AI assistant chat backed by user financial data
- goal tracking, scenarios, what-if analysis, and simulation
- generated financial insights

## Quick Start

### Option 1: Docker Compose

1. Copy `.env.example` to `.env` and fill in real values.
2. Start the app and PostgreSQL:

```bash
docker compose up --build
```

3. Open:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI: `http://localhost:8080/v3/api-docs`
- Health: `http://localhost:8080/actuator/health`

### Option 2: Run Locally

Requirements:

- Java 24
- Maven 3.9+
- PostgreSQL 16+
- Tesseract if OCR is enabled

Start the app:

```bash
mvn spring-boot:run
```

## Core Environment Variables

- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `ASSISTANT_ENABLED`
- `ASSISTANT_AI_PROVIDER`
- `OPENAI_API_KEY` when using OpenAI
- `OPENROUTER_API_KEY` when using OpenRouter

Use `.env.example` as the starting point for local configuration.

## Documentation

- [Getting Started](docs/getting-started.md)
- [Usage Guide](docs/usage-guide.md)
- [API Reference](docs/api-reference.md)
- [Deployment And Operations](docs/deployment-operations.md)
- [Goals Feature Guide](docs/features/goals.md)

## Repository Focus

This repository provides the backend platform and API surface. It does not include a built-in web frontend.
