package cn.gekal.spring.template.presentation.api;

import cn.gekal.spring.template.application.service.UserService;
import cn.gekal.spring.template.domain.model.User;
import cn.gekal.spring.template.domain.model.UserNotFoundException;
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
  public List<UserResponse> getAllUsers() {
    return userService.getAllUsers().stream().map(UserResponse::new).toList();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UserResponse createUser(@RequestBody UserRequest userRequest) {
    User user = userService.createUser(userRequest.toUser());
    return new UserResponse(user);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public UserResponse updateUser(@PathVariable UUID id, @RequestBody UserRequest userRequest) {
    User user = userService.updateUser(id, userRequest.toUser());
    return new UserResponse(user);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(@PathVariable UUID id) {
    userService.deleteUser(id);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<String> validateUser(UserNotFoundException e) {

    return ResponseEntity.notFound().build();
  }
}
