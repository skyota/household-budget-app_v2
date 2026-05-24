# Implementation Plan

- [ ] 1. Foundation: 環境設定と DB サービスの基盤準備
- [ ] 1.1 `.env.example` と `.gitignore` を作成する
  - プロジェクトルートに `.env.example` を作成し、`POSTGRES_DB`、`POSTGRES_USER`、`POSTGRES_PASSWORD`、`SPRING_DATASOURCE_URL`、`SPRING_DATASOURCE_USERNAME`、`SPRING_DATASOURCE_PASSWORD` の6変数をサンプル値付きで記載する
  - `SPRING_DATASOURCE_URL` には `jdbc:postgresql://db:5432/kakeibo_dev` のようにコンテナ内 DNS 名 `db` を使用する
  - `.gitignore` を作成し、`.env`、`build/`、`.gradle/`、`node_modules/`、IDE 設定ファイルを除外する
  - **完了条件**: `.env.example` がプロジェクトルートに存在し、6つの環境変数がコメント付きで定義されている
  - _Requirements: 1.3_

- [ ] 1.2 `docker-compose.yml` に db サービス・volumes・healthcheck を定義する
  - `docker-compose.yml` をプロジェクトルートに作成し、`db` サービス（`postgres:16` イメージ）を定義する
  - `postgres_data` 名前付きボリュームで `/var/lib/postgresql/data` を永続化する
  - ヘルスチェックを設定する（`pg_isready -U ${POSTGRES_USER} -d ${POSTGRES_DB}`、interval: 10s、timeout: 5s、retries: 5、start_period: 10s）
  - `5432:5432` のポートマッピングと `env_file: .env` を設定する
  - `kakeibo-net` という名前の Docker ネットワークをデフォルトネットワークとして定義し、db サービスを接続する
  - **検証前提**: タスク 1.1 で作成した `.env.example` から `cp .env.example .env` で `.env` を生成しておくこと（`env_file: .env` が必須）
  - **完了条件**: `docker compose up -d db` 実行後、`docker compose ps` で db サービスが `healthy` 状態になる
  - _Requirements: 2.4, 4.1, 4.2, 4.3_

- [ ] 2. バックエンド雛形の構築
- [ ] 2.1 Spring Boot Gradle プロジェクトを初期化する
  - `backend/` ディレクトリに Java 21 / Spring Boot 3.x の Gradle プロジェクトを作成する
  - `build.gradle` に Spring Web、Spring Boot DevTools（`developmentOnly`）、Spring Data JPA、PostgreSQL Driver の依存関係を定義する
  - Gradle Wrapper（`gradlew`、`gradlew.bat`、`gradle/wrapper/gradle-wrapper.properties`）を配置する
  - `settings.gradle` にプロジェクト名 `kakeibo` を設定する
  - `com.kakeibo.KakeiboApplication`（`@SpringBootApplication`）を `src/main/java` に作成する
  - **完了条件**: `backend/` ディレクトリに Gradle プロジェクト構造が存在し、`settings.gradle` にプロジェクト名が設定されている
  - _Requirements: 2.3_

- [ ] 2.2 HealthController と DB 接続設定を実装する
  - `com.kakeibo.presentation.HealthController` を作成し、`GET /api/health` が `{"status": "ok"}` を返すよう実装する（`@RestController`、`@GetMapping`）
  - `src/main/resources/application.properties` に `SPRING_DATASOURCE_*` 環境変数参照、`spring.jpa.hibernate.ddl-auto=none`、`spring.flyway.enabled=false` を設定する
  - **完了条件**: `curl http://localhost:8080/api/health` に対して HTTP 200 と `{"status":"ok"}` が返される（コンテナ統合後に検証）
  - _Requirements: 2.1, 2.3_

- [ ] 2.3 `backend/Dockerfile.dev` を作成する
  - `eclipse-temurin:21-jdk` をベースイメージとした `backend/Dockerfile.dev` を作成する
  - `WORKDIR /app` を設定し、`CMD ["./gradlew", "bootRun"]` で起動するよう定義する（ソースは bind mount で提供されるため `COPY` 不要）
  - **完了条件**: `backend/Dockerfile.dev` が存在し、JDK 21 ベースイメージで `./gradlew bootRun` を実行するよう定義されている
  - _Requirements: 2.2_
  - _Depends: 2.1_

- [ ] 3. (P) フロントエンド雛形の構築
- [ ] 3.1 Vite + React + TypeScript プロジェクトを初期化する
  - `frontend/` ディレクトリに Vite + React + TypeScript テンプレートのプロジェクトを作成する（`npm create vite@latest` 相当）
  - `package.json`、`tsconfig.json`、`tsconfig.node.json`、`index.html`、`src/main.tsx`、`src/App.tsx` を配置する
  - **完了条件**: `frontend/package.json` の `scripts.dev` が `vite` を呼び出している
  - _Requirements: 3.1, 3.3_
  - _Boundary: Frontend プロジェクト構造_

