// Post-process the generated OpenAPI to add AWS API Gateway CORS preflight
// (OPTIONS with a mock integration) to every path.
//
// TypeSpec's HTTP library cannot express the OPTIONS verb, so this mirrors the
// CORS block of the app's OpenApiConfig#albIntegrationCustomizer at build time.
//
// Usage: node scripts/apigw-cors.mjs [path/to/openapi.yaml]

import { readFileSync, writeFileSync } from "node:fs";
import { parse, stringify } from "yaml";

const file = process.argv[2] ?? "tsp-output/schema/openapi.yaml";

const corsIntegration = {
  type: "mock",
  requestTemplates: {
    "application/json": '{"statusCode": 200}',
  },
  responses: {
    default: {
      statusCode: "200",
      responseParameters: {
        "method.response.header.Access-Control-Allow-Origin": "'*'",
        "method.response.header.Access-Control-Allow-Methods":
          "'GET,POST,PUT,DELETE,OPTIONS'",
        "method.response.header.Access-Control-Allow-Headers":
          "'Content-Type,Authorization,X-Api-Key'",
      },
    },
  },
};

const doc = parse(readFileSync(file, "utf8"));
let added = 0;

for (const [path, pathItem] of Object.entries(doc.paths ?? {})) {
  if (pathItem.options) continue; // don't clobber an existing OPTIONS
  // e.g. "/api/users/{id}" -> "corsPreflight_api_users_id"
  const operationId = "corsPreflight" + path.replace(/[/{}]+/g, "_").replace(/_+$/, "");
  pathItem.options = {
    operationId,
    summary: "CORS Preflight",
    description: "API GatewayによるCORSプレフライトリクエストの自動応答",
    tags: ["CORS"],
    responses: {
      200: { description: "CORS Preflight Success" },
    },
    security: [], // preflight requests are unauthenticated
    // clone per path so the emitted YAML has no shared anchors/aliases
    "x-amazon-apigateway-integration": structuredClone(corsIntegration),
  };
  added += 1;
}

writeFileSync(file, stringify(doc));
console.log(`apigw-cors: added CORS preflight to ${added} path(s) in ${file}`);
