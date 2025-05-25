#!/bin/bash

# Javaアプリケーション起動スクリプト

# アプリケーションのディレクトリを設定
APP_DIR="$(cd "$(dirname "$0")" && pwd)"

# JARファイルのパス
MIGRATION_JAR="${APP_DIR}/migration.jar"
APP_JAR="${APP_DIR}/app.jar"

# Javaのオプション
JAVA_OPTS="-Xms256m -Xmx512m"

# 起動メッセージ
echo "アプリケーションを起動しています..."
echo "Javaバージョン: $(java -version 2>&1 | head -n 1)"
echo "JARファイル: ${APP_JAR}"

# JARファイルの存在確認
if [ ! -f "${APP_JAR}" ]; then
    echo "エラー: JARファイルが見つかりません: ${APP_JAR}"
    echo "アプリケーションをビルドしてください。例: ./gradlew clean bootJar"
    exit 1
fi

# アプリケーションの起動
echo "データベースのマイグレーション"
java ${JAVA_OPTS} -jar ${MIGRATION_JAR}  \
  -url=${SPRING_DATASOURCE_URL} \
  -user=${SPRING_DATASOURCE_USERNAME} \
  -password=${SPRING_DATASOURCE_PASSWORD} \
  -locations=classpath:db/migration/schema,classpath:db/migration/data/${ENV:-dev} \
  migrate

echo "アプリケーションを起動中..."
java ${JAVA_OPTS} -jar "${APP_JAR}" "$@"
