package cn.gekal.spring.template.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cn.gekal.spring.template.application.dto.UserDto;
import cn.gekal.spring.template.domain.model.User;
import cn.gekal.spring.template.domain.repository.UserRepository;
import cn.gekal.spring.template.domain.service.UserDomainService;
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

class UserApplicationServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private UserDomainService userDomainService;

  @InjectMocks private UserApplicationService userApplicationService;

  private UUID userId;
  private User user;
  private UserDto userDto;
  private LocalDateTime now;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    userId = UUID.randomUUID();
    now = LocalDateTime.now();
    user = new User(userId, "testUser", "test@example.com", now, now);
    userDto = new UserDto(userId, "testUser", "test@example.com", now, now);

    when(userDomainService.validateUser(any(User.class))).thenReturn(true);
    when(userDomainService.enrichUser(any(User.class))).thenReturn(user);
  }

  @Test
  void getUserById_WhenUserExists_ReturnsUserDto() {
    // Arrange
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    // Act
    Optional<UserDto> result = userApplicationService.getUserById(userId);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(userId, result.get().getId());
    assertEquals("testUser", result.get().getUsername());
    assertEquals("test@example.com", result.get().getEmail());
    verify(userRepository).findById(userId);
  }

  @Test
  void getUserById_WhenUserDoesNotExist_ReturnsEmptyOptional() {
    // Arrange
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Act
    Optional<UserDto> result = userApplicationService.getUserById(userId);

    // Assert
    assertFalse(result.isPresent());
    verify(userRepository).findById(userId);
  }

  @Test
  void getAllUsers_ReturnsAllUsers() {
    // Arrange
    List<User> users = Arrays.asList(user);
    when(userRepository.findAll()).thenReturn(users);

    // Act
    List<UserDto> result = userApplicationService.getAllUsers();

    // Assert
    assertEquals(1, result.size());
    assertEquals(userId, result.get(0).getId());
    assertEquals("testUser", result.get(0).getUsername());
    assertEquals("test@example.com", result.get(0).getEmail());
    verify(userRepository).findAll();
  }

  @Test
  void createUser_CreatesAndReturnsUser() {
    // Arrange
    when(userRepository.save(any(User.class))).thenReturn(user);

    // Act
    UserDto result = userApplicationService.createUser(userDto);

    // Assert
    assertEquals(userId, result.getId());
    assertEquals("testUser", result.getUsername());
    assertEquals("test@example.com", result.getEmail());
    verify(userDomainService).validateUser(any(User.class));
    verify(userDomainService).enrichUser(any(User.class));
    verify(userRepository).save(any(User.class));
  }

  @Test
  void createUser_WhenInvalidUser_ThrowsException() {
    // Arrange
    when(userDomainService.validateUser(any(User.class))).thenReturn(false);

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> userApplicationService.createUser(userDto));
    verify(userDomainService).validateUser(any(User.class));
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void updateUser_WhenUserExists_UpdatesAndReturnsUser() {
    // Arrange
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class))).thenReturn(user);

    // Act
    UserDto result = userApplicationService.updateUser(userId, userDto);

    // Assert
    assertEquals(userId, result.getId());
    assertEquals("testUser", result.getUsername());
    assertEquals("test@example.com", result.getEmail());
    verify(userRepository).findById(userId);
    verify(userDomainService).validateUser(any(User.class));
    verify(userDomainService).enrichUser(any(User.class));
    verify(userRepository).save(any(User.class));
  }

  @Test
  void updateUser_WhenUserDoesNotExist_ThrowsException() {
    // Arrange
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        IllegalArgumentException.class, () -> userApplicationService.updateUser(userId, userDto));
    verify(userRepository).findById(userId);
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void updateUser_WhenInvalidUser_ThrowsException() {
    // Arrange
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userDomainService.validateUser(any(User.class))).thenReturn(false);

    // Act & Assert
    assertThrows(
        IllegalArgumentException.class, () -> userApplicationService.updateUser(userId, userDto));
    verify(userRepository).findById(userId);
    verify(userDomainService).validateUser(any(User.class));
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void deleteUser_WhenUserExists_DeletesUser() {
    // Arrange
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    doNothing().when(userRepository).deleteById(userId);

    // Act
    userApplicationService.deleteUser(userId);

    // Assert
    verify(userRepository).findById(userId);
    verify(userRepository).deleteById(userId);
  }

  @Test
  void deleteUser_WhenUserDoesNotExist_ThrowsException() {
    // Arrange
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> userApplicationService.deleteUser(userId));
    verify(userRepository).findById(userId);
    verify(userRepository, never()).deleteById(any(UUID.class));
  }
}
