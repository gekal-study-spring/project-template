package cn.gekal.spring.template.presentation.api;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cn.gekal.spring.template.application.service.UserService;
import cn.gekal.spring.template.domain.model.UserScope;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class UserApiSecurityTest {

  @Autowired private WebApplicationContext context;
  @MockitoBean private UserService userService;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
  }

  @Test
  void getAllUsers_withoutAuth_shouldReturn401() throws Exception {
    mockMvc.perform(get("/api/users")).andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(authorities = UserScope.Values.READ)
  void getAllUsers_withCorrectAuthority_shouldReturnOk() throws Exception {
    Mockito.when(userService.getAllUsers()).thenReturn(Collections.emptyList());
    mockMvc.perform(get("/api/users")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = "SCOPE_users::read")
  void getAllUsers_withScopePrefixAuthority_shouldReturnForbidden() throws Exception {
    // プレフィックスなしを期待しているため、SCOPE_付きは拒否されるはず
    mockMvc.perform(get("/api/users")).andExpect(status().isForbidden());
  }
}
