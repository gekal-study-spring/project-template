#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

if ! command -v trivy >/dev/null 2>&1; then
  echo "Required command not found: trivy" >&2
  echo "Install it on macOS with: brew install trivy" >&2
  exit 2
fi

echo "==> Building application Boot JAR"
"${PROJECT_DIR}/gradlew" \
  :app:clean \
  :app:bootJar \
  --no-daemon

shopt -s nullglob
jars=("${SCRIPT_DIR}"/build/libs/*.jar)

if [ "${#jars[@]}" -ne 1 ]; then
  echo "Expected exactly one Boot JAR, found ${#jars[@]} in app/build/libs" >&2
  exit 2
fi

jar_path="${jars[0]}"

echo "==> Scanning ${jar_path#"${PROJECT_DIR}/"}"
trivy rootfs \
  --scanners vuln \
  --pkg-types library \
  --no-progress \
  --exit-code 1 \
  --severity HIGH,CRITICAL \
  --ignore-unfixed \
  --format table \
  "${SCRIPT_DIR}/build/libs"
