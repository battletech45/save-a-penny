ALTER TABLE recurring_transactions
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    ADD COLUMN name VARCHAR(150),
    ADD COLUMN description TEXT,
    ADD COLUMN start_date DATE,
    ADD COLUMN end_date DATE,
    ADD COLUMN last_run_at TIMESTAMPTZ,
    ADD COLUMN classification VARCHAR(30);
