package com.kubemini.infrastructure.persistence;

import com.kubemini.domain.model.ServiceType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import java.util.HashMap;
import java.util.Map;

@Entity(name = "services")
class ServiceEntity {
  @Id String id;
  String namespaceName;
  String name;

  @Enumerated(EnumType.STRING)
  ServiceType type;

  String virtualIp;
  Integer portNumber;
  Integer targetPort;

  @ElementCollection(fetch = FetchType.EAGER)
  Map<String, String> selector = new HashMap<>();
}
