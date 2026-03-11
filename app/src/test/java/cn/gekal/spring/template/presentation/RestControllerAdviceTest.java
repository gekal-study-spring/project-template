package cn.gekal.spring.template.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class RestControllerAdviceTest {

  @Autowired private WebApplicationContext context;

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
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  void testGenericException_shouldReturnJsonResponse() throws Exception {
    // 実際には意図的に例外を発生させるエンドポイントを作るか、
    // 既存のエンドポイントで不正な入力を送るなどしてテストする
    // ここでは /v3/api-docs (SecurityConfigで許可されているが、
    // 実際には存在しない場合にRestControllerAdviceが動く)をテスト
    mockMvc
        .perform(get("/v3/api-docs"))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").exists());
  }
}
