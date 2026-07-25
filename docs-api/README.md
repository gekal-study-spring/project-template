# docs-api

API documentation written in [TypeSpec](https://typespec.io/) and previewed
live with [Redoc](https://redocly.com/) (via the Redocly CLI).

TypeSpec (`*.tsp`) は OpenAPI 3.1 (`tsp-output/schema/openapi.yaml`) にコンパイルされ、
Redoc がそれを監視してブラウザにリアルタイムで反映します。

## セットアップ

```bash
npm install
```

## リアルタイムプレビュー

```bash
npm run dev
```

- TypeSpec を watch モードでコンパイルし、同時に Redoc プレビューを起動します。
- ブラウザで <http://localhost:8080> を開きます。
- `main.tsp` を保存すると OpenAPI が再生成され、Redoc が自動でリロードします。

> 初回起動時は `redocly preview` が `@redocly/redoc` を npx で取得するため
> ネットワーク接続が必要です(以降はキャッシュされます)。

## そのほかのコマンド

| コマンド | 内容 |
| --- | --- |
| `npm run build` | TypeSpec を一度だけコンパイル |
| `npm run watch:tsp` | TypeSpec を watch コンパイル(Redoc なし) |
| `npm run preview` | Redoc プレビューのみ起動 |
| `npm run lint` | 生成した OpenAPI を Redocly でlint |
| `npm run build-docs` | 静的な Redoc HTML を `tsp-output/redoc.html` に出力 |
| `npm run clean` | `tsp-output/` を削除 |

## AWS API Gateway 連携

生成した OpenAPI は AWS API Gateway へのインポートを想定し、拡張を付与しています
(app の `OpenApiConfig#albIntegrationCustomizer` と同等)。

- **VPC Link 統合** (`x-amazon-apigateway-integration`): `aws.tsp` で各オペレーションに
  augment デコレーターで付与しています。TypeSpec ネイティブなので watch でも即反映されます。
- **CORS プリフライト** (OPTIONS + mock 統合): TypeSpec は OPTIONS を表現できないため、
  `npm run build` の後処理 (`scripts/apigw-cors.mjs`) で各パスに注入します。

環境依存の値は `aws.tsp` の定数を編集するか、デプロイ時に差し替えてください。

```tsp
const vpcLinkId = "<vpc-link-id>";
const albBaseUri = "https://alb.internal.example.com";
```

> `npm run dev`(watch)中は CORS 後処理は走りません。API Gateway 用の完成版を得るには
> `npm run build`(compile + CORS 注入)を実行してください。

## ファイル構成

| ファイル | 役割 |
| --- | --- |
| `main.tsp` | API 定義(エントリポイント) |
| `aws.tsp` | AWS API Gateway VPC Link 統合拡張(augment デコレーター) |
| `scripts/apigw-cors.mjs` | 生成後に CORS プリフライト OPTIONS を注入する後処理 |
| `tspconfig.yaml` | TypeSpec エミッタ設定(OpenAPI 3.1 を `tsp-output/schema/` に出力) |
| `redocly.yaml` | Redocly CLI の設定(API エイリアス・lint・Redoc 表示オプション) |
| `tsp-output/` | 生成物(gitignore 済み) |
