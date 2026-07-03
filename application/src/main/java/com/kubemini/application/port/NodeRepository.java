package com.kubemini.application.port;

import com.kubemini.domain.model.Node;
import java.util.List;
import java.util.Optional;

public interface NodeRepository {
  Node save(Node node);

  Optional<Node> findById(String id);

  List<Node> findAll();

  void deleteById(String id);
}
