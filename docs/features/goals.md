# Goals Feature Guide

## Overview

SaveAPenny includes goal management and financial simulation endpoints for planning future savings, debt payoff, purchases, retirement, and income targets.

The goal system is designed for repeatable, user-scoped projections. Saved goals, scenarios, and simulation runs can be revisited later instead of relying only on one-time chat output.

## Supported Goal Types

| Goal Type | Use Case |
| --- | --- |
| `SAVINGS` | Reach a target balance by a target date |
| `DEBT_PAYOFF` | Estimate debt payoff timing and payment pressure |
| `PURCHASE` | Save toward a purchase or down payment |
| `RETIREMENT` | Project retirement readiness |
| `INCOME_TARGET` | Estimate growth needed to hit a target income |

## Main Endpoints

Goal management:

- `POST /api/v1/goals`
- `GET /api/v1/goals`
- `GET /api/v1/goals/{goalId}`
- `PATCH /api/v1/goals/{goalId}`
- `DELETE /api/v1/goals/{goalId}`
- `PATCH /api/v1/goals/{goalId}/status`

Scenarios and history:

- `POST /api/v1/goals/{goalId}/scenarios`
- `GET /api/v1/goals/{goalId}/scenarios`
- `GET /api/v1/goals/{goalId}/runs`
- `POST /api/v1/goals/{goalId}/scenarios/compare`
- `POST /api/v1/goals/{goalId}/what-if`

Simulation:

- `POST /api/v1/goals/simulate`
- `POST /api/v1/goals/simulate/draft`
- `POST /api/v1/goals/{goalId}/simulate`

All goal endpoints require `Authorization: Bearer <accessToken>`.

## Typical User Flow

1. create a goal
2. run a simulation
3. save alternate scenarios if needed
4. compare scenarios
5. monitor progress and run history over time

## Create A Goal

Example savings goal:

```json
{
  "type": "SAVINGS",
  "title": "Emergency Fund",
  "targetAmount": 10000,
  "currency": "USD",
  "targetDate": "2027-12-31",
  "inputs": {
    "monthlyContribution": 350,
    "expectedAnnualReturn": 0,
    "startBalance": 1500
  }
}
```

Required top-level fields:

- `type`
- `title`
- `targetAmount`
- `currency`
- `targetDate`
- `inputs`

Optional field:

- `linkedAccountId`

## Draft Simulation

Use draft simulation when you want a projection before saving a goal.

Example request:

```json
{
  "type": "SAVINGS",
  "title": "Emergency Fund",
  "targetAmount": 10000,
  "currency": "USD",
  "targetDate": "2027-12-31",
  "inputs": {
    "monthlyContribution": 350,
    "expectedAnnualReturn": 0,
    "startBalance": 1500
  }
}
```

Endpoint:

- `POST /api/v1/goals/simulate/draft`

## Prompt-Based Simulation

Use prompt simulation when you want the backend to interpret a natural-language request.

Example:

```json
{
  "prompt": "I want to save 10,000 USD by the end of next year."
}
```

Endpoint:

- `POST /api/v1/goals/simulate`

## Existing Goal Simulation

To rerun the currently saved version of a goal:

- `POST /api/v1/goals/{goalId}/simulate`

This is useful after updating transactions, balances, or scenario assumptions.

## Scenarios

Scenarios let you compare alternate assumptions for the same goal.

Example scenario request:

```json
{
  "name": "More aggressive savings",
  "inputs": {
    "monthlyContribution": 500,
    "expectedAnnualReturn": 0,
    "startBalance": 1500
  },
  "isBaseline": false
}
```

Common use cases:

- compare two savings rates
- compare conservative vs optimistic assumptions
- test alternate debt payments
- test different time horizons

## Feasibility Meanings

| Value | Meaning |
| --- | --- |
| `ON_TRACK` | The target appears achievable under current assumptions |
| `TIGHT` | The target may be achievable, but with limited margin |
| `AT_RISK` | The target depends on aggressive assumptions or stronger cash flow |
| `INFEASIBLE` | The current assumptions do not support the target |

## Goal Statuses

| Status | Meaning |
| --- | --- |
| `DRAFT` | Goal exists but is not yet actively tracked |
| `ACTIVE` | Goal is active and can be monitored over time |
| `ACHIEVED` | Goal target has been met |
| `ABANDONED` | Goal is no longer being pursued |

## Common Warnings

Simulations may include warnings such as:

- `MULTI_CURRENCY`
- `MISSING_INCOME_HISTORY`
- `MISSING_LINKED_ACCOUNT`
- `NEGATIVE_CASH_FLOW`
- `LONG_HORIZON`

Warnings do not always block a result, but they indicate lower confidence or important context gaps.

## Important Limits

- simulations are informational and not professional financial advice
- multi-currency situations may return warnings instead of doing currency conversion
- results depend on available historical data and the assumptions supplied

## Recommended Usage Pattern

1. create a realistic baseline goal
2. run an initial simulation
3. create one or two scenarios, not many low-value variants
4. use comparisons to evaluate tradeoffs
5. revisit the goal after major transaction or income changes

## Related Documents

- [Getting Started](../getting-started.md)
- [Usage Guide](../usage-guide.md)
- [API Reference](../api-reference.md)
