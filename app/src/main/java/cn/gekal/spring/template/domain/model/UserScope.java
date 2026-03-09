package cn.gekal.spring.template.domain.model;

/** ユーザー操作に関するスコープ（権限）を定義するEnumです。 */
public enum UserScope {
  /** ユーザー参照権限 */
  READ("users::read"),
  /** ユーザー作成権限 */
  CREATE("users::create"),
  /** ユーザー更新権限 */
  UPDATE("users::update"),
  /** ユーザー削除権限 */
  DELETE("users::delete");

  private final String value;

  UserScope(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  /** String値を保持するための定数クラスです。 アノテーションの引数（例：@PreAuthorize）で使用するために定義しています。 */
  public static class Values {
    public static final String READ = "users::read";
    public static final String CREATE = "users::create";
    public static final String UPDATE = "users::update";
    public static final String DELETE = "users::delete";
  }
}
