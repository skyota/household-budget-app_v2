# Research & Design Decisions: docker-infra

## Summary
- **Feature**: `docker-infra`
- **Discovery Scope**: New Feature（グリーンフィールド）
- **Key Findings**:
  - Docker bind mount 環境での Spring Boot ホットリロードは IDE によるコンパイルと Spring DevTools の組み合わせが最もシンプル
  - Vite の HMR は Docker bind mount で `usePolling: true` が必須。`hmr.host` をホスト側に固定しないと WebSocket が接続できない
  - `depends_on: condition: service_healthy` + PostgreSQL の `pg_isready` ヘルスチェックが DB 起動依存関係の確立した解法

---

## Research Log

### Spring Boot ホットリロードの Docker 対応

- **Context**: Docker bind mount 環境でソースコード変更をリアルタイムに反映する方法を調査
- **Sources Consulted**: Spring Boot DevTools 公式ドキュメント、Docker bind mount 関連の一般的なパターン
- **Findings**:
  - Spring DevTools はクラスパスの変更（.class ファイル）を監視して Spring コンテキストを再起動する
  - Docker 内で `./gradlew bootRun` を実行した場合、IDE が .java → .class にコンパイルすると DevTools が検知して再起動
  - IDE なしで完全自動にする場合は `./gradlew build --continuous` をバックグラウンドで実行し DevTools と組み合わせる方法がある
- **Implications**: MVP では IDE + DevTools の組み合わせを採用。IDE なしのシナリオは research 記録に留める

### Vite HMR の Docker bind mount 対応

- **Context**: Docker コンテナ内の Vite dev server にホストブラウザから HMR WebSocket が接続できない問題を調査
- **Sources Consulted**: Vite 公式ドキュメント（server.hmr, server.watch）
- **Findings**:
  - `server.host: '0.0.0.0'` でコンテナ外からの接続を受け付ける
  - `server.watch.usePolling: true` で Docker bind mount でのファイル変更検知（inotify が機能しないケース対応）
  - `server.hmr.host: 'localhost'` でブラウザが HMR WebSocket 接続に使う IP を明示指定
- **Implications**: vite.config.ts にこれらを明示的に設定することで Docker 環境での HMR が安定動作する

### node_modules の bind mount 上書き問題

- **Context**: `./frontend:/app` の bind mount がコンテナ内の `node_modules` を空にしてしまう問題
- **Sources Consulted**: Docker 公式ドキュメント（volumes の優先順位）
- **Findings**:
  - bind mount と同じパス上に named または anonymous volume を設定すると volume が優先される
  - `docker-compose.yml` で `- /app/node_modules` と anonymous volume を追加することで解決
- **Implications**: frontend サービスの docker-compose.yml に anonymous volume エントリを追加する

### PostgreSQL の起動依存関係

- **Context**: Spring Boot がデータベース準備前に起動して接続エラーになる問題
- **Sources Consulted**: Docker Compose 公式ドキュメント（depends_on, healthcheck）
- **Findings**:
  - `depends_on: condition: service_healthy` と PostgreSQL の `pg_isready` ヘルスチェックの組み合わせが確立した解法
  - `start_period` を設定することで初期化中の False Negative を防げる
- **Implications**: `pg_isready` コマンドをヘルスチェックに使い、`start_period: 10s` で初回ボリューム初期化の時間を確保する

---

## Architecture Pattern Evaluation

| Option | Description | Strengths | Risks / Limitations | Notes |
|--------|-------------|-----------|---------------------|-------|
| Bind mount + DevTools | IDE compile → DevTools 再起動 | シンプル、追加ツール不要 | IDE 依存、IDE なし環境で機能しない | **採用** |
| Gradle 継続ビルド | `build --continuous` + DevTools | IDE 不要 | Dockerfile の CMD が複雑化（複数プロセス管理） | MVP スコープ外 |
| docker compose watch | Docker 23+ の新機能 | 宣言的で clean | Docker Engine 23+ が必要、学習コスト | 将来の改善候補 |

---

## Design Decisions

### Decision: `frontend/Dockerfile.dev` の CMD 設計

- **Context**: `node_modules` をコンテナ起動時にインストールするか、イメージビルド時にインストールするか
- **Alternatives Considered**:
  1. Dockerfile 内で `COPY package.json` + `RUN npm install` → イメージビルド時にキャッシュ
  2. `CMD ["sh", "-c", "npm install && npm run dev"]` → 起動時にインストール
- **Selected Approach**: 起動時に `npm install && npm run dev` を実行
- **Rationale**: bind mount が `/app` を上書きするため、Dockerfile でコピーしてもビルド後に消える。`node_modules` は anonymous volume で保護されるため2回目以降の `npm install` は差分のみで高速
- **Trade-offs**: 初回起動時のインストール時間が必要。イメージキャッシュによる高速化ができない
- **Follow-up**: 依存が増えて起動が遅くなった場合は Dockerfile 内でのプリインストールを検討

### Decision: Flyway 無効化（docker-infra フェーズ）

- **Context**: `spring.jpa.hibernate.ddl-auto` と Flyway の初期設定
- **Selected Approach**: `spring.flyway.enabled=false`、`spring.jpa.hibernate.ddl-auto=none`
- **Rationale**: docker-infra フェーズではスキーマなし状態で Spring Boot が起動できれば十分。Flyway マイグレーションは auth spec が最初のマイグレーションを追加した時点で有効化する
- **Follow-up**: auth spec 実装時に `spring.flyway.enabled=true` に変更

### Decision: Spring Boot パッケージ構造

- **Context**: DDD + Clean Architecture を意識した初期パッケージ構成
- **Selected Approach**: `com.kakeibo` をルート、`presentation` サブパッケージに Controller を配置
- **Rationale**: 後続 spec（auth / expense-management / budget-dashboard）が domain / application / infrastructure / presentation の4層構造を追加しやすい雛形にする

---

## Risks & Mitigations

- Spring DevTools のリロードが IDE コンパイル依存 → CLAUDE.md に IDE での自動コンパイル設定を案内する
- 初回 Gradle 依存ダウンロードで起動が遅い → CLAUDE.md に初回は数分かかる旨を記載
- `.env` が存在しない場合の起動エラー → CLAUDE.md に `cp .env.example .env` を明示

---

## References

- Vite: server.hmr 設定 — Docker 環境での HMR WebSocket 設定
- Spring Boot DevTools 公式ドキュメント — クラスパス変更監視の動作説明
- Docker Compose depends_on / healthcheck 公式ドキュメント — service_healthy 条件の依存関係制御
