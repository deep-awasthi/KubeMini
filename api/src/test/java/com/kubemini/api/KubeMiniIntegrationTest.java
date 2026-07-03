package com.kubemini.api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class KubeMiniIntegrationTest {
  @Autowired MockMvc mvc;
  @Autowired ObjectMapper objectMapper;

  @Test
  void registersNodeAndReconcilesDeploymentIntoRunningPods() throws Exception {
    String token =
        token(
            mvc.perform(
                    post("/api/v1/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("subject", "alice", "role", "ADMIN"))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString());

    mvc.perform(
            post("/api/v1/nodes")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    json(
                        Map.of(
                            "name",
                            "worker-1",
                            "host",
                            "unix:///var/run/docker.sock",
                            "cpuCapacity",
                            "4000m",
                            "memoryCapacity",
                            "8Gi",
                            "diskCapacity",
                            "20Gi"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.status", is("READY")));

    mvc.perform(
            post("/api/v1/deployments")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    json(
                        Map.of(
                            "name",
                            "payment-service",
                            "image",
                            "nginx:1.27",
                            "replicas",
                            2,
                            "cpuRequest",
                            "250m",
                            "memoryRequest",
                            "128Mi",
                            "storageRequest",
                            "0",
                            "scheduler",
                            "RESOURCE_AWARE"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.replicas", is(2)))
        .andExpect(jsonPath("$.data.availableReplicas", is(2)));

    mvc.perform(get("/api/v1/pods").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data", hasSize(2)))
        .andExpect(jsonPath("$.data[0].phase", is("RUNNING")));
  }

  private String token(String response) throws Exception {
    JsonNode root = objectMapper.readTree(response);
    return root.path("data").path("token").asText();
  }

  private String json(Object value) throws Exception {
    return objectMapper.writeValueAsString(value);
  }
}
