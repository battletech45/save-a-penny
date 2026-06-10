CREATE TABLE recurring_execution_history (
    id UUID PRIMARY KEY,
    recurring_transaction_id UUID NOT NULL,
    user_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL,
    scheduled_date DATE NOT NULL,
    executed_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    transaction_id UUID,
    failure_reason TEXT,
    CONSTRAINT fk_recurring_execution_history_recurring_transaction
        FOREIGN KEY (recurring_transaction_id) REFERENCES recurring_transactions (id) ON DELETE CASCADE,
    CONSTRAINT fk_recurring_execution_history_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uq_recurring_execution_history_run
        UNIQUE (recurring_transaction_id, scheduled_date)
);

CREATE INDEX idx_recurring_execution_history_recurring_transaction
    ON recurring_execution_history (recurring_transaction_id);
CREATE INDEX idx_recurring_execution_history_user
    ON recurring_execution_history (user_id);

CREATE TABLE net_worth_snapshots (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    snapshot_date DATE NOT NULL,
    total_assets NUMERIC(19,4) NOT NULL DEFAULT 0,
    total_liabilities NUMERIC(19,4) NOT NULL DEFAULT 0,
    net_worth NUMERIC(19,4) NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_net_worth_snapshots_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uq_net_worth_snapshots_user_date
        UNIQUE (user_id, snapshot_date)
);

CREATE INDEX idx_net_worth_snapshots_user_date
    ON net_worth_snapshots (user_id, snapshot_date);
