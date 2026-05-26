CREATE TABLE users (
    id            UUID         NOT NULL,
    username      VARCHAR(50)  NOT NULL,
    hashed_password VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_username UNIQUE (username)
);

CREATE TABLE login_sessions (
    id           VARCHAR(255) NOT NULL,
    user_id      UUID         NOT NULL,
    expires_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    last_used_at TIMESTAMP WITH TIME ZONE,
    revoked_at   TIMESTAMP WITH TIME ZONE,
    CONSTRAINT pk_login_sessions PRIMARY KEY (id),
    CONSTRAINT fk_login_sessions_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE categories (
    id            BIGSERIAL    NOT NULL,
    name          VARCHAR(100) NOT NULL,
    budget_amount BIGINT       NOT NULL,
    user_id       UUID         NOT NULL,
    CONSTRAINT pk_categories PRIMARY KEY (id),
    CONSTRAINT uq_categories_user_name UNIQUE (user_id, name),
    CONSTRAINT fk_categories_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE expenses (
    id           BIGSERIAL    NOT NULL,
    title        VARCHAR(255) NOT NULL,
    price        BIGINT       NOT NULL,
    expense_date DATE         NOT NULL,
    category_id  BIGINT,      
    memo         TEXT,
    user_id      UUID         NOT NULL,
    CONSTRAINT pk_expenses PRIMARY KEY (id),
    CONSTRAINT fk_expenses_category FOREIGN KEY (category_id)
        REFERENCES categories(id) ON DELETE SET NULL,
    CONSTRAINT fk_expenses_user FOREIGN KEY (user_id) REFERENCES users(id)
);
