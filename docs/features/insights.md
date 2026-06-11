# Insights

## Overview

Financial insights are generated observations about user spending patterns, trends, and anomalies. They are an optional feature disabled by default.

## Enabling

```yaml
insight:
  enabled: true
```

Or via environment variable.

## How Insights Work

A daily scheduled job analyzes transaction data for all users and generates insights. Each insight has:

- A category (e.g., spending trend, budget warning)
- A severity level
- A human-readable message
- Supporting data

## Examples

- "You spent 40% more on dining out this month compared to last month"
- "Your grocery spending is consistently 15% above budget"
- "Subscription expenses have increased by $25/month over the last 3 months"

## Insight Lifecycle

| Status | Description |
|--------|-------------|
| Generated | Created by the scheduled job |
| Read | Marked as seen by the user |
| Dismissed | User has dismissed the insight |

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/insights` | List insights (paginated) |
| GET | `/api/v1/insights/{id}` | Get insight details |
| PATCH | `/api/v1/insights/{id}/read` | Mark as read |
| PATCH | `/api/v1/insights/{id}/dismiss` | Dismiss an insight |
| POST | `/api/v1/insights/generate` | Trigger on-demand generation |

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `insight.max-insights-per-generation` | 10 | Max insights per run |
| `insight.deduplication-window-days` | 7 | Suppress duplicates within N days |
| `insight.stddev-threshold` | 3.0 | Standard deviation threshold for anomaly detection |
| `insight.max-amount-ratio` | 0.5 | Max ratio for amount-based comparisons |
| `insight.ai-enhanced` | false | Use AI to generate insight descriptions |
