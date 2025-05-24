package cn.gekal.spring.template.application.service;

import cn.gekal.spring.template.domain.model.User;
import cn.gekal.spring.template.domain.repository.UserRepository;
import cn.gekal.spring.template.domain.service.UserDomainService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    User enrichedUser = userDomainService.enrichUser(user);
    User savedUser = userRepository.save(enrichedUser);

    return savedUser;
  }

  @Transactional
  public User updateUser(UUID id, User user) {
    User existingUser =
        userRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    existingUser.setUsername(user.getUsername());
    existingUser.setEmail(user.getEmail());
    existingUser.setUpdatedAt(LocalDateTime.now());

    if (!userDomainService.validateUser(existingUser)) {
      throw new IllegalArgumentException("Invalid user data");
    }

    User enrichedUser = userDomainService.enrichUser(existingUser);

    return userRepository.save(enrichedUser);
  }

  @Transactional
  public void deleteUser(UUID id) {
    if (userRepository.findById(id).isEmpty()) {
      throw new IllegalArgumentException("User not found");
    }

    userRepository.deleteById(id);
  }
}
