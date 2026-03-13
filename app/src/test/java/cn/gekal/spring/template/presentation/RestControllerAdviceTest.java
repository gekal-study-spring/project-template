package cn.gekal.spring.template.presentation;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cn.gekal.spring.template.application.service.UserService;
import cn.gekal.spring.template.domain.model.UserNotFoundException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class RestControllerAdviceTest {

  @Autowired private WebApplicationContext context;

  @MockitoBean private UserService userService;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
  }

  @Test
  void testNotFoundResource_shouldReturnJsonResponse() throws Exception {
    mockMvc
        .perform(get("/non-existent-resource"))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.type").value("about:blank"))
        .andExpect(jsonPath("$.title").value("Not Found"))
        .andExpect(jsonPath("$.detail").value("Resource not found"))
        .andExpect(jsonPath("$.instance").value("/non-existent-resource"))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void testGenericException_shouldReturnJsonResponse() throws Exception {
    mockMvc
        .perform(get("/v3/api-docs"))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.type").value("about:blank"))
        .andExpect(jsonPath("$.title").value("Not Found"));
  }

  @Test
  void testMethodArgumentTypeMismatch_shouldReturnBadRequest() throws Exception {
    mockMvc
        .perform(get("/api/users/not-a-uuid"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.type").value("about:blank"))
        .andExpect(jsonPath("$.detail").value("Parameter value is invalid for type 'UUID'"));
  }

  @Test
  void testValidationFieldError_shouldReturnBadRequest() throws Exception {
    String invalidUser = "{\"username\": \"\", \"email\": \"invalid-email\"}";
    mockMvc
        .perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(invalidUser))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.type").value("about:blank"))
        .andExpect(jsonPath("$.detail").value("Validation failed"))
        .andExpect(jsonPath("$.errors").isArray())
        .andExpect(jsonPath("$.errors", hasSize(2)));
  }

  @Test
  void testUserNotFound_shouldReturnNotFound() throws Exception {
    when(userService.getUserById(any(UUID.class)))
        .thenThrow(new UserNotFoundException("User not found"));

    mockMvc
        .perform(get("/api/users/" + UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.type").value("about:blank"))
        .andExpect(jsonPath("$.detail").value("User not found"));
  }
}
