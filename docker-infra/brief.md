# Brief: docker-infra

## Problem
開発者がローカル環境で Spring Boot バックエンド・React フロントエンド・PostgreSQL を個別にセットアップする手間を排除し、`docker compose up -d` 一発で開発を始められる環境を整える。環境差異によるバグを防ぎ、将来のクラウドデプロイへの橋渡しにもなる。

## Current State
プロジェクトルートに CLAUDE.md のみ存在するグリーンフィールド。アプリコードも Docker 設定も一切ない。

## Desired Outcome
- `docker compose up -d` で Spring Boot（8080）・React（5173）・PostgreSQL（5432）が起動する
- バックエンドとフロントエンドがホットリロードで開発できる
- PostgreSQL のデータはボリュームで永続化される
- バックエンド・フロントエンドそれぞれのプロジェクト雛形（Hello World レベル）が配置されている

## Approach
- `docker-compose.yml` で 3 サービス（backend / frontend / db）を定義
- backend: Gradle + Spring Boot 3.x の雛形、開発時は `./gradlew bootRun` でホットリロード
- frontend: Vite + React + TypeScript + TailwindCSS の雛形、開発時は `npm run dev`
- db: PostgreSQL 16 公式イメージ、名前付きボリュームでデータ永続化
- 環境変数は `.env` ファイルで管理（`.env.example` を提供）

## Scope
- **In**:
  - `docker-compose.yml` の作成
  - `backend/` ディレクトリ: Spring Boot 3.x 雛形（Gradle、Java 21）
  - `frontend/` ディレクトリ: Vite + React + TypeScript + TailwindCSS 雛形
  - PostgreSQL サービス定義 + データボリューム
  - `.env.example` による環境変数テンプレート
  - `CLAUDE.md` の Build/Run コマンド更新
- **Out**:
  - Nginx / リバースプロキシ設定（フェーズ1スコープ外）
  - クラウドデプロイ用の本番 Dockerfile
  - CI/CD 設定

## Boundary Candidates
- Docker Compose サービス定義（backend / frontend / db の分離）
- 各サービスの Dockerfile（開発用）
- 環境変数の管理戦略

## Out of Boundary
- アプリケーションのビジネスロジック（auth / expense-management / budget-dashboard が担う）
- 本番環境向けの最適化（マルチステージビルド等）

## Upstream / Downstream
- **Upstream**: なし（このプロジェクトの起点）
- **Downstream**: auth、expense-management、budget-dashboard すべての spec がこの環境を前提に開発される

## Existing Spec Touchpoints
- **Extends**: なし
- **Adjacent**: なし

## Constraints
- Java 21 / Spring Boot 3.x（Gradle）
- Node.js + Vite + React + TypeScript + TailwindCSS
- PostgreSQL 16
- Docker Compose v2
