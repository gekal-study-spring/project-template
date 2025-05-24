package cn.gekal.spring.template.presentation.api;

import cn.gekal.spring.template.application.service.UserService;
import cn.gekal.spring.template.domain.model.User;
import cn.gekal.spring.template.presentation.api.dto.UserRequest;
import cn.gekal.spring.template.presentation.api.dto.UserResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserApi {

  private final UserService userService;

  public UserApi(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {

    Optional<UserResponse> userResponse = userService.getUserById(id).map(UserResponse::new);

    return userResponse.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @GetMapping
  public ResponseEntity<List<UserResponse>> getAllUsers() {
    List<UserResponse> users = userService.getAllUsers().stream().map(UserResponse::new).toList();
    return ResponseEntity.ok(users);
  }

  @PostMapping
  public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest) {
    User user = userService.createUser(userRequest.toUser());
    return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponse(user));
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserResponse> updateUser(
      @PathVariable UUID id, @RequestBody UserRequest userRequest) {
    try {
      User user = userService.updateUser(id, userRequest.toUser());
      return ResponseEntity.ok(new UserResponse(user));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
    try {
      userService.deleteUser(id);
      return ResponseEntity.noContent().build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.notFound().build();
    }
  }
}
