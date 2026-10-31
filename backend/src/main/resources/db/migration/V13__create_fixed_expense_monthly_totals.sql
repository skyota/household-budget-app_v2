CREATE TABLE fixed_expense_monthly_totals (
    user_id UUID NOT NULL REFERENCES users(id),
    year INTEGER NOT NULL,
    month INTEGER NOT NULL,
    total BIGINT NOT NULL,
    CONSTRAINT pk_fixed_expense_monthly_totals PRIMARY KEY (user_id, year, month),
    CONSTRAINT ck_fixed_expense_monthly_totals_month CHECK (month BETWEEN 1 AND 12)
);
