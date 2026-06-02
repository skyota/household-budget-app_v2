CREATE TABLE income_settings (
    id                BIGSERIAL    NOT NULL,
    title             VARCHAR(30)  NOT NULL,
    amount            BIGINT       NOT NULL,
    income_date       INTEGER      NOT NULL,
    memo              VARCHAR(200),
    is_auto_generate  BOOLEAN      NOT NULL DEFAULT true,
    user_id           UUID         NOT NULL,
    created_at        TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_income_settings PRIMARY KEY (id),
    CONSTRAINT chk_income_settings_date CHECK (income_date BETWEEN 1 AND 31),
    CONSTRAINT fk_income_settings_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
