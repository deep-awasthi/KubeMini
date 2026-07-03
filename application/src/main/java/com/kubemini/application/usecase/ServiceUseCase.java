package com.kubemini.application.usecase;

import com.kubemini.application.in.CreateServiceCommand;
import com.kubemini.application.port.ServiceRepository;
import com.kubemini.common.util.Ids;
import com.kubemini.domain.model.KubeService;
import com.kubemini.domain.model.ServiceType;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ServiceUseCase {
  private final ServiceRepository services;

  public ServiceUseCase(ServiceRepository services) {
    this.services = services;
  }

  public KubeService create(CreateServiceCommand command) {
    var service =
        new KubeService(
            Ids.prefixed("svc"),
            command.namespace() == null ? "default" : command.namespace(),
            command.name(),
            command.type() == null ? ServiceType.CLUSTER_IP : command.type(),
            "10.96." + ThreadLocalRandom.current().nextInt(1, 255) + "." + ThreadLocalRandom.current().nextInt(1, 255),
            command.port(),
            command.targetPort(),
            command.selector());
    return services.save(service);
  }

  public List<KubeService> list() {
    return services.findAll();
  }

  public String resolve(String namespace, String name) {
    return services.findByName(namespace, name).map(KubeService::virtualIp).orElse(null);
  }
}
