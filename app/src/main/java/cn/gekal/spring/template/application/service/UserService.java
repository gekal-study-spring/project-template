package cn.gekal.spring.template.application.service;

import cn.gekal.spring.template.domain.model.User;
import cn.gekal.spring.template.domain.model.UserNotFoundException;
import cn.gekal.spring.template.domain.repository.UserRepository;
import cn.gekal.spring.template.domain.service.UserDomainService;
import java.sql.SQLTransientConnectionException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.annotation.Transactional;

@Retryable(
    includes = {
      // DBへの接続が確立できない
      DataAccessResourceFailureException.class,
      // DB接続プール（HikariCPなど）から有効なコネクションを取得できなかったり、DBに接続できなかったりした場合
      CannotCreateTransactionException.class,
      // JDBC標準の例外で、一時的な接続エラー
      SQLTransientConnectionException.class,
      // DB内部の「一時的な競合」
      TransientDataAccessException.class,
    },
    maxRetries = 3,
    delay = 100,
    jitter = 10,
    multiplier = 2,
    maxDelay = 1000)
@Service
public class UserService {

  private final UserRepository userRepository;
  private final UserDomainService userDomainService;

  public UserService(UserRepository userRepository, UserDomainService userDomainService) {
    this.userRepository = userRepository;
    this.userDomainService = userDomainService;
  }

  public Optional<User> getUserById(UUID id) {
    return userRepository.findById(id);
  }

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @Transactional
  public User createUser(User user) {

    user.setId(UUID.randomUUID());
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());

    if (!userDomainService.validateUser(user)) {
      throw new IllegalArgumentException("Invalid user data");
    }

    if (userRepository.findByEmail(user.getEmail()).isPresent()) {
      throw new IllegalArgumentException("Email already exists");
    }

    User enrichedUser = userDomainService.enrichUser(user);
    userRepository.create(enrichedUser);

    return userRepository
        .findById(enrichedUser.getId())
        .orElseThrow(() -> new UserNotFoundException("User created but not found"));
  }

  @Transactional
  public User updateUser(UUID id, User user) {
    User existingUser =
        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));

    existingUser.setUsername(user.getUsername());
    existingUser.setEmail(user.getEmail());
    existingUser.setUpdatedAt(LocalDateTime.now());

    if (!userDomainService.validateUser(existingUser)) {
      throw new UserNotFoundException("Invalid user data");
    }

    User enrichedUser = userDomainService.enrichUser(existingUser);

    return userRepository.save(enrichedUser);
  }

  @Transactional
  public void deleteUser(UUID id) {
    if (userRepository.findById(id).isEmpty()) {
      throw new UserNotFoundException("User not found");
    }

    userRepository.deleteById(id);
  }
}
