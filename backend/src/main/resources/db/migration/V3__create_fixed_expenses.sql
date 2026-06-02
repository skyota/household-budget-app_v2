CREATE TABLE fixed_expenses (
    id                   BIGSERIAL    NOT NULL,
    title                VARCHAR(30)  NOT NULL,
    price                BIGINT       NOT NULL,
    fixed_expense_date   INTEGER      NOT NULL,
    memo                 VARCHAR(200),
    user_id              UUID         NOT NULL,
    created_at           TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at           TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_fixed_expenses PRIMARY KEY (id),
    CONSTRAINT chk_fixed_expenses_date CHECK (fixed_expense_date BETWEEN 1 AND 31),
    CONSTRAINT fk_fixed_expenses_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
