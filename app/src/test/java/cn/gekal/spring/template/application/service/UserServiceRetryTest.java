package cn.gekal.spring.template.application.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cn.gekal.spring.template.domain.model.User;
import cn.gekal.spring.template.domain.repository.UserRepository;
import cn.gekal.spring.template.domain.service.UserDomainService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
    properties = {
      "spring.datasource.url=jdbc:postgresql://localhost:15432/template",
      "spring.datasource.driver-class-name=org.postgresql.Driver",
      "spring.datasource.username=myuser",
      "spring.datasource.password=secret",
      "spring.sql.init.mode=never",
      "spring.docker.compose.enabled=false"
    })
class UserServiceRetryTest {

  @Autowired private UserService userService;

  @MockitoBean private UserRepository userRepository;

  @MockitoBean private UserDomainService userDomainService;

  @MockitoBean
  private org.springframework.transaction.PlatformTransactionManager transactionManager;

  @Test
  void createUser_shouldRetryOnTransientDataAccessException() {
    // Arrange
    User user = new User();
    user.setUsername("retryuser");
    user.setEmail("retry@example.com");

    // 2回失敗し、3回目で成功するように設定
    when(userDomainService.validateUser(any(User.class))).thenReturn(true);
    when(userDomainService.enrichUser(any(User.class))).thenReturn(user);
    when(userRepository.save(any(User.class)))
        .thenThrow(new TransientDataAccessException("Temporary error1", null) {})
        .thenThrow(new TransientDataAccessException("Temporary error2", null) {})
        .thenReturn(user);

    // Act
    User result = userService.createUser(user);

    // Assert
    assertNotNull(result);
    // saveが3回呼ばれたことを確認
    verify(userRepository, times(3)).save(any(User.class));
  }

  @Test
  void createUser_shouldFailAfterMaxRetries() {
    // Arrange
    User user = new User();
    user.setUsername("retryuser");
    user.setEmail("retry@example.com");

    when(userDomainService.validateUser(any(User.class))).thenReturn(true);
    when(userDomainService.enrichUser(any(User.class))).thenReturn(user);
    // ずっと失敗するように設定
    when(userRepository.save(any(User.class)))
        .thenThrow(new TransientDataAccessException("Temporary error", null) {});

    // Act & Assert
    assertThrows(
        TransientDataAccessException.class,
        () -> {
          userService.createUser(user);
        });

    // 最大3回リトライ（初回 + リトライ3回 = 合計4回実行）されるはず
    // UserServiceのアノテーションは maxRetries = 3 なので、初回含めて4回
    verify(userRepository, times(4)).save(any(User.class));
  }
}
