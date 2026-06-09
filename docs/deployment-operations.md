# Deployment And Operations

## Purpose

This document covers the runtime requirements and operational expectations for running SaveAPenny outside basic local exploration.

## Runtime Dependencies

Required:

- Java 24
- PostgreSQL 16+
- network access between the application and PostgreSQL

Optional but feature-dependent:

- Tesseract for OCR
- OpenAI or OpenRouter for assistant features

## Required Environment Variables

Database and auth:

- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`

Assistant:

- `ASSISTANT_ENABLED`
- `ASSISTANT_AI_PROVIDER`
- `OPENAI_API_KEY` when using OpenAI
- `OPENROUTER_API_KEY` when using OpenRouter

Recommended Docker/PostgreSQL values:

- `POSTGRES_DB`
- `POSTGRES_USER`
- `POSTGRES_PASSWORD`

## Database Behavior

- the application uses Flyway migrations on startup
- JPA validation expects the schema to match the migrations
- the default local database target in `application.yml` is PostgreSQL on `localhost:5432`

## Health And Service Discovery

Health endpoint:

- `GET /actuator/health`

Current behavior:

- exposed over HTTP
- does not require authentication

Useful public operational endpoints:

- `GET /actuator/health`
- `GET /v3/api-docs`
- `GET /swagger-ui.html`

## Docker Compose Notes

The repository includes `docker-compose.yml` for local containerized startup.

The compose file:

- starts PostgreSQL 16
- builds the application container from the local `Dockerfile`
- maps the app to port `8080`
- maps PostgreSQL to port `5432`
- uses an HTTP health check against `/actuator/health`

## OCR Runtime Requirements

OCR features depend on Tesseract and tessdata files being available to the runtime environment.

Expected local macOS tessdata path in current config:

- `/opt/homebrew/share/tessdata`

Container path in `docker-compose.yml`:

- `/usr/share/tesseract-ocr/5/tessdata`

If OCR is enabled but the runtime is missing Tesseract or a valid tessdata path, OCR features can fail and startup validation may fail.

## Assistant Provider Notes

Assistant features are optional and controlled by configuration.

If the assistant is disabled:

- `POST /api/v1/assistant/chat` returns `503 ASSISTANT_DISABLED`

If the assistant is enabled, the selected provider credentials must be valid.

## Security Expectations

- all business endpoints are protected by bearer-token authentication except the public auth, docs, and health routes
- user-owned resources are scoped per authenticated user
- rate limiting is enabled for login and general API traffic

## Operational Smoke Checks

Recommended checks after deployment:

1. `GET /actuator/health` returns `UP`
2. Swagger UI loads
3. user registration and login succeed
4. a protected endpoint works with a valid token
5. database migrations apply cleanly
6. OCR and assistant paths are exercised only if enabled

## Common Runtime Problems

### Health endpoint returns unauthorized

Expected behavior is public access. If it returns `401`, verify the running build includes the current security configuration.

### Database connection failures

Check:

- host and port
- credentials
- PostgreSQL availability
- network access from the application runtime

### Assistant failures

Check:

- `ASSISTANT_ENABLED`
- provider selection
- provider API key
- outbound network access

### OCR failures

Check:

- Tesseract installation
- tessdata path
- OCR enabled state
- file format and size

## Recommended External Access Pattern

For production-like environments, place the service behind a reverse proxy or ingress layer that handles TLS, host routing, and deployment-specific access controls.
