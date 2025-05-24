package cn.gekal.spring.template.interfaces.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import cn.gekal.spring.template.application.dto.UserDto;
import cn.gekal.spring.template.application.service.UserApplicationService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class UserApiTest {

  @Mock private UserApplicationService userApplicationService;

  @InjectMocks private UserApi userApi;

  private UUID userId;
  private UserDto userDto;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();
    userDto = new UserDto(userId, "testUser", "test@example.com", now, now);
  }

  @Test
  void getUserById_WhenUserExists_ReturnsUser() {
    // Arrange
    when(userApplicationService.getUserById(userId)).thenReturn(Optional.of(userDto));

    // Act
    ResponseEntity<UserDto> response = userApi.getUserById(userId);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(userDto, response.getBody());
    verify(userApplicationService).getUserById(userId);
  }

  @Test
  void getUserById_WhenUserDoesNotExist_ReturnsNotFound() {
    // Arrange
    when(userApplicationService.getUserById(userId)).thenReturn(Optional.empty());

    // Act
    ResponseEntity<UserDto> response = userApi.getUserById(userId);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNull(response.getBody());
    verify(userApplicationService).getUserById(userId);
  }

  @Test
  void getAllUsers_ReturnsAllUsers() {
    // Arrange
    List<UserDto> users = Arrays.asList(userDto);
    when(userApplicationService.getAllUsers()).thenReturn(users);

    // Act
    ResponseEntity<List<UserDto>> response = userApi.getAllUsers();

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(users, response.getBody());
    verify(userApplicationService).getAllUsers();
  }

  @Test
  void createUser_ReturnsCreatedUser() {
    // Arrange
    when(userApplicationService.createUser(userDto)).thenReturn(userDto);

    // Act
    ResponseEntity<UserDto> response = userApi.createUser(userDto);

    // Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(userDto, response.getBody());
    verify(userApplicationService).createUser(userDto);
  }

  @Test
  void updateUser_WhenUserExists_ReturnsUpdatedUser() {
    // Arrange
    when(userApplicationService.updateUser(userId, userDto)).thenReturn(userDto);

    // Act
    ResponseEntity<UserDto> response = userApi.updateUser(userId, userDto);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(userDto, response.getBody());
    verify(userApplicationService).updateUser(userId, userDto);
  }

  @Test
  void updateUser_WhenUserDoesNotExist_ReturnsNotFound() {
    // Arrange
    when(userApplicationService.updateUser(userId, userDto))
        .thenThrow(new IllegalArgumentException("User not found"));

    // Act
    ResponseEntity<UserDto> response = userApi.updateUser(userId, userDto);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNull(response.getBody());
    verify(userApplicationService).updateUser(userId, userDto);
  }

  @Test
  void deleteUser_WhenUserExists_ReturnsNoContent() {
    // Arrange
    doNothing().when(userApplicationService).deleteUser(userId);

    // Act
    ResponseEntity<Void> response = userApi.deleteUser(userId);

    // Assert
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(userApplicationService).deleteUser(userId);
  }

  @Test
  void deleteUser_WhenUserDoesNotExist_ReturnsNotFound() {
    // Arrange
    doThrow(new IllegalArgumentException("User not found"))
        .when(userApplicationService)
        .deleteUser(userId);

    // Act
    ResponseEntity<Void> response = userApi.deleteUser(userId);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    verify(userApplicationService).deleteUser(userId);
  }
}
