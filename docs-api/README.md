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

## ファイル構成

| ファイル | 役割 |
| --- | --- |
| `main.tsp` | API 定義(エントリポイント) |
| `tspconfig.yaml` | TypeSpec エミッタ設定(OpenAPI 3.1 を `tsp-output/schema/` に出力) |
| `redocly.yaml` | Redocly CLI の設定(API エイリアス・lint・Redoc 表示オプション) |
| `tsp-output/` | 生成物(gitignore 済み) |
