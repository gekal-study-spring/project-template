# マイグレーションのプロジェクト

## ローカルのデータ構築

```shell
./gradlew clean migration:jar migration:flywayMigrate
```

```shell
./gradlew migration:clean migration:shadowJar

java -jar migration/build/libs/migration-1.0.1-all.jar  \
  -url=jdbc:postgresql://localhost:15432/template \
  -user=myuser \
  -password=secret \
  -locations=classpath:db/migration/schema,classpath:db/migration/data/dev \
  migrate
```
