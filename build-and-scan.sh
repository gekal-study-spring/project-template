#!/usr/bin/env bash
#
# GitHub Actions(.github/workflows/trivy-scan.yml)と同じ内容をローカルで実行する。
# Dockerfile-app / Dockerfile-migration をビルドし、Trivy でスキャンする。
#
# 使い方:
#   ./build-and-scan.sh            # 両方のイメージを対象
#   ./build-and-scan.sh app        # app のみ
#   ./build-and-scan.sh migration  # migration のみ
#
# いずれかのイメージで HIGH / CRITICAL の脆弱性(修正パッチありのもの)が
# 検出された場合は exit code 1 で終了します。
#
# 必要なツール: docker, trivy
#   trivy のインストール例 (macOS): brew install trivy
set -uo pipefail
cd "$(dirname "$0")"

# ビルド対象の定義: <名前>:<Dockerfile>:<イメージ名>
ALL_TARGETS=(
  "app:Dockerfile-app:project-template-app"
  "migration:Dockerfile-migration:project-template-migration"
)

# 引数があれば対象を絞り込む
if [ "$#" -gt 0 ]; then
  TARGETS=()
  for arg in "$@"; do
    matched=0
    for t in "${ALL_TARGETS[@]}"; do
      if [ "${t%%:*}" = "${arg}" ]; then
        TARGETS+=("${t}")
        matched=1
      fi
    done
    if [ "${matched}" -eq 0 ]; then
      echo "Unknown target: ${arg} (app | migration)" >&2
      exit 2
    fi
  done
else
  TARGETS=("${ALL_TARGETS[@]}")
fi

# 必要なコマンドの存在チェック
for cmd in docker trivy; do
  if ! command -v "${cmd}" >/dev/null 2>&1; then
    echo "Required command not found: ${cmd}" >&2
    exit 2
  fi
done

failed=0
for entry in "${TARGETS[@]}"; do
  IFS=':' read -r name dockerfile image <<< "${entry}"
  image_tag="${image}:local"

  echo
  echo "==> Building ${image_tag} (from ${dockerfile})"
  docker build -f "${dockerfile}" -t "${image_tag}" . || { failed=1; continue; }

  echo
  echo "==> Scanning ${image_tag}"
  trivy image \
    --exit-code 1 \
    --severity HIGH,CRITICAL \
    --ignore-unfixed \
    --format table \
    "${image_tag}" || failed=1
done

exit "${failed}"
