package cn.gekal.spring.template.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private UserDomainService userDomainService;

  @InjectMocks private UserService userService;

  private User testUser;
  private UUID testId;

  @BeforeEach
  void setUp() {
    testId = UUID.randomUUID();
    testUser = new User();
    testUser.setId(testId);
    testUser.setUsername("testuser");
    testUser.setEmail("test@example.com");
    testUser.setCreatedAt(LocalDateTime.now());
    testUser.setUpdatedAt(LocalDateTime.now());
  }

  @Test
  void getUserById_whenUserExists_shouldReturnUser() {
    // Arrange
    when(userRepository.findById(testId)).thenReturn(Optional.of(testUser));

    // Act
    Optional<User> result = userService.getUserById(testId);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(testUser, result.get());
    verify(userRepository).findById(testId);
  }

  @Test
  void getUserById_whenUserDoesNotExist_shouldReturnEmpty() {
    // Arrange
    when(userRepository.findById(testId)).thenReturn(Optional.empty());

    // Act
    Optional<User> result = userService.getUserById(testId);

    // Assert
    assertFalse(result.isPresent());
    verify(userRepository).findById(testId);
  }

  @Test
  void getAllUsers_shouldReturnAllUsers() {
    // Arrange
    User user1 = new User();
    user1.setId(UUID.randomUUID());
    user1.setUsername("user1");
    user1.setEmail("user1@example.com");

    User user2 = new User();
    user2.setId(UUID.randomUUID());
    user2.setUsername("user2");
    user2.setEmail("user2@example.com");

    List<User> users = Arrays.asList(user1, user2);
    when(userRepository.findAll()).thenReturn(users);

    // Act
    List<User> result = userService.getAllUsers();

    // Assert
    assertEquals(2, result.size());
    assertEquals(users, result);
    verify(userRepository).findAll();
  }

  @Test
  void createUser_withValidUser_shouldSaveAndReturnUser() {
    // Arrange
    User inputUser = new User();
    inputUser.setUsername("newuser");
    inputUser.setEmail("new@example.com");

    when(userDomainService.validateUser(any(User.class))).thenReturn(true);
    when(userDomainService.enrichUser(any(User.class))).thenReturn(inputUser);
    when(userRepository.save(any(User.class))).thenReturn(inputUser);

    // Act
    User result = userService.createUser(inputUser);

    // Assert
    assertNotNull(result);
    assertNotNull(result.getId());
    assertNotNull(result.getCreatedAt());
    assertNotNull(result.getUpdatedAt());
    assertEquals(inputUser.getUsername(), result.getUsername());
    assertEquals(inputUser.getEmail(), result.getEmail());
    verify(userDomainService).validateUser(any(User.class));
    verify(userDomainService).enrichUser(any(User.class));
    verify(userRepository).save(any(User.class));
  }

  @Test
  void createUser_withInvalidUser_shouldThrowException() {
    // Arrange
    User invalidUser = new User();
    invalidUser.setUsername("inv");
    invalidUser.setEmail("invalid-email");

    when(userDomainService.validateUser(any(User.class))).thenReturn(false);

    // Act & Assert
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              userService.createUser(invalidUser);
            });

    assertEquals("Invalid user data", exception.getMessage());
    verify(userDomainService).validateUser(any(User.class));
    verify(userDomainService, never()).enrichUser(any(User.class));
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void updateUser_whenUserExists_shouldUpdateAndReturnUser() {
    // Arrange
    User updateUser = new User();
    updateUser.setUsername("updateduser");
    updateUser.setEmail("updated@example.com");

    when(userRepository.findById(testId)).thenReturn(Optional.of(testUser));
    when(userDomainService.validateUser(any(User.class))).thenReturn(true);
    when(userDomainService.enrichUser(any(User.class))).thenReturn(testUser);
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    // Act
    User result = userService.updateUser(testId, updateUser);

    // Assert
    assertNotNull(result);
    assertEquals(updateUser.getUsername(), result.getUsername());
    assertEquals(updateUser.getEmail(), result.getEmail());
    verify(userRepository).findById(testId);
    verify(userDomainService).validateUser(any(User.class));
    verify(userDomainService).enrichUser(any(User.class));
    verify(userRepository).save(any(User.class));
  }

  @Test
  void updateUser_whenUserDoesNotExist_shouldThrowException() {
    // Arrange
    User updateUser = new User();
    updateUser.setUsername("updateduser");
    updateUser.setEmail("updated@example.com");

    when(userRepository.findById(testId)).thenReturn(Optional.empty());

    // Act & Assert
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              userService.updateUser(testId, updateUser);
            });

    assertEquals("User not found", exception.getMessage());
    verify(userRepository).findById(testId);
    verify(userDomainService, never()).validateUser(any(User.class));
    verify(userDomainService, never()).enrichUser(any(User.class));
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void updateUser_withInvalidUser_shouldThrowException() {
    // Arrange
    User updateUser = new User();
    updateUser.setUsername("inv");
    updateUser.setEmail("invalid-email");

    when(userRepository.findById(testId)).thenReturn(Optional.of(testUser));
    when(userDomainService.validateUser(any(User.class))).thenReturn(false);

    // Act & Assert
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              userService.updateUser(testId, updateUser);
            });

    assertEquals("Invalid user data", exception.getMessage());
    verify(userRepository).findById(testId);
    verify(userDomainService).validateUser(any(User.class));
    verify(userDomainService, never()).enrichUser(any(User.class));
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void deleteUser_whenUserExists_shouldDeleteUser() {
    // Arrange
    when(userRepository.findById(testId)).thenReturn(Optional.of(testUser));
    doNothing().when(userRepository).deleteById(testId);

    // Act
    userService.deleteUser(testId);

    // Assert
    verify(userRepository).findById(testId);
    verify(userRepository).deleteById(testId);
  }

  @Test
  void deleteUser_whenUserDoesNotExist_shouldThrowException() {
    // Arrange
    when(userRepository.findById(testId)).thenReturn(Optional.empty());

    // Act & Assert
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              userService.deleteUser(testId);
            });

    assertEquals("User not found", exception.getMessage());
    verify(userRepository).findById(testId);
    verify(userRepository, never()).deleteById(any(UUID.class));
  }
}
