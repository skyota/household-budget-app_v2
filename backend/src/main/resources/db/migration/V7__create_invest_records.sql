CREATE TABLE invest_records (
    id           BIGSERIAL   NOT NULL,
    amount       BIGINT      NOT NULL,
    invest_date  DATE        NOT NULL,
    invest_type  VARCHAR(20) NOT NULL,
    memo         VARCHAR(200),
    user_id      UUID        NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_invest_records PRIMARY KEY (id),
    CONSTRAINT chk_invest_records_type CHECK (invest_type IN ('INITIAL', 'REGULAR', 'SPOT')),
    CONSTRAINT fk_invest_records_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
