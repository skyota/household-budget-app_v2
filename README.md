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

---

## 開発フロー

### ブランチ戦略

```
main ← 本番。Vercel が自動デプロイ
 └── dev ← 統合ブランチ。動作確認済みのものだけマージ
      └── feature/xxx / fix/xxx ← 作業ブランチ
```

### 手順

#### 1. 作業前に main と dev が同期していることを確認

```bash
git fetch origin
git log --oneline -5 main
git log --oneline -5 dev
git diff main..dev   # 差分がなければ OK
```

#### 2. dev から作業ブランチを切る

```bash
git checkout dev
git checkout -b feature/xxx   # または fix/xxx
```

#### 3. 実装・コミット

```bash
# 変更を加えてコミット
git add <ファイル>
git commit -m "Add: xxxx"
```

#### 4. dev にマージ

```bash
git checkout dev
git merge feature/xxx
# または GitHub で PR を作成して dev にマージ
```

#### 5. ローカルで動作確認

```bash
docker compose up -d --build
# http://localhost:5173 で確認
```

#### 6. main にマージ → 本番自動デプロイ

```bash
# GitHub で dev → main の PR を作成してマージ
# Vercel が main への push を検知して自動デプロイ
```

> **ルール**: main への直接 push は禁止。必ず dev 経由でマージすること。

---

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
