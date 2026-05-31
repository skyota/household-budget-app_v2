ALTER TABLE users ADD COLUMN api_token VARCHAR(64) UNIQUE;
UPDATE users SET api_token = encode(gen_random_bytes(32), 'hex');
ALTER TABLE users ALTER COLUMN api_token SET NOT NULL;
