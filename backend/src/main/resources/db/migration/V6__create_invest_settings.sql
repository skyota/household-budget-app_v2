CREATE TABLE invest_settings (
    user_id      UUID    NOT NULL,
    amount       BIGINT  NOT NULL,
    invest_date  INTEGER NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_invest_settings PRIMARY KEY (user_id),
    CONSTRAINT chk_invest_settings_date CHECK (invest_date BETWEEN 1 AND 31),
    CONSTRAINT fk_invest_settings_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
