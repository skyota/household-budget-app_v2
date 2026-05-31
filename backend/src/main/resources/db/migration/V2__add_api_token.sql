ALTER TABLE users ADD COLUMN api_token VARCHAR(64) UNIQUE;
-- gen_random_uuid() はPostgreSQL 13以降で標準搭載（拡張不要）
-- ハイフンを除いた2つのUUIDを結合して64文字のトークンを生成
UPDATE users SET api_token = replace(gen_random_uuid()::text, '-', '') || replace(gen_random_uuid()::text, '-', '');
ALTER TABLE users ALTER COLUMN api_token SET NOT NULL;
