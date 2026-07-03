package com.kubemini.infrastructure.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.RestartPolicy;
import com.kubemini.application.port.ContainerRuntime;
import com.kubemini.common.error.ErrorCode;
import com.kubemini.common.error.KubeMiniException;
import com.kubemini.domain.model.Pod;
import java.nio.charset.StandardCharsets;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "kubemini.runtime.mode", havingValue = "docker")
public class DockerContainerRuntime implements ContainerRuntime {
  private final DockerClient docker;

  public DockerContainerRuntime(DockerClient docker) {
    this.docker = docker;
  }

  @Override
  public String start(Pod pod) {
    try {
      docker.pullImageCmd(pod.image()).start().awaitCompletion();
      var response =
          docker
              .createContainerCmd(pod.image())
              .withName(pod.id())
              .withHostConfig(HostConfig.newHostConfig().withRestartPolicy(RestartPolicy.onFailureRestart(3)).withBinds(new Bind[0]))
              .exec();
      docker.startContainerCmd(response.getId()).exec();
      return response.getId();
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      throw new KubeMiniException(ErrorCode.CONTAINER_RUNTIME_ERROR, "Interrupted while pulling image " + pod.image());
    } catch (RuntimeException ex) {
      throw new KubeMiniException(ErrorCode.CONTAINER_RUNTIME_ERROR, ex.getMessage());
    }
  }

  @Override
  public void stop(Pod pod) {
    if (pod.containerId() != null) {
      docker.stopContainerCmd(pod.containerId()).withTimeout(10).exec();
      docker.removeContainerCmd(pod.containerId()).withForce(true).exec();
    }
  }

  @Override
  public void restart(Pod pod) {
    if (pod.containerId() != null) {
      docker.restartContainerCmd(pod.containerId()).exec();
    }
  }

  @Override
  public String logs(Pod pod) {
    if (pod.containerId() == null) {
      return "";
    }
    var callback = new StringBuilderLogCallback();
    docker.logContainerCmd(pod.containerId()).withStdOut(true).withStdErr(true).exec(callback);
    try {
      callback.awaitCompletion();
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
    return callback.logs();
  }

  private static final class StringBuilderLogCallback
      extends com.github.dockerjava.core.command.LogContainerResultCallback {
    private final StringBuilder logs = new StringBuilder();

    @Override
    public void onNext(com.github.dockerjava.api.model.Frame item) {
      logs.append(new String(item.getPayload(), StandardCharsets.UTF_8));
    }

    String logs() {
      return logs.toString();
    }
  }
}
