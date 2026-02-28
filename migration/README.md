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
java -Dflyway.skipCheckForUpdate=true \
  --enable-native-access=ALL-UNNAMED \
  -jar migration/build/libs/migration-1.0.1-all.jar \
  -url=jdbc:postgresql://localhost:15432/template \
  -user=myuser \
  -password=secret \
  -locations=classpath:db/migration/schema,classpath:db/migration/data/dev \
  migrate
```

## 注意事項

- **バージョニング**: SQL ファイルの名前は Flyway の命名規則（`V<Version>__<Description>.sql`）に従ってください。
- **データ分離**: スキーマ定義（`schema/`）と環境依存のデータ（`data/`）を分けて管理しています。
- **実行順序**: `V1_0_0` はスキーマ定義、`V1_0_1` 以降はデータ投入など、依存関係に注意してバージョン番号を割り振ってください。