- [ ] 3.2 TailwindCSS を設定して Hello World ページを作成する
  - `tailwindcss`、`postcss`、`autoprefixer` を `devDependencies` に追加する
  - `tailwind.config.js` の `content` に `./src/**/*.{ts,tsx}` を設定する
  - `postcss.config.js` に TailwindCSS と Autoprefixer プラグインを設定する
  - `src/index.css` に `@tailwind base/components/utilities` ディレクティブを記述する
  - `src/App.tsx` を TailwindCSS クラスを使用した「1s-kakeibo」表示ページに更新する
  - **完了条件**: `src/App.tsx` に TailwindCSS クラスが使用されており、ページが正しくスタイリングされた状態でレンダリングされる（コンテナ統合後に確認）
  - _Requirements: 3.1, 3.3_
  - _Boundary: Frontend UI_

- [ ] 3.3 Docker 対応の `vite.config.ts` と `frontend/Dockerfile.dev` を作成する
  - `vite.config.ts` に以下を設定する: `server.host: '0.0.0.0'`、`server.port: 5173`、`server.watch.usePolling: true`（Docker bind mount 対応）、`server.hmr.host: 'localhost'`（HMR WebSocket 接続先固定）
  - `node:20-alpine` をベースイメージとした `frontend/Dockerfile.dev` を作成する（`WORKDIR /app`、`EXPOSE 5173`、`CMD ["sh", "-c", "npm install && npm run dev"]`）
  - **完了条件**: `vite.config.ts` に Docker HMR 設定が記述され、`frontend/Dockerfile.dev` が存在している
  - _Requirements: 3.2_
  - _Boundary: Frontend 設定・コンテナ_

- [ ] 4. Docker Compose 統合設定（バックエンド・フロントエンドサービスの追加）
- [ ] 4.1 `docker-compose.yml` にバックエンドサービスを追加する
  - `backend` サービスを追加する（`build: {context: ./backend, dockerfile: Dockerfile.dev}`、`ports: ["8080:8080"]`）
  - ソースの bind mount（`./backend:/app`）を設定する
  - `depends_on: db: condition: service_healthy` で起動依存関係を設定する
  - `env_file: .env` と `kakeibo-net` ネットワーク接続を設定する
  - **完了条件**: `docker compose up -d` で backend サービスが DB の healthy を確認してから起動し、`docker compose ps` で全3サービスが running 状態になる
  - _Requirements: 1.1, 1.2, 2.2, 2.4_
  - _Depends: 2.3_

- [ ] 4.2 `docker-compose.yml` にフロントエンドサービスを追加する
  - `frontend` サービスを追加する（`build: {context: ./frontend, dockerfile: Dockerfile.dev}`、`ports: ["5173:5173"]`）
  - bind mount（`./frontend:/app`）と `node_modules` 保護用 anonymous volume（`/app/node_modules`）を設定する
  - `kakeibo-net` ネットワーク接続と `networks: default: name: kakeibo-net` の最終定義を追加する
  - **完了条件**: `docker compose up -d` 後にブラウザで `http://localhost:5173` にアクセスすると Hello World ページが表示される
  - _Requirements: 1.1, 1.2, 3.2_
  - _Depends: 3.3_

- [ ] 5. 統合検証と CLAUDE.md 更新
- [ ] 5.1 CLAUDE.md を更新し、全サービスの起動・疎通を確認する
  - `CLAUDE.md` の Build/Run コマンドセクションを Docker Compose コマンドに更新する（`docker compose build`、`up -d`、`down`、`logs -f`）
  - 初回セットアップ手順（`cp .env.example .env`）を CLAUDE.md に追記する
  - `docker compose up -d` → `docker compose logs -f` でリアルタイムログが確認できることを検証する（要件 1.4）
  - `curl http://localhost:8080/api/health` → 200 OK と `{"status":"ok"}` を確認する（要件 2.1）
  - ブラウザで `http://localhost:5173` にアクセスし Hello World ページが表示されることを確認する（要件 3.1）
  - **完了条件**: `docker compose up -d` で3サービスが起動し、backend と frontend の疎通確認が取れた状態で CLAUDE.md が更新されている
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 2.1, 4.1_

- [ ] 5.2 ホットリロード・HMR・データ永続化を確認する
  - `backend/src/main/java` の `.java` ファイルを編集し、Spring DevTools によるコンテキスト再起動ログがコンテナログに出ることを確認する（要件 2.2）
  - `frontend/src/App.tsx` のテキストを変更し、ブラウザが自動更新されることを確認する（要件 3.2）
  - `docker compose down && docker compose up -d` を実行し、`docker exec` で PostgreSQL のデータが保持されていることを確認する（要件 4.2）
  - **完了条件**: ホットリロード・HMR・データ永続化の3つがすべて動作し、docker-infra spec の全受け入れ基準が満たされている
  - _Requirements: 2.2, 3.2, 4.2_
