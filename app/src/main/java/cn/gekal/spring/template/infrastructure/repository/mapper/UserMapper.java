package cn.gekal.spring.template.infrastructure.repository.mapper;

import cn.gekal.spring.template.domain.model.User;
import java.util.List;
import java.util.UUID;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

  User findById(UUID id);

  List<User> findAll();

  int insert(User user);

  int update(User user);

  int deleteById(UUID id);
}
