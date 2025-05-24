package cn.gekal.spring.template.domain.service;

import static org.junit.jupiter.api.Assertions.*;

import cn.gekal.spring.template.domain.model.User;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserDomainServiceTest {

  private UserDomainService userDomainService;
  private User validUser;
  private User invalidUser;

  @BeforeEach
  void setUp() {
    userDomainService = new UserDomainService();

    // Setup a valid user
    validUser = new User();
    validUser.setId(UUID.randomUUID());
    validUser.setUsername("testuser");
    validUser.setEmail("test@example.com");
    validUser.setCreatedAt(LocalDateTime.now());
    validUser.setUpdatedAt(LocalDateTime.now());

    // Setup an invalid user
    invalidUser = new User();
    invalidUser.setId(UUID.randomUUID());
    invalidUser.setUsername("te"); // Too short
    invalidUser.setEmail("invalid-email"); // Invalid email
    invalidUser.setCreatedAt(LocalDateTime.now());
    invalidUser.setUpdatedAt(LocalDateTime.now());
  }

  @Test
  void validateUser_withValidUser_shouldReturnTrue() {
    // Act
    boolean result = userDomainService.validateUser(validUser);

    // Assert
    assertTrue(result);
  }

  @Test
  void validateUser_withNullUser_shouldReturnFalse() {
    // Act
    boolean result = userDomainService.validateUser(null);

    // Assert
    assertFalse(result);
  }

  @Test
  void validateUser_withInvalidEmail_shouldReturnFalse() {
    // Arrange
    User userWithInvalidEmail = new User();
    userWithInvalidEmail.setId(UUID.randomUUID());
    userWithInvalidEmail.setUsername("testuser");
    userWithInvalidEmail.setEmail("invalid-email");

    // Act
    boolean result = userDomainService.validateUser(userWithInvalidEmail);

    // Assert
    assertFalse(result);
  }

  @Test
  void validateUser_withShortUsername_shouldReturnFalse() {
    // Arrange
    User userWithShortUsername = new User();
    userWithShortUsername.setId(UUID.randomUUID());
    userWithShortUsername.setUsername("te"); // Less than 3 characters
    userWithShortUsername.setEmail("test@example.com");

    // Act
    boolean result = userDomainService.validateUser(userWithShortUsername);

    // Assert
    assertFalse(result);
  }

  @Test
  void validateUser_withNullUsername_shouldReturnFalse() {
    // Arrange
    User userWithNullUsername = new User();
    userWithNullUsername.setId(UUID.randomUUID());
    userWithNullUsername.setUsername(null);
    userWithNullUsername.setEmail("test@example.com");

    // Act
    boolean result = userDomainService.validateUser(userWithNullUsername);

    // Assert
    assertFalse(result);
  }

  @Test
  void validateUser_withNullEmail_shouldReturnFalse() {
    // Arrange
    User userWithNullEmail = new User();
    userWithNullEmail.setId(UUID.randomUUID());
    userWithNullEmail.setUsername("testuser");
    userWithNullEmail.setEmail(null);

    // Act
    boolean result = userDomainService.validateUser(userWithNullEmail);

    // Assert
    assertFalse(result);
  }

  @Test
  void enrichUser_shouldReturnSameUser() {
    // Act
    User result = userDomainService.enrichUser(validUser);

    // Assert
    assertSame(validUser, result);
  }
}
