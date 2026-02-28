# Springプロジェクトの構造テンプレート

このプロジェクトは、Spring Bootを使用したWebアプリケーションの構造テンプレートです。
レイヤードアーキテクチャを採用し、ドメイン駆動設計（DDD）の考え方を取り入れた構成になっています。

## 技術スタック

- **Java**: 21+
- **Framework**: Spring Boot 3.x
- **Build Tool**: Gradle (Multi-module)
- **Database**: PostgreSQL 17
- **Persistence**: MyBatis
- **Migration**: Flyway (別モジュールとして分離)
- **Container**: Docker, Docker Compose

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
│   └── src/main/resources/       # 設定ファイル、MyBatis Mapper XML
├── migration/           # データベースマイグレーションモジュール（Flyway）
│   ├── src/main/resources/db/migration/
│   │   ├── schema/               # テーブル定義スクリプト
│   │   └── data/                 # 初期データ・テストデータ（環境別）
├── database/            # データベース初期化用スクリプト・Dockerfile
├── compose.yaml         # Docker Compose設定
├── Dockerfile           # アプリケーション実行用Dockerfile
└── start.sh             # アプリケーション起動スクリプト
```

## セットアップと起動方法

### 前提条件

- Docker / Docker Compose がインストールされていること
- Java 21 がインストールされていること

### 開発環境の起動（Docker Compose）

以下のコマンドでデータベースとアプリケーションを起動できます。

```bash
docker compose up --build
```

- **API**: `http://localhost:18080`
- **Health Check**: `http://localhost:18080/actuator/health`
- **Database**: `localhost:15432` (User: `myuser`, Password: `secret`, DB: `template`)

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
