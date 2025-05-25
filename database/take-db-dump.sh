#!/bin/bash

DATABASE=template

USER=myuser
PASSWORD=secret

EXCLUDED_TABLES=(
)

IGNORED_TABLES_STRING=''
for TABLE in "${EXCLUDED_TABLES[@]}"
do :
  IGNORED_TABLES_STRING+=" --exclude-table-data=${TABLE}"
done

current_dir=$(dirname "$0")

container_tmp_dir="/tmp/$(date '+%Y-%m-%d-%H-%M')"
docker exec -it project-template-postgres sh -c "mkdir -p ${container_tmp_dir}"

export PGPASSWORD=${PASSWORD}

echo "structure"
docker exec -it project-template-postgres sh -c "PGPASSWORD=${PASSWORD} pg_dump -h localhost -p 5432 -U ${USER} -d ${DATABASE} -s -f ${container_tmp_dir}/01.schema.sql"
docker cp project-template-postgres:${container_tmp_dir}/01.schema.sql ${current_dir}/initdb/01.schema.sql

echo "content"
docker exec -it project-template-postgres sh -c "pg_dump -h localhost -p 5432 -U ${USER} -d ${DATABASE} -a --inserts ${IGNORED_TABLES_STRING} -f ${container_tmp_dir}/02.data.sql"
docker cp project-template-postgres:${container_tmp_dir}/02.data.sql ${current_dir}/initdb/02.data.sql
