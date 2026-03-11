package cn.gekal.spring.template.presentation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Schema(description = "共通エラーレスポンス")
public class ErrorResponse {

  @Schema(description = "HTTPステータスコード", example = "400")
  private int status;

  @Schema(description = "エラーの種類（RFC 7807準拠のタイトル）", example = "Bad Request")
  private String title;

  @Schema(description = "詳細なエラーメッセージ", example = "Validation failed for object='userRequest'")
  private String detail;

  @Schema(description = "リクエストパス", example = "/api/users")
  private String instance;

  @Schema(description = "エラー発生日時", example = "2023-01-01T00:00:00")
  private LocalDateTime timestamp;

  @Schema(description = "フィールドごとの詳細なバリデーションエラー")
  private List<FieldErrorDetail> errors;

  public ErrorResponse() {}

  public ErrorResponse(int status, String title, String detail, String instance) {
    this.status = status;
    this.title = title;
    this.detail = detail;
    this.instance = instance;
    this.timestamp = LocalDateTime.now();
  }

  public ErrorResponse(
      int status, String title, String detail, String instance, List<FieldErrorDetail> errors) {
    this(status, title, detail, instance);
    this.errors = errors;
  }

  public static ErrorResponse of(HttpStatus status, String detail, HttpServletRequest request) {
    return new ErrorResponse(
        status.value(), status.getReasonPhrase(), detail, request.getRequestURI());
  }

  public static ErrorResponse of(
      HttpStatus status, String detail, HttpServletRequest request, List<FieldErrorDetail> errors) {
    return new ErrorResponse(
        status.value(), status.getReasonPhrase(), detail, request.getRequestURI(), errors);
  }

  public static ResponseEntity<ErrorResponse> toEntity(
      HttpStatus status, String detail, HttpServletRequest request) {
    return ResponseEntity.status(status).body(of(status, detail, request));
  }

  public static ResponseEntity<ErrorResponse> toEntity(
      HttpStatus status, String detail, HttpServletRequest request, List<FieldErrorDetail> errors) {
    return ResponseEntity.status(status).body(of(status, detail, request, errors));
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDetail() {
    return detail;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }

  public String getInstance() {
    return instance;
  }

  public void setInstance(String instance) {
    this.instance = instance;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public List<FieldErrorDetail> getErrors() {
    return errors;
  }

  public void setErrors(List<FieldErrorDetail> errors) {
    this.errors = errors;
  }

  @Schema(description = "フィールドごとのエラー詳細")
  public record FieldErrorDetail(
      @Schema(description = "フィールド名", example = "email") String field,
      @Schema(description = "エラーメッセージ", example = "不正な形式のメールアドレスです") String message) {}
}
