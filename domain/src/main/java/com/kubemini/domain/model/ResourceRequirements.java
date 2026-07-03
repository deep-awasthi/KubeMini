package com.kubemini.domain.model;

public record ResourceRequirements(ResourceQuantity requests, ResourceQuantity limits) {
  public static ResourceRequirements none() {
    return new ResourceRequirements(ResourceQuantity.ZERO, ResourceQuantity.ZERO);
  }
}
