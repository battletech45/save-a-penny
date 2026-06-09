# Getting Started

## Who This Is For

Use this guide if you want to get SaveAPenny running locally for development, testing, integration work, or product evaluation.

## Prerequisites

Required:

- Java 24
- Maven 3.9+
- PostgreSQL 16+

Optional:

- Docker and Docker Compose
- Tesseract if you want OCR endpoints enabled
- OpenAI or OpenRouter credentials if you want assistant features enabled

## 1. Configure Environment Variables

Create a local `.env` file from `.env.example`.

Minimum required values:

```env
POSTGRES_DB=saveapenny
POSTGRES_USER=saveapenny_app
POSTGRES_PASSWORD=change_me_local_only
DB_USERNAME=saveapenny_app
DB_PASSWORD=change_me_local_only
JWT_SECRET=change_me_to_a_64_plus_char_secret_for_hs512_signing_key
ASSISTANT_ENABLED=false
ASSISTANT_AI_PROVIDER=openrouter
```

Assistant provider values:

- `OPENROUTER_API_KEY` for OpenRouter
- `OPENAI_API_KEY` for OpenAI

## 2. Start The Application

### Option A: Docker Compose

This starts PostgreSQL and the application together.

```bash
docker compose up --build
```

Default exposed ports:

- app: `8080`
- PostgreSQL: `5432`

### Option B: Run With Maven

First make sure PostgreSQL is already running and matches your `.env` values.

```bash
mvn spring-boot:run
```

## 3. Verify Startup

Open these URLs after the app starts:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Health: `http://localhost:8080/actuator/health`

Expected health response:

```json
{
  "status": "UP"
}
```

`/actuator/health` is intentionally public and should not require a token.

## 4. Run A First Smoke Test

### Register

```bash
curl -X POST "http://localhost:8080/api/v1/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "demo@example.com",
    "password": "StrongPass123!",
    "fullName": "Demo User"
  }'
```

### Create An Account

Use the returned access token:

```bash
curl -X POST "http://localhost:8080/api/v1/accounts" \
  -H "Authorization: Bearer <accessToken>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Main Account",
    "type": "BANK",
    "currency": "USD",
    "initialBalance": 1000.00
  }'
```

## OCR Setup

OCR depends on Tesseract.

macOS example:

```bash
brew install tesseract
tesseract --version
ls /opt/homebrew/share/tessdata
```

If OCR is enabled and Tesseract is not available, startup validation can fail.

## Useful Commands

Run tests:

```bash
mvn test
```

Run a focused test:

```bash
mvn -Dtest=AuthFlowIntegrationTest test
```

## Next Steps

1. Follow the [Usage Guide](usage-guide.md) for common workflows.
2. Use the [API Reference](api-reference.md) for endpoint conventions.
3. Review [Deployment And Operations](deployment-operations.md) before running outside local development.
