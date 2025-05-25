package cn.gekal.spring.template.presentation.api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cn.gekal.spring.template.application.service.UserService;
import cn.gekal.spring.template.domain.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class UserApiTest {

  private MockMvc mockMvc;

  @Mock private UserService userService;

  @InjectMocks private UserApi userApi;

  private ObjectMapper objectMapper;

  private User testUser;
  private UUID testId;
  private UserRequest userRequest;

  @BeforeEach
  void setUp() {
    // Initialize MockMvc
    mockMvc = MockMvcBuilders.standaloneSetup(userApi).build();

    // Initialize ObjectMapper
    objectMapper = new ObjectMapper();

    // Initialize test data
    testId = UUID.randomUUID();
    testUser = new User();
    testUser.setId(testId);
    testUser.setUsername("testuser");
    testUser.setEmail("test@example.com");
    testUser.setCreatedAt(LocalDateTime.now());
    testUser.setUpdatedAt(LocalDateTime.now());

    userRequest = new UserRequest();
    userRequest.setUsername("testuser");
    userRequest.setEmail("test@example.com");
  }

  @Test
  void getUserById_whenUserExists_shouldReturnUser() throws Exception {
    // Arrange
    when(userService.getUserById(testId)).thenReturn(Optional.of(testUser));

    // Act & Assert
    mockMvc
        .perform(get("/api/users/{id}", testId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(testId.toString())))
        .andExpect(jsonPath("$.username", is("testuser")))
        .andExpect(jsonPath("$.email", is("test@example.com")));

    verify(userService).getUserById(testId);
  }

  @Test
  void getUserById_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
    // Arrange
    when(userService.getUserById(testId)).thenReturn(Optional.empty());

    // Act & Assert
    mockMvc.perform(get("/api/users/{id}", testId)).andExpect(status().isNotFound());

    verify(userService).getUserById(testId);
  }

  @Test
  void getAllUsers_shouldReturnAllUsers() throws Exception {
    // Arrange
    User user1 = new User();
    user1.setId(UUID.randomUUID());
    user1.setUsername("user1");
    user1.setEmail("user1@example.com");
    user1.setCreatedAt(LocalDateTime.now());
    user1.setUpdatedAt(LocalDateTime.now());

    User user2 = new User();
    user2.setId(UUID.randomUUID());
    user2.setUsername("user2");
    user2.setEmail("user2@example.com");
    user2.setCreatedAt(LocalDateTime.now());
    user2.setUpdatedAt(LocalDateTime.now());

    List<User> users = Arrays.asList(user1, user2);
    when(userService.getAllUsers()).thenReturn(users);

    // Act & Assert
    mockMvc
        .perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].username", is("user1")))
        .andExpect(jsonPath("$[1].username", is("user2")));

    verify(userService).getAllUsers();
  }

  @Test
  void createUser_shouldCreateAndReturnUser() throws Exception {
    // Arrange
    when(userService.createUser(any(User.class))).thenReturn(testUser);

    // Act & Assert
    mockMvc
        .perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(testId.toString())))
        .andExpect(jsonPath("$.username", is("testuser")))
        .andExpect(jsonPath("$.email", is("test@example.com")));

    verify(userService).createUser(any(User.class));
  }

  @Test
  void updateUser_whenUserExists_shouldUpdateAndReturnUser() throws Exception {
    // Arrange
    when(userService.updateUser(eq(testId), any(User.class))).thenReturn(testUser);

    // Act & Assert
    mockMvc
        .perform(
            put("/api/users/{id}", testId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(testId.toString())))
        .andExpect(jsonPath("$.username", is("testuser")))
        .andExpect(jsonPath("$.email", is("test@example.com")));

    verify(userService).updateUser(eq(testId), any(User.class));
  }

  @Test
  void updateUser_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
    // Arrange
    when(userService.updateUser(eq(testId), any(User.class)))
        .thenThrow(new IllegalArgumentException("User not found"));

    // Act & Assert
    mockMvc
        .perform(
            put("/api/users/{id}", testId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
        .andExpect(status().isNotFound());

    verify(userService).updateUser(eq(testId), any(User.class));
  }

  @Test
  void deleteUser_whenUserExists_shouldReturnNoContent() throws Exception {
    // Arrange
    doNothing().when(userService).deleteUser(testId);

    // Act & Assert
    mockMvc.perform(delete("/api/users/{id}", testId)).andExpect(status().isNoContent());

    verify(userService).deleteUser(testId);
  }

  @Test
  void deleteUser_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
    // Arrange
    doThrow(new IllegalArgumentException("User not found")).when(userService).deleteUser(testId);

    // Act & Assert
    mockMvc.perform(delete("/api/users/{id}", testId)).andExpect(status().isNotFound());

    verify(userService).deleteUser(testId);
  }
}
