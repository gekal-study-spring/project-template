#!/usr/bin/env bash

# Mac用の10年後のタイムスタンプ取得コマンド
EXP_TIME=$(date -v+10y +%s)

# ヘッダーとペイロードを定義
HEADER='{"alg":"RS256","typ":"JWT"}'

PAYLOAD=$(cat <<EOF
{
  "sub": "test-user",
  "scope": "read",
  "exp": $EXP_TIME
}
EOF
)

# Base64URLエンコード用の関数
b64enc() { openssl base64 -e -A | tr '+/' '-_' | tr -d '='; }

# エンコードして署名を作成し、JWTを出力
H=$(echo -n "$HEADER" | b64enc)
P=$(echo -n "$PAYLOAD" | b64enc)
S=$(echo -n "$H.$P" | openssl dgst -sha256 -binary -sign ./app/src/main/resources/jwt/private-key.pem | b64enc)

export TEST_JWT="$H.$P.$S"

echo "=== 10年後に期限切れになるJWT ==="
echo $TEST_JWT
