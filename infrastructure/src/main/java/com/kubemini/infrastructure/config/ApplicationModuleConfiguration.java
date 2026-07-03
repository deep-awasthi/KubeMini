package com.kubemini.infrastructure.config;

import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.kubemini.application.controller.AutoScalingControllerService;
import com.kubemini.application.controller.HealthControllerService;
import com.kubemini.application.port.ContainerRuntime;
import com.kubemini.application.port.DeploymentRepository;
import com.kubemini.application.port.EventPublisher;
import com.kubemini.application.port.NodeRepository;
import com.kubemini.application.port.PodRepository;
import com.kubemini.application.port.ServiceRepository;
import com.kubemini.application.scheduler.SchedulerFactory;
import com.kubemini.application.service.ClusterStateService;
import com.kubemini.application.usecase.DeploymentReconciler;
import com.kubemini.application.usecase.DeploymentUseCase;
import com.kubemini.application.usecase.LogUseCase;
import com.kubemini.application.usecase.NodeUseCase;
import com.kubemini.application.usecase.PodUseCase;
import com.kubemini.application.usecase.ServiceUseCase;
import java.time.Duration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationModuleConfiguration {
  @Bean
  SchedulerFactory schedulerFactory() {
    return new SchedulerFactory();
  }

  @Bean
  ClusterStateService clusterStateService(
      NodeRepository nodes,
      DeploymentRepository deployments,
      PodRepository pods,
      ServiceRepository services) {
    return new ClusterStateService(nodes, deployments, pods, services);
  }

  @Bean
  DeploymentReconciler deploymentReconciler(
      DeploymentRepository deployments,
      PodRepository pods,
      NodeRepository nodes,
      ContainerRuntime runtime,
      SchedulerFactory schedulerFactory,
      EventPublisher events) {
    return new DeploymentReconciler(deployments, pods, nodes, runtime, schedulerFactory, events);
  }

  @Bean
  DeploymentUseCase deploymentUseCase(
      DeploymentRepository deployments,
      PodRepository pods,
      DeploymentReconciler reconciler,
      EventPublisher events) {
    return new DeploymentUseCase(deployments, pods, reconciler, events);
  }

  @Bean
  NodeUseCase nodeUseCase(NodeRepository nodes, EventPublisher events) {
    return new NodeUseCase(nodes, events);
  }

  @Bean
  PodUseCase podUseCase(PodRepository pods, ContainerRuntime runtime, EventPublisher events) {
    return new PodUseCase(pods, runtime, events);
  }

  @Bean
  ServiceUseCase serviceUseCase(ServiceRepository services) {
    return new ServiceUseCase(services);
  }

  @Bean
  LogUseCase logUseCase(PodRepository pods, ContainerRuntime runtime) {
    return new LogUseCase(pods, runtime);
  }

  @Bean
  HealthControllerService healthControllerService(
      PodRepository pods, ContainerRuntime runtime, EventPublisher events) {
    return new HealthControllerService(pods, runtime, events);
  }

  @Bean
  AutoScalingControllerService autoScalingControllerService(DeploymentUseCase deployments) {
    return new AutoScalingControllerService(deployments);
  }

  @Bean
  @ConditionalOnProperty(name = "kubemini.runtime.mode", havingValue = "docker")
  com.github.dockerjava.api.DockerClient dockerClient() {
    var config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
    var httpClient =
        new ApacheDockerHttpClient.Builder()
            .dockerHost(config.getDockerHost())
            .sslConfig(config.getSSLConfig())
            .maxConnections(100)
            .connectionTimeout(Duration.ofSeconds(30))
            .responseTimeout(Duration.ofSeconds(45))
            .build();
    return DockerClientImpl.getInstance(config, httpClient);
  }
}
