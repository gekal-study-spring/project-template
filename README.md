# Springプロジェクトの構造テンプレート

このプロジェクトは、Spring Bootを使用したモダンなWebアプリケーションの構造テンプレートです。
レイヤードアーキテクチャを採用し、ドメイン駆動設計（DDD）の考え方を取り入れた構成になっています。
マイクロサービスや小〜中規模のバックエンドアプリケーションの開発に最適です。

## 技術スタック

- **Java**: 21
- **Framework**: Spring Boot 4.0.2 (Spring Boot 3.4+相当)
- **Security**: Spring Security (OAuth 2.0 Resource Server / JWT)
- **Persistence**: MyBatis / PostgreSQL
- **Migration**: Flyway (別モジュールとして分離)
- **Resilience**: Spring Retry
- **Documentation**: OpenAPI 3 / Swagger UI
- **Container**: Docker, Docker Compose
- **Quality**: Spotless (Google Java Format)

## アーキテクチャ詳細

本プロジェクトは4層のレイヤードアーキテクチャを採用しています。

### 1. プレゼンテーション層 (`presentation`)
- 外部とのインターフェース（REST API）を担当します。
- **主要な責務**: リクエストの受け取り、バリデーション、レスポンスの返却。
- **主要な要素**: `Controller`, `Request/Response DTO`, `GlobalExceptionHandler`.

### 2. アプリケーション層 (`application`)
- ユースケースを実現するためのワークフローを制御します。
- **主要な責務**: トランザクション管理、ドメインオブジェクトのオーケストレーション。
- **特徴**: データベース接続エラー等に対する再試行（`@Retryable`）を実装しています。

### 3. ドメイン層 (`domain`)
- ビジネスロジックとドメイン知識を保持します。
- **主要な要素**: `Model (Entity)`, `Repository Interface`, `Domain Service`, `Domain Exception`.
- 他のレイヤーに依存しない純粋なビジネスロジックを記述します。

### 4. インフラストラクチャ層 (`infrastructure`)
- 技術的な詳細（DBアクセス、外部API連携、設定等）を実装します。
- **主要な要素**: `Repository Implementation (Datasource)`, `MyBatis Mapper`, `Security Config`.

## ディレクトリ構成

```text
.
├── app/                 # メインアプリケーションモジュール
│   ├── src/main/java/   # ソースコード
│   │   └── cn/gekal/spring/template/
│   │       ├── application/      # アプリケーション層（Service, Command/Query）
│   │       ├── domain/           # ドメイン層（Model, Repository Interface, Domain Service）
│   │       ├── infrastructure/   # インフラストラクチャ層（Repository Implementation, Config）
│   │       └── presentation/     # プレゼンテーション層（API Controller, Request/Response）
│   └── src/main/resources/       # 設定、MyBatis Mapper、JWT公開鍵
├── migration/           # データベースマイグレーションモジュール（Flyway）
│   ├── src/main/resources/db/migration/
│   │   ├── schema/               # テーブル定義スクリプト (DDL)
│   │   └── data/                 # 環境別データ・テストデータ (DML)
├── database/            # データベース初期化用スクリプト・Dockerfile
├── compose.yaml         # Docker Compose設定
├── Dockerfile-app       # アプリケーション用Dockerfile
├── Dockerfile-migration # マイグレーション用Dockerfile
├── generate-jwt.sh      # テスト用JWT生成スクリプト
├── apis.rest            # VS Code REST Client用ファイル
└── upgrade.md           # 更新履歴
```

## セットアップと起動方法

### 前提条件
- Docker / Docker Compose
- Java 21 (ローカル実行時)

### クイックスタート (Docker)
以下のコマンドでデータベース、マイグレーション、アプリケーションがすべて起動します。

```bash
docker compose up --build
```

- **APIベースURL**: `http://localhost:18080`
- **Swagger UI**: `http://localhost:18080/swagger-ui.html`
- **Actuator**: `http://localhost:18080/actuator`
- **DB接続**: `localhost:15432` (User: `myuser`, Password: `secret`, DB: `template`)

## 開発ガイド

### 認証・認可
Spring Securityを使用したJWTによる認可を実装しています。

- **スコープ**: `users::read`, `users::create`, `users::update`, `users::delete` のスコープに基づいてAPIのアクセス制御を行っています。
- **テスト用JWT**: `./generate-jwt.sh` を実行してトークンを取得し、`Authorization: Bearer <token>` ヘッダーで使用してください。

### データベースマイグレーション
`migration` モジュールで一元管理しています。

- **新しいテーブルの追加**: `migration/src/main/resources/db/migration/schema/` に新しい SQL ファイルを追加します。
- **初期データの追加**: `migration/src/main/resources/db/migration/data/dev/` に追加します。

### 耐障害性 (Resilience)
`UserService` には `@Retryable` が設定されており、一時的なデータベース接続エラー時に自動的にリトライを行います。

### AWS API Gateway 統合
本テンプレートは、AWS API Gateway との統合を容易にするための機能を備えています。
- **OpenAPI Extensions**: `OpenApiConfig` により、Swagger UI (v3/api-docs) からエクスポートされる OpenAPI 定義に `x-amazon-apigateway-integration` 拡張が自動的に付与されます。
- **CORS プレフライト**: API Gateway 側での CORS 処理を自動化するための Mock 統合設定も OpenAPI 定義に含まれます。
- **設定項目**: `application.yaml` の `aws.apigateway` セクションで VPC Link や ALB の情報を設定可能です。

### コード規約
Gradleの `Spotless` プラグインを使用しています。コミット前にフォーマットを確認してください。

```bash
./gradlew spotlessApply
```

## テスト

JUnit 5 を使用して各レイヤーのテストを実装しています。

```bash
./gradlew test
```

- **Unit Test**: ドメイン層・アプリケーション層のロジック検証
- **Repository Test**: MyBatisのSQL検証 (`@MybatisTest` を使用)
- **API Test**: `MockMvc` を使用したエンドポイント検証
