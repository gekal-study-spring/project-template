package cn.gekal.spring.template.presentation.api;

import cn.gekal.spring.template.domain.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "ユーザーレスポンス")
public class UserResponse {

  @Schema(description = "ユーザーID", example = "550e8400-e29b-41d4-a716-446655440000")
  private UUID id;

  @Schema(description = "ユーザー名", example = "john_doe")
  private String username;

  @Schema(description = "メールアドレス", example = "john@example.com")
  private String email;

  @Schema(description = "作成日時", example = "2023-01-01T00:00:00")
  private LocalDateTime createdAt;

  @Schema(description = "更新日時", example = "2023-01-01T00:00:00")
  private LocalDateTime updatedAt;

  public UserResponse() {}

  public UserResponse(User user) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.email = user.getEmail();
    this.createdAt = user.getCreatedAt();
    this.updatedAt = user.getUpdatedAt();
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
