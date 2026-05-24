# Requirements Document

## Introduction
1s-kakeibo 開発の基盤として、Docker Compose による統合開発環境を構築する。開発者は単一コマンドでバックエンド（Spring Boot）・フロントエンド（React）・データベース（PostgreSQL）の3サービスを起動し、ホットリロード付きで即座に開発を開始できる。この spec は後続のすべての spec（auth / expense-management / budget-dashboard）が依存するインフラ基盤を提供する。

## Boundary Context
- **In scope**: Docker Compose による3サービスの起動設定・ネットワーク設定・データ永続化、各サービスの雛形コード配置、ホットリロード設定、環境変数テンプレート（`.env.example`）
- **Out of scope**: Nginx・リバースプロキシ設定、本番向け Dockerfile の最適化、CI/CD 設定、アプリケーションのビジネスロジック（auth / expense-management / budget-dashboard が担当）
- **Adjacent expectations**: auth / expense-management / budget-dashboard の各 spec はこの環境が正常動作することを前提とするが、それらのアプリコードはこの spec のスコープ外

## Requirements

### Requirement 1: 開発環境の一括起動

**Objective:** 開発者として、単一コマンドで全サービスを起動したい。環境構築の手間を排除して開発に集中できるようにするため。

#### Acceptance Criteria
1. When 開発者がプロジェクトルートで `docker compose up -d` を実行したとき、the Docker Infra shall バックエンドサービス・フロントエンドサービス・データベースサービスの3サービスをすべて起動する
2. When 全サービスの起動が完了したとき、the Docker Infra shall バックエンドをホストの 8080 番ポート、フロントエンドを 5173 番ポート、データベースを 5432 番ポートでアクセス可能にする
3. The Docker Infra shall `.env.example` をプロジェクトルートに配置し、全サービスが必要とする環境変数をプレースホルダー値付きで一覧化する
4. When 開発者が `docker compose logs -f` を実行したとき、the Docker Infra shall バックエンド・フロントエンド・データベースそれぞれのログをリアルタイムで表示する

### Requirement 2: バックエンドサービス

**Objective:** 開発者として、Spring Boot バックエンドの雛形がコンテナ上で動作してほしい。バックエンド開発をすぐに開始できるようにするため。

#### Acceptance Criteria
1. When バックエンドサービスが起動したとき、the Backend Service shall `http://localhost:8080` へのリクエストに対してレスポンスを返す
2. While バックエンドコンテナが起動中のとき、the Backend Service shall ホスト上の `backend/` ディレクトリ内のソースコード変更を検知してサービスを自動再起動する
3. The Backend Service shall `backend/` ディレクトリに Java 21 / Spring Boot 3.x / Gradle 構成のプロジェクト雛形を配置する
4. When `docker compose up -d` が完了したとき、the Backend Service shall データベースへの接続が確立された状態になる

### Requirement 3: フロントエンドサービス

**Objective:** 開発者として、React フロントエンドの雛形がコンテナ上で動作してほしい。フロントエンド開発をすぐに開始できるようにするため。

#### Acceptance Criteria
1. When フロントエンドサービスが起動したとき、the Frontend Service shall ブラウザで `http://localhost:5173` にアクセスするとページが表示される
2. While フロントエンドコンテナが起動中のとき、the Frontend Service shall ホスト上の `frontend/` ディレクトリ内のソースコード変更を検知してブラウザを自動更新する
3. The Frontend Service shall `frontend/` ディレクトリに Vite + React + TypeScript + TailwindCSS のプロジェクト雛形を配置する

### Requirement 4: データベースサービスとデータ永続化

**Objective:** 開発者として、PostgreSQL が永続化されたデータで起動してほしい。コンテナを再起動してもデータが失われないようにするため。

#### Acceptance Criteria
1. When データベースサービスが起動したとき、the Database Service shall PostgreSQL がポート 5432 で接続を受け付ける
2. When 開発者が `docker compose down` を実行した後に再度 `docker compose up -d` を実行したとき、the Database Service shall 以前に書き込まれたデータが引き続き存在する
3. While 全サービスが起動中のとき、the Docker Infra shall バックエンドサービスがデータベースサービスに到達できるコンテナネットワークを提供する
