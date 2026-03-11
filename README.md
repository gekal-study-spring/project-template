# Springプロジェクトの構造テンプレート

このプロジェクトは、Spring Bootを使用したWebアプリケーションの構造テンプレートです。
レイヤードアーキテクチャを採用し、ドメイン駆動設計（DDD）の考え方を取り入れた構成になっています。

## 技術スタック

- **Java**: 21+
- **Framework**: Spring Boot 3.x
- **Security**: Spring Security (OAuth 2.0 Resource Server / JWT)
- **Build Tool**: Gradle (Multi-module)
- **Database**: PostgreSQL 17
- **Persistence**: MyBatis
- **Migration**: Flyway (別モジュールとして分離)
- **Container**: Docker, Docker Compose
- **API Documentation**: OpenAPI 3 / Swagger UI

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
│   │   ├── schema/               # テーブル定義スクリプト
│   │   └── data/                 # 初期データ・テストデータ（環境別）
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

- Docker / Docker Compose がインストールされていること
- Java 21 がインストールされていること (ローカル実行時)

### 開発環境の起動（Docker Compose）

以下のコマンドでデータベースとアプリケーションを起動できます。

```bash
docker compose up --build
```

- **API**: `http://localhost:18080`
- **Swagger UI**: `http://localhost:18080/swagger-ui.html`
- **Health Check**: `http://localhost:18080/actuator/health`
- **Database**: `localhost:15432` (User: `myuser`, Password: `secret`, DB: `template`)

### 認証・認可

このテンプレートは、Spring Security を使用した JWT による認証をサポートしています。

- **公開鍵/秘密鍵**: `app/src/main/resources/jwt/` に配置されています。
- **JWTの生成**: `generate-jwt.sh` を使用して、テスト用の JWT を生成できます。
  ```bash
  ./generate-jwt.sh
  ```
  生成されたトークンを `Authorization: Bearer <token>` ヘッダーにセットして API を呼び出してください。

### ローカルでの実行

1. **データベースの起動**
   ```bash
   docker compose up postgres -d
   ```

2. **マイグレーションの実行**
   ```bash
   ./gradlew migration:flywayMigrate
   ```

3. **アプリケーションの起動**
   ```bash
   ./gradlew app:bootRun
   ```

## 開発フロー

### データベースマイグレーション

テーブル構成の変更やデータの追加は `migration` モジュールの SQL ファイルを編集して行います。

- `migration/src/main/resources/db/migration/schema/`: DDL (テーブル定義)
- `migration/src/main/resources/db/migration/data/`: DML (環境別データ)

マイグレーションの詳細については `migration/README.md` を参照してください。

### APIのテスト

`apis.rest` ファイルを使用して、VS Code の REST Client 等で API の動作確認が可能です。

### 更新履歴・アップグレード

依存関係の更新などは `upgrade.md` に記載されています。
