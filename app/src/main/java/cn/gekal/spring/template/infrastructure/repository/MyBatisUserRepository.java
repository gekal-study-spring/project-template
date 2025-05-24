package cn.gekal.spring.template.infrastructure.repository;

import cn.gekal.spring.template.domain.model.User;
import cn.gekal.spring.template.domain.repository.UserRepository;
import cn.gekal.spring.template.infrastructure.repository.mapper.UserMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class MyBatisUserRepository implements UserRepository {

  private final UserMapper userMapper;

  public MyBatisUserRepository(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  @Override
  public Optional<User> findById(UUID id) {
    return Optional.ofNullable(userMapper.findById(id));
  }

  @Override
  public List<User> findAll() {
    return userMapper.findAll();
  }

  @Override
  public User save(User user) {
    if (userMapper.findById(user.getId()) == null) {
      userMapper.insert(user);
    } else {
      userMapper.update(user);
    }
    return user;
  }

  @Override
  public void deleteById(UUID id) {
    userMapper.deleteById(id);
  }
}
