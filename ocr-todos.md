# OCR Remaining TODOs

## 1) Functional and product improvements

- Add export support for OCR-derived transaction candidates if OCR output needs downloadable/reportable flows.
- Improve parser accuracy beyond current heuristic rules (merchant detection, locale-aware dates/amounts, richer category hints).
- Add confidence scoring for parsed transaction candidates and expose it in OCR status responses.

## 2) Reliability and execution model

- Improve retry policy to use typed transient/non-transient failure categories instead of message-based matching.
- Add persistent queue/backpressure strategy for OCR jobs (instead of in-process async executor only).
- Add OCR endpoint rate limiting to protect compute-heavy OCR paths.

## 3) Observability and operations

- Add Prometheus metrics for OCR job counts, retries, failures, and duration histograms.
- Add Grafana dashboards for OCR throughput, latency, and failure rate.
- Add OpenTelemetry tracing for OCR job lifecycle (submit -> run -> complete/fail).

## 4) Environment and CI hardening

- Verify Docker/compose OCR flow end-to-end in target environments (tessdata path, memory/CPU, health behavior).
- Ensure CI installs native Tesseract + tessdata so OCR golden test runs consistently (not skipped).

## 5) API documentation polish

- Add richer Swagger/OpenAPI examples and explicit constraints for OCR multipart inputs and status outputs.
