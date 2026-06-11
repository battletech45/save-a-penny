# OCR Receipt Processing

## Overview

OCR (Optical Character Recognition) extracts text from receipt and document images. It is an optional feature disabled by default.

## Enabling OCR

Set the following in `application.yml`:

```yaml
ocr:
  enabled: true
  tessdata-path: /path/to/tessdata
```

Or set the environment variable equivalent.

## Prerequisites

- Tesseract OCR engine installed on the server
- Tessdata language files available at the configured path
- Supported file formats: PNG, JPEG, PDF
- Maximum file size: 10 MB

## Workflow

```
1. Upload image ──▶ 2. Receive jobId ──▶ 3. Poll job status ──▶ 4. Review results
```

### Step 1: Upload

`POST /api/v1/imports/ocr`

```bash
curl -X POST "http://localhost:8080/api/v1/imports/ocr" \
  -H "Authorization: Bearer <accessToken>" \
  -F "file=@receipt.jpg"
```

Returns HTTP `202 Accepted` with a `jobId`.

### Step 2: Poll Status

`GET /api/v1/imports/ocr/{jobId}`

Returns the job status and extracted data when complete:

| Status | Meaning |
|--------|---------|
| `PENDING` | Job queued, not yet processed |
| `PROCESSING` | OCR is running |
| `COMPLETED` | Text extracted, results available |
| `FAILED` | Processing error |

### Step 3: Review Results

When the job completes, the response includes extracted text and parsed candidates (amounts, dates, merchants).

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/imports/ocr` | Upload a document for OCR |
| GET | `/api/v1/imports/ocr/{jobId}` | Get job status and results |

## Notes

- OCR is CPU-intensive; processing is async with retries
- Default timeout: 30 seconds per job
- Maximum retries: 2
- When disabled, endpoints return `503 OCR_DISABLED`
