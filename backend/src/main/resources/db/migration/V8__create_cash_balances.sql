CREATE TABLE cash_balances (
    id            BIGSERIAL   NOT NULL,
    amount        BIGINT      NOT NULL,
    balance_date  DATE        NOT NULL,
    balance_type  VARCHAR(20) NOT NULL,
    memo          VARCHAR(200),
    user_id       UUID        NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_cash_balances PRIMARY KEY (id),
    CONSTRAINT chk_cash_balances_type CHECK (balance_type IN ('INITIAL', 'ADJUSTMENT')),
    CONSTRAINT fk_cash_balances_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
