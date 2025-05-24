package cn.gekal.spring.template.domain.service;

import cn.gekal.spring.template.domain.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserDomainService {

  public boolean validateUser(User user) {
    if (user == null) {
      return false;
    }

    if (!user.isValid()) {
      return false;
    }

    String email = user.getEmail();
    if (email == null || !email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
      return false;
    }

    String username = user.getUsername();
    return username != null && username.length() >= 3;
  }

  public User enrichUser(User user) {
    // This is where you would add domain-specific enrichment logic
    // For example, calculating derived properties based on the user's data
    return user;
  }
}
