# MCP Roadmap

## Goal

Evolve the current assistant implementation into an assistant-internal MCP-based tool platform under `com.saveapenny.mcp`, without breaking the existing `/api/v1/assistant/chat` flow.

## Current Baseline

The project already has the right starting pieces:

- Spring Boot backend
- Spring AI dependency and assistant configuration
- authenticated assistant endpoint at `/api/v1/assistant/chat`
- in-process Spring AI tools for reports, budgets, and transactions
- strong business modules that can become MCP tools later

Relevant current files:

- `src/main/java/com/saveapenny/assistant/service/impl/AssistantServiceImpl.java`
- `src/main/java/com/saveapenny/mcp/adapter/springai/SpringAiMcpToolAdapter.java`
- `src/main/resources/application.yml`
- `pom.xml`

Current flow:

1. Request reaches `/api/v1/assistant/chat`.
2. `AssistantServiceImpl` builds prompt and history.
3. Spring AI invokes in-process `@Tool` methods.
4. Tool classes call domain services directly.
5. Authenticated user context is passed through `AssistantToolContextHolder`.

This is good for the current MVP, but it is still an app-internal tool model, not an MCP platform.

## Target Direction

Treat MCP as an internal shared tool platform below the assistant, not as a public server or replacement for the assistant.

Target architecture:

1. Keep domain logic in existing modules.
2. Introduce a dedicated MCP tool layer under `com.saveapenny.mcp`.
3. Expose that tool layer through Spring AI adapters for `/api/v1/assistant/chat`.
4. Make auth, validation, observability, and audit part of tool execution.

## Recommended Package Structure

Use `com.saveapenny.mcp` as the root package.

Suggested structure:

- `com.saveapenny.mcp.core`
- `com.saveapenny.mcp.definition`
- `com.saveapenny.mcp.execution`
- `com.saveapenny.mcp.security`
- `com.saveapenny.mcp.registry`
- `com.saveapenny.mcp.dto`
- `com.saveapenny.mcp.adapter.springai`
- `com.saveapenny.mcp.report`
- `com.saveapenny.mcp.budget`
- `com.saveapenny.mcp.transaction`
- `com.saveapenny.mcp.account`
- `com.saveapenny.mcp.imports`
- `com.saveapenny.mcp.ocr`
- `com.saveapenny.mcp.notification`
- `com.saveapenny.mcp.automation`
- `com.saveapenny.mcp.audit`

## Phase 1: Extract a Reusable Tool Layer [COMPLETED]

Goal: decouple tool logic from Spring AI-specific classes.

Project changes:

- create `com.saveapenny.mcp` package tree
- move away from tool classes that only return free-form strings
- introduce tool handlers that return structured results
- keep existing business services as the source of truth

What should be implemented:

- a common tool contract
- tool metadata model
- tool execution context model
- tool registry
- request and response DTOs for each tool
- first shared tool handlers for:
  - monthly summary
  - top spending categories
  - monthly budget status
  - recent transactions

Recommended abstractions:

- `ToolDefinition`
- `ToolExecutionContext`
- `ToolHandler<I, O>`
- `ToolResult<T>`
- `ToolRegistry`

Why first:

- the current assistant tools are tightly coupled to Spring AI annotations and string formatting
- MCP becomes straightforward only after tools are reusable outside chat prompt execution

## Phase 2: Standardize Tool Contracts [COMPLETED]

Goal: make tools stable and machine-consumable.

Project changes:

- define every tool with a stable name and explicit contract
- return typed results from core handlers
- keep presentation formatting in adapters only

What should be implemented:

- tool names and descriptions
- JSON-style input schema model
- JSON-style output schema model
- validation layer
- standard tool error model

Recommended error codes:

- `UNAUTHORIZED`
- `NOT_FOUND`
- `VALIDATION_ERROR`
- `FEATURE_DISABLED`
- `RATE_LIMITED`
- `TOOL_EXECUTION_FAILED`

Why:

- MCP consumers need explicit structure
- future AI agents should not depend on parsing narrative strings

## Phase 3: Add Assistant-Internal MCP Invocation Layer [COMPLETED]

Goal: expose the shared tool layer through internal invocation and registry-backed execution for the assistant.

Project changes:

- keep registry-backed invocation inside the application
- do not expose public MCP transport endpoints for now

What should be implemented:

- internal tool registry support
- tool invocation handling
- auth propagation into `ToolExecutionContext`

Recommendation:

- keep this internal to the assistant module

Why:

- the current app already owns authentication, services, persistence, and business rules
- public MCP transport is not needed for the current scope

## Phase 4: Keep Assistant Support Through an Adapter [COMPLETED]

Goal: preserve the current `/api/v1/assistant/chat` feature while switching its tool backend.

Project changes:

- keep `AssistantServiceImpl` as the chat orchestration layer
- replace direct dependence on current `assistant.tool` classes with adapter-backed MCP tool handlers

