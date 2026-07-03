package com.kubemini.application.port;

import com.kubemini.domain.model.KubeService;
import java.util.List;
import java.util.Optional;

public interface ServiceRepository {
  KubeService save(KubeService service);

  Optional<KubeService> findById(String id);

  Optional<KubeService> findByName(String namespace, String name);

  List<KubeService> findAll();
}
