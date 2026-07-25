package cn.gekal.spring.template.presentation;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ErrorResponse {

  private URI type;

  private String title;

  private int status;

  private String detail;

  private String instance;

  private LocalDateTime timestamp;

  private List<FieldErrorDetail> errors;

  public ErrorResponse() {
    this.type = URI.create("about:blank");
    this.timestamp = LocalDateTime.now();
  }

  public ErrorResponse(int status, String title, String detail, String instance) {
    this();
    this.status = status;
    this.title = title;
    this.detail = detail;
    this.instance = instance;
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

  public URI getType() {
    return type;
  }

  public void setType(URI type) {
    this.type = type;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
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

  public record FieldErrorDetail(String field, String message) {}
}