What should be implemented:

- `com.saveapenny.mcp.adapter.springai`
- adapter classes that expose shared MCP tools to Spring AI
- formatting layer for assistant-facing string output where needed
- migration of current tool classes so they delegate instead of owning logic

Result:

- one shared tool implementation supports both assistant chat and MCP clients

Current adjusted result:

- one shared internal MCP tool implementation supports assistant chat through the Spring AI adapter

## Phase 5: Expand the Tool Catalog

Goal: support current and future AI features through a curated set of tools.

Best first tools based on the current backend:

### Read-only insight tools

- monthly summary
- category spending
- cash flow
- net worth
- monthly budget status
- account balances
- recent transactions
- filtered transaction search
- unread notification count
- recurring transaction overview
- audit lookup

### Workflow and status tools

- import preview status
- import confirm status
- OCR job status
- notification list
- recurring transaction due items

### Write tools after safeguards

- create transaction
- create transfer
- create/update budget
- create recurring transaction
- mark notifications as read
- confirm import

Priority order:

1. read-only financial insight tools
2. workflow and status tools
3. controlled write tools
4. multi-step orchestration tools

## Phase 6: Add Safety Controls for Write Tools

Goal: keep AI-triggered mutations safe and explainable.

What should be implemented:

- tool risk classification:
  - read-only
  - low-risk write
  - high-impact write
- explicit execution policy per tool
- confirmation-required flag for risky writes
- idempotency support where relevant
- dry-run mode for mutating tools
- audit logging for AI-triggered writes
- actor attribution including:
  - user id
  - client type
  - tool name
  - request id or correlation id

Examples of tools that should not execute casually:

- delete or overwrite budget data
- confirm imports
- create transfers
- modify recurring transaction schedules

## Phase 7: Observability and Governance

Goal: make MCP tool usage production-safe.

What should be implemented:

- structured logging for each tool call
- latency metrics per tool
- success/failure counters
- correlation ids across assistant request and tool execution
- rate limiting by user and tool
- audit events for tool invocation
- sensitive data redaction in logs

Why:

- financial tooling must be debuggable and traceable, especially for AI-triggered actions

## Phase 8: AI Context and Memory Tools

Goal: support stronger future assistant behaviors without tightly coupling tools to chat tables.

What should be implemented later:

- session summary retrieval
- user financial preference retrieval
- active alerts retrieval
- unresolved goal retrieval
- recent tool result references

Important note:

- chat persistence can remain useful, but MCP tools should not depend directly on chat message storage as their primary context model

## Testing Strategy

Add tests at three levels.

### Unit tests

- tool handlers
- validators
- schema mapping
- permission checks
- error translation

### Integration tests

- assistant to shared tool layer path
- MCP invocation to shared tool layer path
- auth propagation and enforcement
- failure handling and response mapping

### Contract tests

- tool names remain stable
- input and output schemas remain backward compatible
- MCP responses stay machine-consumable

Regression coverage should include:

- empty datasets
- invalid ranges and parameters
- unauthorized access
- concurrent execution
- idempotent write behavior

## Important Design Decisions

These decisions should be made before implementation starts.

1. Embedded vs separate MCP server
   - Recommendation: embedded first.
2. Structured output vs free-form output
   - Recommendation: structured internally, formatted only in adapters.
3. Direct service exposure vs curated tool layer
   - Recommendation: curated tool layer.
4. Read-only first vs immediate write support
   - Recommendation: read-only first.
5. User context model
   - Recommendation: move from raw `ThreadLocal` dependence to an explicit execution context abstraction.

## Best First Implementation Slice

When implementation starts, the safest and highest-value first slice is:

1. Create the `com.saveapenny.mcp` core abstractions and registry.
2. Refactor the current three assistant tools onto shared structured handlers.
3. Keep `/api/v1/assistant/chat` working through a Spring AI adapter.
4. Add MCP server support for the same three tools.
5. Add tests for auth, schema, and execution flow.
6. Expand from there to reports, accounts, OCR, imports, and notifications.

## What Not To Do

Avoid these implementation mistakes:

- exposing repositories or entities directly as MCP tools
- duplicating logic between Spring AI tools and MCP tools
- keeping core tool outputs as narrative strings only
- letting write tools run without explicit safety policy
- making prompt wording part of business logic
- using chat persistence as the only AI state mechanism

## Practical End State

The desired end state for this codebase is:

- the current assistant still works
- the same backend exposes MCP tools
- new AI features reuse shared tool contracts
- tool execution is authenticated, observable, auditable, and testable
- adding a new AI capability mostly means:
  1. define the tool contract
  2. implement the handler
  3. register the tool
  4. expose it through adapters

## Suggested Next Planning Step

Before coding, the next useful document would be a concrete implementation backlog with:

- exact package/class names under `com.saveapenny.mcp`
- initial three-tool contract definitions
- MCP dependency choice
- assistant migration steps
- test matrix
