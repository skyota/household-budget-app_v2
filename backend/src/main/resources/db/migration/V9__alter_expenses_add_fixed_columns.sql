ALTER TABLE expenses
    ADD COLUMN is_fixed          BOOLEAN DEFAULT false NOT NULL,
    ADD COLUMN fixed_expense_id  BIGINT,
    ADD CONSTRAINT fk_expenses_fixed_expense
        FOREIGN KEY (fixed_expense_id) REFERENCES fixed_expenses(id) ON DELETE SET NULL;
