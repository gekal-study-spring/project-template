import "@typespec/http";
import "@typespec/openapi";
import "./aws.tsp";

using Http;
using OpenAPI;

@service(#{ title: "User API" })
@info(#{ version: "1.0.0" })
@doc("ユーザーに関する操作を提供します。認証は RS256 で署名された JWT を使用します。")
@server("http://localhost:8080", "Local development server")
@useAuth(JwtAuth)
namespace UserService;

/**
 * OAuth2 リソースサーバー (Spring Security) が検証する JWT。
 * RS256 で署名し `Authorization: Bearer <token>` で送信します。
 * 権限は `scope` クレーム (スペース区切り、例: `users::read users::create`) で表現します。
 */
model JwtAuth {
  type: AuthType.http;
  scheme: "bearer";
}

// ---------------------------------------------------------------------------
// Models
// ---------------------------------------------------------------------------

@doc("ユーザーレスポンス")
model User {
  @doc("ユーザーID")
  @format("uuid")
  id: string;

  @doc("ユーザー名")
  username: string;

  @doc("メールアドレス")
  email: string;

  @doc("作成日時")
  createdAt: utcDateTime;

  @doc("更新日時")
  updatedAt: utcDateTime;
}

@doc("ユーザーリクエスト")
model UserRequest {
  @doc("ユーザー名")
  @maxLength(50)
  username: string;

  @doc("メールアドレス")
  @format("email")
  @maxLength(100)
  email: string;
}

@doc("フィールドごとのエラー詳細")
model FieldErrorDetail {
  @doc("フィールド名")
  field: string;

  @doc("エラーメッセージ")
  message: string;
}

@doc("共通エラーレスポンス (RFC 7807 準拠)")
model ProblemDetail {
  @doc("エラーのタイプを識別するURI")
  type: url;

  @doc("エラーの種類 (RFC 7807 準拠のタイトル)")
  title: string;

  @doc("HTTPステータスコード")
  status: int32;

  @doc("詳細なエラーメッセージ")
  detail: string;

  @doc("リクエストパス")
  instance: string;

  @doc("エラー発生日時")
  timestamp: utcDateTime;

  @doc("フィールドごとの詳細なバリデーションエラー")
  errors?: FieldErrorDetail[];
}

@error
@doc("リクエストが不正です")
model BadRequestError {
  @statusCode statusCode: 400;
  @body body: ProblemDetail;
}

@error
@doc("リソースが見つかりませんでした")
model NotFoundError {
  @statusCode statusCode: 404;
  @body body: ProblemDetail;
}

// ---------------------------------------------------------------------------
// Operations
// ---------------------------------------------------------------------------

@route("/api/users")
@tag("User API")
interface Users {
  @get
  @summary("ユーザー取得")
  @doc("IDを指定してユーザーを取得します")
  getUserById(@path id: string): User | NotFoundError;

  @get
  @summary("ユーザー一覧取得")
  @doc("登録されているすべてのユーザーを取得します (スコープ: users::read)")
  getAllUsers(): User[];

  @post
  @summary("ユーザー作成")
  @doc("新しいユーザーを作成します (スコープ: users::create)")
  createUser(@body body: UserRequest): {
    @statusCode statusCode: 201;
    @body user: User;
  } | BadRequestError;

  @put
  @summary("ユーザー更新")
  @doc("既存のユーザー情報を更新します (スコープ: users::update)")
  updateUser(@path id: string, @body body: UserRequest): User | BadRequestError | NotFoundError;

  @delete
  @summary("ユーザー削除")
  @doc("ユーザーを削除します (スコープ: users::delete)")
  deleteUser(@path id: string): {
    @statusCode statusCode: 204;
  } | NotFoundError;
}
