package cn.gekal.spring.template.presentation;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "共通エラーレスポンス")
public class ErrorResponse {

  @Schema(description = "エラーメッセージ", example = "An unexpected error occurred")
  private String message;

  public ErrorResponse() {}

  public ErrorResponse(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
