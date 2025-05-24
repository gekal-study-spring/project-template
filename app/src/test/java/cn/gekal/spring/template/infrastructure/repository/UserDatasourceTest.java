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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserDatasourceTest {

  @Mock private UserMapper userMapper;

  @InjectMocks private UserDatasource userDatasource;

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
  void findById_whenUserExists_shouldReturnUser() {
    // Arrange
    when(userMapper.findById(testId)).thenReturn(testUser);

    // Act
    Optional<User> result = userDatasource.findById(testId);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(testUser, result.get());
    verify(userMapper).findById(testId);
  }

  @Test
  void findById_whenUserDoesNotExist_shouldReturnEmpty() {
    // Arrange
    when(userMapper.findById(testId)).thenReturn(null);

    // Act
    Optional<User> result = userDatasource.findById(testId);

    // Assert
    assertFalse(result.isPresent());
    verify(userMapper).findById(testId);
  }

  @Test
  void findAll_shouldReturnAllUsers() {
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
    when(userMapper.findAll()).thenReturn(users);

    // Act
    List<User> result = userDatasource.findAll();

    // Assert
    assertEquals(2, result.size());
    assertEquals(users, result);
    verify(userMapper).findAll();
  }

  @Test
  void save_whenUserDoesNotExist_shouldInsertAndReturnUser() {
    // Arrange
    when(userMapper.findById(testId)).thenReturn(null);
    when(userMapper.insert(testUser)).thenReturn(1);

    // Act
    User result = userDatasource.save(testUser);

    // Assert
    assertEquals(testUser, result);
    verify(userMapper).findById(testId);
    verify(userMapper).insert(testUser);
    verify(userMapper, never()).update(any(User.class));
  }

  @Test
  void save_whenUserExists_shouldUpdateAndReturnUser() {
    // Arrange
    when(userMapper.findById(testId)).thenReturn(testUser);
    when(userMapper.update(testUser)).thenReturn(1);

    // Act
    User result = userDatasource.save(testUser);

    // Assert
    assertEquals(testUser, result);
    verify(userMapper).findById(testId);
    verify(userMapper, never()).insert(any(User.class));
    verify(userMapper).update(testUser);
  }

  @Test
  void deleteById_shouldCallMapperDeleteById() {
    // Arrange
    when(userMapper.deleteById(testId)).thenReturn(1);

    // Act
    userDatasource.deleteById(testId);

    // Assert
    verify(userMapper).deleteById(testId);
  }
}
