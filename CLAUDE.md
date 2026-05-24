# 1s-kakeibo

家計簿アプリ。Spring Boot (Java 21) + Vite/React (TypeScript) + PostgreSQL 16 の構成。
開発環境は Docker Compose で一括管理。

## 初回セットアップ

```bash
cp .env.example .env
docker compose build
```

> **注意**: 初回ビルド時は Gradle の依存解決に数分かかります。

## Build / Run

```bash
# 全サービスをバックグラウンドで起動
docker compose up -d

# 全サービスをビルドしてから起動
docker compose up -d --build

# ログをリアルタイムで確認
docker compose logs -f

# 特定サービスのログ
docker compose logs -f backend
docker compose logs -f frontend
docker compose logs -f db

# 全サービスを停止（ボリュームは保持）
docker compose down

# ボリュームごと削除（DBデータも消える）
docker compose down -v
```

## サービス一覧

| サービス | URL | 説明 |
|---|---|---|
| Backend | http://localhost:8080 | Spring Boot API |
| Frontend | http://localhost:5173 | Vite + React |
| Database | localhost:5432 | PostgreSQL 16 |

## ヘルスチェック

```bash
curl http://localhost:8080/api/health
# → {"status":"ok"}
```

## トラブルシューティング

- **ポート競合**: `lsof -i :8080` / `lsof -i :5173` / `lsof -i :5432` で確認
- **`.env` がない**: `cp .env.example .env` を実行
- **DB接続エラー**: `docker compose ps` で db が `healthy` になっているか確認

## ローカル実行（Docker なし）

Gradle をローカルにインストールし、以下を実行:

```bash
# gradle-wrapper.jar の生成（初回のみ）
cd backend && gradle wrapper

# バックエンド起動
./gradlew bootRun

# フロントエンド起動
cd frontend && npm install && npm run dev
```
