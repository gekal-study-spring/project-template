import "@typespec/openapi";

using OpenAPI;

// ---------------------------------------------------------------------------
// AWS API Gateway integration
//
// Attaches `x-amazon-apigateway-integration` (VPC Link / http_proxy) to every
// operation so the generated OpenAPI can be imported into API Gateway.
// Mirrors app OpenApiConfig#albIntegrationCustomizer.
//
// Replace the placeholders below with your environment's values, or template
// them at deploy time before importing into API Gateway.
// ---------------------------------------------------------------------------

namespace UserService;

const vpcLinkId = "<vpc-link-id>";
const albBaseUri = "https://alb.internal.example.com";

// Integration for the collection path: /api/users
const usersIntegration = #{
  type: "http_proxy",
  connectionType: "VPC_LINK",
  connectionId: vpcLinkId,
  httpMethod: "ANY",
  uri: "${albBaseUri}/api/users",
  passthroughBehavior: "when_no_match",
};

// Integration for the item path: /api/users/{id}
const userItemIntegration = #{
  type: "http_proxy",
  connectionType: "VPC_LINK",
  connectionId: vpcLinkId,
  httpMethod: "ANY",
  uri: "${albBaseUri}/api/users/{id}",
  passthroughBehavior: "when_no_match",
};

@@extension(Users.getAllUsers, "x-amazon-apigateway-integration", usersIntegration);
@@extension(Users.createUser, "x-amazon-apigateway-integration", usersIntegration);
@@extension(Users.getUserById, "x-amazon-apigateway-integration", userItemIntegration);
@@extension(Users.updateUser, "x-amazon-apigateway-integration", userItemIntegration);
@@extension(Users.deleteUser, "x-amazon-apigateway-integration", userItemIntegration);
