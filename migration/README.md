# Database Migration モジュール

このモジュールは [Flyway](https://flywaydb.org/) を使用してデータベースのスキーマ管理および環境別データのマイグレーションを行います。

## ディレクトリ構成

```text
src/main/resources/db/migration/
├── schema/               # テーブル定義スクリプト (DDL)
│   └── V1_0_0__init.sql
└── data/                 # 環境別データ・テストデータ (DML)
    ├── dev/              # 開発環境用 (V1_0_1__init_data.sql)
    └── stg/              # ステージング環境用 (V1_0_1__init_data.sql)
```

## 実行方法

### 1. Gradle タスクから実行する

ローカル開発環境でデータベースを最新の状態に更新する場合に使用します。

```bash
# デフォルト(dev)のデータを投入
./gradlew migration:flywayMigrate

# 環境を指定して実行 (例: stg)
ENV=stg ./gradlew migration:flywayMigrate
```

その他の主な Flyway タスク:
- `migration:flywayInfo`: マイグレーションの状態を確認
- `migration:flywayClean`: データベースをクリーン（全テーブル削除）
- `migration:flywayRepair`: チェックサムの不一致などを修復

### 2. Shadow JAR を使用して実行する

Docker 環境や CI/CD パイプラインなどで、Gradle がインストールされていない環境で使用します。

```bash
# JAR ファイルのビルド
./gradlew migration:clean migration:shadowJar

# マイグレーションの実行
# ※ 実際のバージョン番号に合わせて jar ファイル名を指定してください
(
export DATASOURCE_URL=jdbc:postgresql://localhost:15432/template
export DATASOURCE_USERNAME=myuser
export DATASOURCE_PASSWORD=secret
export FLYWAY_CONFIG_FILES="$(pwd)/migration/config/flyway.toml"
export ENV=dev

# -loggers=console / slf4j
java -Dflyway.skipCheckForUpdate=true \
  -jar migration/build/libs/migration-1.0.1-all.jar \
  -environment=${ENV} \
  -loggers=console \
  clean
)
```

接続情報と環境別のマイグレーションパスは `config/flyway.toml` から読み込みます。
実行時に `DATASOURCE_URL`、`DATASOURCE_USERNAME`、`DATASOURCE_PASSWORD`、`FLYWAY_CONFIG_FILES`、`ENV` を環境変数として設定してください。
`ENV` の値は `-environment` パラメータとしてFlywayへ渡されます。
上記のローカル実行では、読みやすいコンソール形式でログを出力します。
Docker環境では、LogbackのLogstash形式で標準出力へ出力します。

## 注意事項

- **バージョニング**: SQL ファイルの名前は Flyway の命名規則（`V<Version>__<Description>.sql`）に従ってください。
- **データ分離**: スキーマ定義（`schema/`）と環境依存のデータ（`data/`）を分けて管理しています。
- **実行順序**: `V1_0_0` はスキーマ定義、`V1_0_1` 以降はデータ投入など、依存関係に注意してバージョン番号を割り振ってください。
