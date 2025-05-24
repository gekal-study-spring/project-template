package cn.gekal.spring.template.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import cn.gekal.spring.template.domain.model.User;
import cn.gekal.spring.template.infrastructure.repository.mapper.UserMapper;
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

class UserDatasourceTest {

  @Mock private UserMapper userMapper;

  @InjectMocks private UserDatasource userDatasource;

  private UUID userId;
  private User user;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();
    user = new User(userId, "testUser", "test@example.com", now, now);
  }

  @Test
  void findById_WhenUserExists_ReturnsUser() {
    // Arrange
    when(userMapper.findById(userId)).thenReturn(user);

    // Act
    Optional<User> result = userDatasource.findById(userId);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(userId, result.get().getId());
    assertEquals("testUser", result.get().getUsername());
    assertEquals("test@example.com", result.get().getEmail());
    verify(userMapper).findById(userId);
  }

  @Test
  void findById_WhenUserDoesNotExist_ReturnsEmptyOptional() {
    // Arrange
    when(userMapper.findById(userId)).thenReturn(null);

    // Act
    Optional<User> result = userDatasource.findById(userId);

    // Assert
    assertFalse(result.isPresent());
    verify(userMapper).findById(userId);
  }

  @Test
  void findAll_ReturnsAllUsers() {
    // Arrange
    List<User> users = Arrays.asList(user);
    when(userMapper.findAll()).thenReturn(users);

    // Act
    List<User> result = userDatasource.findAll();

    // Assert
    assertEquals(1, result.size());
    assertEquals(userId, result.get(0).getId());
    assertEquals("testUser", result.get(0).getUsername());
    assertEquals("test@example.com", result.get(0).getEmail());
    verify(userMapper).findAll();
  }

  @Test
  void save_WhenUserDoesNotExist_InsertsUser() {
    // Arrange
    when(userMapper.findById(userId)).thenReturn(null);
    when(userMapper.insert(user)).thenReturn(1);

    // Act
    User result = userDatasource.save(user);

    // Assert
    assertEquals(userId, result.getId());
    assertEquals("testUser", result.getUsername());
    assertEquals("test@example.com", result.getEmail());
    verify(userMapper).findById(userId);
    verify(userMapper).insert(user);
    verify(userMapper, never()).update(any(User.class));
  }

  @Test
  void save_WhenUserExists_UpdatesUser() {
    // Arrange
    when(userMapper.findById(userId)).thenReturn(user);
    when(userMapper.update(user)).thenReturn(1);

    // Act
    User result = userDatasource.save(user);

    // Assert
    assertEquals(userId, result.getId());
    assertEquals("testUser", result.getUsername());
    assertEquals("test@example.com", result.getEmail());
    verify(userMapper).findById(userId);
    verify(userMapper, never()).insert(any(User.class));
    verify(userMapper).update(user);
  }

  @Test
  void deleteById_DeletesUser() {
    // Arrange
    when(userMapper.deleteById(userId)).thenReturn(1);

    // Act
    userDatasource.deleteById(userId);

    // Assert
    verify(userMapper).deleteById(userId);
  }
}
