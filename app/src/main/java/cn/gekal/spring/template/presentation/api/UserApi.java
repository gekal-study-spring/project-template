package cn.gekal.spring.template.presentation.api;

import cn.gekal.spring.template.application.service.UserService;
import cn.gekal.spring.template.domain.model.User;
import cn.gekal.spring.template.domain.model.UserNotFoundException;
import cn.gekal.spring.template.domain.model.UserScope;
import cn.gekal.spring.template.presentation.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User API", description = "ユーザーに関する操作を提供します")
public class UserApi {

  private final UserService userService;

  public UserApi(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/{id}")
  @Operation(summary = "ユーザー取得", description = "IDを指定してユーザーを取得します")
  @ApiResponse(
      responseCode = "200",
      description = "ユーザーが見つかりました",
      content = @Content(schema = @Schema(implementation = UserResponse.class)))
  @ApiResponse(
      responseCode = "404",
      description = "ユーザーが見つかりませんでした",
      content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {

    Optional<UserResponse> userResponse = userService.getUserById(id).map(UserResponse::new);

    return userResponse
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
  }

  @GetMapping
  @PreAuthorize("hasAuthority('" + UserScope.Values.READ + "')")
  @Operation(summary = "ユーザー一覧取得", description = "登録されているすべてのユーザーを取得します")
  @ApiResponse(responseCode = "200", description = "成功")
  public List<UserResponse> getAllUsers() {
    return userService.getAllUsers().stream().map(UserResponse::new).toList();
  }

  @PostMapping
  @PreAuthorize("hasAuthority('" + UserScope.Values.CREATE + "')")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "ユーザー作成", description = "新しいユーザーを作成します")
  @ApiResponse(responseCode = "201", description = "ユーザーが作成されました")
  public UserResponse createUser(@Valid @RequestBody UserRequest userRequest) {
    User user = userService.createUser(userRequest.toUser());
    return new UserResponse(user);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('" + UserScope.Values.UPDATE + "')")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "ユーザー更新", description = "既存のユーザー情報を更新します")
  @ApiResponse(responseCode = "200", description = "ユーザーが更新されました")
  @ApiResponse(
      responseCode = "404",
      description = "ユーザーが見つかりませんでした",
      content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  public UserResponse updateUser(@PathVariable UUID id, @Valid @RequestBody UserRequest userRequest) {
    User user = userService.updateUser(id, userRequest.toUser());
    return new UserResponse(user);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('" + UserScope.Values.DELETE + "')")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "ユーザー削除", description = "ユーザーを削除します")
  @ApiResponse(responseCode = "204", description = "ユーザーが削除されました")
  @ApiResponse(
      responseCode = "404",
      description = "ユーザーが見つかりませんでした",
      content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  public void deleteUser(@PathVariable UUID id) {
    userService.deleteUser(id);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> validateUser(UserNotFoundException e) {

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
  }
}
