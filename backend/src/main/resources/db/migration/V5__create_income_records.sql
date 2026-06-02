CREATE TABLE income_records (
    id           BIGSERIAL    NOT NULL,
    title        VARCHAR(30)  NOT NULL,
    amount       BIGINT       NOT NULL,
    income_date  DATE         NOT NULL,
    memo         VARCHAR(200),
    is_regular   BOOLEAN      NOT NULL DEFAULT false,
    user_id      UUID         NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_income_records PRIMARY KEY (id),
    CONSTRAINT fk_income_records_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
