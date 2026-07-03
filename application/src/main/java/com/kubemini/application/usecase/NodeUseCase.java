package com.kubemini.application.usecase;

import com.kubemini.application.in.RegisterNodeCommand;
import com.kubemini.application.port.EventPublisher;
import com.kubemini.application.port.NodeRepository;
import com.kubemini.common.error.ErrorCode;
import com.kubemini.common.error.KubeMiniException;
import com.kubemini.common.event.ClusterEvent;
import com.kubemini.common.util.Ids;
import com.kubemini.domain.model.Node;
import com.kubemini.domain.model.NodeStatus;
import com.kubemini.domain.model.ResourceQuantity;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class NodeUseCase {
  private final NodeRepository nodes;
  private final EventPublisher events;

  public NodeUseCase(NodeRepository nodes, EventPublisher events) {
    this.nodes = nodes;
    this.events = events;
  }

  public Node register(RegisterNodeCommand command) {
    var node =
        new Node(
            Ids.prefixed("node"),
            command.name(),
            command.host(),
            ResourceQuantity.of(command.cpuCapacity(), command.memoryCapacity(), command.diskCapacity()),
            command.labels(),
            NodeStatus.READY,
            ResourceQuantity.ZERO,
            Instant.now());
    var saved = nodes.save(node);
    events.publish(ClusterEvent.of("NodeJoined", saved.id(), "Node registered", Map.of("name", saved.name())));
    return saved;
  }

  public List<Node> list() {
    return nodes.findAll();
  }

  public Node get(String id) {
    return nodes.findById(id).orElseThrow(() -> new KubeMiniException(ErrorCode.NODE_NOT_FOUND, "Node not found: " + id));
  }

  public void remove(String id) {
    nodes.deleteById(id);
    events.publish(ClusterEvent.of("NodeLeft", id, "Node removed", Map.of()));
  }

  public Node drain(String id) {
    var node = get(id);
    node.drain();
    var saved = nodes.save(node);
    events.publish(ClusterEvent.of("NodeDraining", id, "Node draining started", Map.of()));
    return saved;
  }

  public Node cordon(String id) {
    var node = get(id);
    node.cordon();
    var saved = nodes.save(node);
    events.publish(ClusterEvent.of("NodeCordoned", id, "Node cordoned", Map.of()));
    return saved;
  }

  public Node heartbeat(String id) {
    var node = get(id);
    node.heartbeat();
    return nodes.save(node);
  }
}
