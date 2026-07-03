package com.kubemini.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Deployment {
  private final String id;
  private final String namespace;
  private final String name;
  private final Map<String, String> labels;
  private final Instant createdAt;
  private String image;
  private int desiredReplicas;
  private DeploymentStatus status;
  private ResourceRequirements resources;
  private SchedulerAlgorithm schedulerAlgorithm;
  private boolean paused;
  private int generation;
  private List<String> history;

  public Deployment(
      String id,
      String namespace,
      String name,
      String image,
      int desiredReplicas,
      DeploymentStatus status,
      ResourceRequirements resources,
      SchedulerAlgorithm schedulerAlgorithm,
      Map<String, String> labels,
      Instant createdAt,
      boolean paused,
      int generation,
      List<String> history) {
    this.id = Objects.requireNonNull(id);
    this.namespace = namespace == null || namespace.isBlank() ? "default" : namespace;
    this.name = Objects.requireNonNull(name);
    this.image = Objects.requireNonNull(image);
    this.desiredReplicas = desiredReplicas;
    this.status = Objects.requireNonNull(status);
    this.resources = Objects.requireNonNull(resources);
    this.schedulerAlgorithm = Objects.requireNonNull(schedulerAlgorithm);
    this.labels = Map.copyOf(labels == null ? Map.of() : labels);
    this.createdAt = Objects.requireNonNull(createdAt);
    this.paused = paused;
    this.generation = generation;
    this.history = List.copyOf(history == null ? List.of(image) : history);
  }

  public void scale(int replicas) {
    desiredReplicas = replicas;
    status = paused ? DeploymentStatus.PAUSED : DeploymentStatus.RUNNING;
  }

  public void updateImage(String nextImage) {
    if (!image.equals(nextImage)) {
      history = append(history, image);
      image = nextImage;
      generation++;
      status = DeploymentStatus.UPDATING;
    }
  }

  public void pause() {
    paused = true;
    status = DeploymentStatus.PAUSED;
  }

  public void resume() {
    paused = false;
    status = DeploymentStatus.RUNNING;
  }

  public void rollback() {
    if (!history.isEmpty()) {
      image = history.get(history.size() - 1);
      history = history.subList(0, history.size() - 1);
      generation++;
      status = DeploymentStatus.UPDATING;
    }
  }

  public void markRunning() {
    if (!paused) {
      status = DeploymentStatus.RUNNING;
    }
  }

  public void markDeleted() {
    status = DeploymentStatus.DELETED;
  }

  private static List<String> append(List<String> source, String value) {
    java.util.ArrayList<String> copy = new java.util.ArrayList<>(source);
    copy.add(value);
    return List.copyOf(copy);
  }

  public String id() {
    return id;
  }

  public String namespace() {
    return namespace;
  }

  public String name() {
    return name;
  }

  public String image() {
    return image;
  }

  public int desiredReplicas() {
    return desiredReplicas;
  }

  public DeploymentStatus status() {
    return status;
  }

  public ResourceRequirements resources() {
    return resources;
  }

  public SchedulerAlgorithm schedulerAlgorithm() {
    return schedulerAlgorithm;
  }

  public Map<String, String> labels() {
    return labels;
  }

  public Instant createdAt() {
    return createdAt;
  }

  public boolean paused() {
    return paused;
  }

  public int generation() {
    return generation;
  }

  public List<String> history() {
    return history;
  }
}
