CREATE TABLE recurring_transactions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    account_id UUID NOT NULL,
    category_id UUID NOT NULL,
    type VARCHAR(20) NOT NULL,
    amount NUMERIC(19,4) NOT NULL,
    frequency VARCHAR(20) NOT NULL,
    next_run_date DATE NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_recurring_transactions_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_recurring_transactions_account FOREIGN KEY (account_id) REFERENCES accounts (id) ON DELETE RESTRICT,
    CONSTRAINT fk_recurring_transactions_category FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE RESTRICT,
    CONSTRAINT chk_recurring_transactions_type CHECK (type IN ('INCOME', 'EXPENSE')),
    CONSTRAINT chk_recurring_transactions_amount_positive CHECK (amount > 0),
    CONSTRAINT chk_recurring_transactions_frequency CHECK (frequency IN ('DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY'))
);

CREATE INDEX idx_recurring_transactions_user_id ON recurring_transactions (user_id);
CREATE INDEX idx_recurring_transactions_active_next_run_date ON recurring_transactions (active, next_run_date);
CREATE INDEX idx_recurring_transactions_user_active ON recurring_transactions (user_id, active);
