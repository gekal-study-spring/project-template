package cn.gekal.spring.template.application.service;

import cn.gekal.spring.template.application.dto.UserDto;
import cn.gekal.spring.template.domain.model.User;
import cn.gekal.spring.template.domain.repository.UserRepository;
import cn.gekal.spring.template.domain.service.UserDomainService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserApplicationService {

  private final UserRepository userRepository;
  private final UserDomainService userDomainService;

  public UserApplicationService(
      UserRepository userRepository, UserDomainService userDomainService) {
    this.userRepository = userRepository;
    this.userDomainService = userDomainService;
  }

  public Optional<UserDto> getUserById(UUID id) {
    return userRepository.findById(id).map(this::convertToDto);
  }

  public List<UserDto> getAllUsers() {
    return userRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
  }

  @Transactional
  public UserDto createUser(UserDto userDto) {
    User user = convertToEntity(userDto);
    user.setId(UUID.randomUUID());
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());

    if (!userDomainService.validateUser(user)) {
      throw new IllegalArgumentException("Invalid user data");
    }

    User enrichedUser = userDomainService.enrichUser(user);
    User savedUser = userRepository.save(enrichedUser);

    return convertToDto(savedUser);
  }

  @Transactional
  public UserDto updateUser(UUID id, UserDto userDto) {
    User existingUser =
        userRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    existingUser.setUsername(userDto.getUsername());
    existingUser.setEmail(userDto.getEmail());
    existingUser.setUpdatedAt(LocalDateTime.now());

    if (!userDomainService.validateUser(existingUser)) {
      throw new IllegalArgumentException("Invalid user data");
    }

    User enrichedUser = userDomainService.enrichUser(existingUser);
    User savedUser = userRepository.save(enrichedUser);

    return convertToDto(savedUser);
  }

  @Transactional
  public void deleteUser(UUID id) {
    if (userRepository.findById(id).isEmpty()) {
      throw new IllegalArgumentException("User not found");
    }

    userRepository.deleteById(id);
  }

  private UserDto convertToDto(User user) {
    return new UserDto(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getCreatedAt(),
        user.getUpdatedAt());
  }

  private User convertToEntity(UserDto userDto) {
    return new User(
        userDto.getId(),
        userDto.getUsername(),
        userDto.getEmail(),
        userDto.getCreatedAt(),
        userDto.getUpdatedAt());
  }
}
