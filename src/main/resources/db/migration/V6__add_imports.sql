CREATE TABLE imports (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    total_rows INTEGER NOT NULL DEFAULT 0,
    imported_rows INTEGER NOT NULL DEFAULT 0,
    failed_rows INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_imports_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT chk_imports_status CHECK (status IN ('PENDING', 'RUNNING', 'COMPLETED', 'FAILED')),
    CONSTRAINT chk_imports_total_rows_non_negative CHECK (total_rows >= 0),
    CONSTRAINT chk_imports_imported_rows_non_negative CHECK (imported_rows >= 0),
    CONSTRAINT chk_imports_failed_rows_non_negative CHECK (failed_rows >= 0),
    CONSTRAINT chk_imports_row_counts_consistent CHECK (imported_rows + failed_rows <= total_rows)
);

CREATE TABLE import_rows (
    id UUID PRIMARY KEY,
    import_id UUID NOT NULL,
    row_number INTEGER NOT NULL,
    raw_data TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    error_message TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_import_rows_import FOREIGN KEY (import_id) REFERENCES imports (id) ON DELETE CASCADE,
    CONSTRAINT chk_import_rows_row_number_positive CHECK (row_number > 0),
    CONSTRAINT chk_import_rows_status CHECK (status IN ('VALID', 'IMPORTED', 'FAILED', 'SKIPPED')),
    CONSTRAINT uq_import_rows_import_row_number UNIQUE (import_id, row_number)
);

CREATE INDEX idx_imports_user_id ON imports (user_id);
CREATE INDEX idx_imports_user_status ON imports (user_id, status);
CREATE INDEX idx_import_rows_import_id ON import_rows (import_id);
CREATE INDEX idx_import_rows_import_status ON import_rows (import_id, status);
