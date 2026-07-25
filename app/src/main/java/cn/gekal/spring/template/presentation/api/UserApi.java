package cn.gekal.spring.template.presentation.api;

import cn.gekal.spring.template.application.service.UserService;
import cn.gekal.spring.template.domain.model.User;
import cn.gekal.spring.template.domain.model.UserNotFoundException;
import cn.gekal.spring.template.domain.model.UserScope;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserApi {

  private static final Logger log = LoggerFactory.getLogger(UserApi.class);

  private final UserService userService;

  public UserApi(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {

    UserResponse userResponse =
        userService
            .getUserById(id)
            .map(UserResponse::new)
            .orElseThrow(() -> new UserNotFoundException("User not found"));

    return ResponseEntity.ok(userResponse);
  }

  @GetMapping
  @PreAuthorize("hasAuthority('" + UserScope.Values.READ + "')")
  public List<UserResponse> getAllUsers() {
    return userService.getAllUsers().stream().map(UserResponse::new).toList();
  }

  @PostMapping
  @PreAuthorize("hasAuthority('" + UserScope.Values.CREATE + "')")
  @ResponseStatus(HttpStatus.CREATED)
  public UserResponse createUser(@Valid @RequestBody UserRequest userRequest) {
    User user = userService.createUser(userRequest.toUser());
    return new UserResponse(user);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('" + UserScope.Values.UPDATE + "')")
  @ResponseStatus(HttpStatus.OK)
  public UserResponse updateUser(
      @PathVariable UUID id, @Valid @RequestBody UserRequest userRequest) {
    User user = userService.updateUser(id, userRequest.toUser());
    return new UserResponse(user);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('" + UserScope.Values.DELETE + "')")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(@PathVariable UUID id) {
    userService.deleteUser(id);
  }
}
