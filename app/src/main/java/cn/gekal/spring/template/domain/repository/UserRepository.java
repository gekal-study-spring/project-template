package cn.gekal.spring.template.domain.repository;

import cn.gekal.spring.template.domain.model.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

  Optional<User> findById(UUID id);

  List<User> findAll();

  User save(User user);

  void deleteById(UUID id);
}
