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
  private LocalDateTime now;

  @BeforeEach
  void setUp() {
    userDomainService = new UserDomainService();
    now = LocalDateTime.now();
    validUser = new User(UUID.randomUUID(), "testUser", "test@example.com", now, now);
  }

  @Test
  void validateUser_WithValidUser_ReturnsTrue() {
    // Act
    boolean result = userDomainService.validateUser(validUser);

    // Assert
    assertTrue(result);
  }

  @Test
  void validateUser_WithNullUser_ReturnsFalse() {
    // Act
    boolean result = userDomainService.validateUser(null);

    // Assert
    assertFalse(result);
  }

  @Test
  void validateUser_WithNullUsername_ReturnsFalse() {
    // Arrange
    validUser.setUsername(null);

    // Act
    boolean result = userDomainService.validateUser(validUser);

    // Assert
    assertFalse(result);
  }

  @Test
  void validateUser_WithEmptyUsername_ReturnsFalse() {
    // Arrange
    validUser.setUsername("");

    // Act
    boolean result = userDomainService.validateUser(validUser);

    // Assert
    assertFalse(result);
  }

  @Test
  void validateUser_WithShortUsername_ReturnsFalse() {
    // Arrange
    validUser.setUsername("ab"); // Less than 3 characters

    // Act
    boolean result = userDomainService.validateUser(validUser);

    // Assert
    assertFalse(result);
  }

  @Test
  void validateUser_WithNullEmail_ReturnsFalse() {
    // Arrange
    validUser.setEmail(null);

    // Act
    boolean result = userDomainService.validateUser(validUser);

    // Assert
    assertFalse(result);
  }

  @Test
  void validateUser_WithInvalidEmail_NoAtSymbol_ReturnsFalse() {
    // Arrange
    validUser.setEmail("testexample.com");

    // Act
    boolean result = userDomainService.validateUser(validUser);

    // Assert
    assertFalse(result);
  }

  @Test
  void validateUser_WithInvalidEmail_NoDomain_ReturnsFalse() {
    // Arrange
    validUser.setEmail("test@");

    // Act
    boolean result = userDomainService.validateUser(validUser);

    // Assert
    assertFalse(result);
  }

  @Test
  void validateUser_WithInvalidEmail_NoTLD_ReturnsFalse() {
    // Arrange
    validUser.setEmail("test@example");

    // Act
    boolean result = userDomainService.validateUser(validUser);

    // Assert
    assertFalse(result);
  }

  @Test
  void validateUser_WithInvalidEmail_ShortTLD_ReturnsFalse() {
    // Arrange
    validUser.setEmail("test@example.c");

    // Act
    boolean result = userDomainService.validateUser(validUser);

    // Assert
    assertFalse(result);
  }

  @Test
  void enrichUser_ReturnsUnmodifiedUser() {
    // Act
    User result = userDomainService.enrichUser(validUser);

    // Assert
    assertEquals(validUser, result);
    // Since the current implementation just returns the user without modification,
    // we're just verifying that the same user object is returned.
    // If enrichment logic is added in the future, this test should be updated.
  }
}
