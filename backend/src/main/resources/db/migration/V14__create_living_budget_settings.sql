CREATE TABLE living_budget_settings (
    user_id UUID NOT NULL,
    amount BIGINT NOT NULL DEFAULT 65000,
    CONSTRAINT pk_living_budget_settings PRIMARY KEY (user_id),
    CONSTRAINT ck_living_budget_settings_amount CHECK (amount >= 0),
    CONSTRAINT fk_living_budget_settings_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
