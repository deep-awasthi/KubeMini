package com.kubemini.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
    scanBasePackages = "com.kubemini",
    exclude = UserDetailsServiceAutoConfiguration.class)
@EntityScan(basePackages = "com.kubemini.infrastructure.persistence")
@EnableJpaRepositories(basePackages = "com.kubemini.infrastructure.persistence")
public class KubeMiniApplication {
  public static void main(String[] args) {
    SpringApplication.run(KubeMiniApplication.class, args);
  }
}
