package com.kubemini.application.scheduler;

import com.kubemini.domain.model.SchedulerAlgorithm;
import java.util.EnumMap;
import java.util.Map;

public class SchedulerFactory {
  private final Map<SchedulerAlgorithm, Scheduler> schedulers;

  public SchedulerFactory() {
    schedulers = new EnumMap<>(SchedulerAlgorithm.class);
    var leastLoaded = new LeastLoadedScheduler();
    schedulers.put(SchedulerAlgorithm.LEAST_LOADED, leastLoaded);
    schedulers.put(SchedulerAlgorithm.RESOURCE_AWARE, leastLoaded);
    schedulers.put(SchedulerAlgorithm.NODE_AFFINITY, leastLoaded);
    schedulers.put(SchedulerAlgorithm.PREFERRED, leastLoaded);
    schedulers.put(SchedulerAlgorithm.ROUND_ROBIN, new RoundRobinScheduler());
    schedulers.put(SchedulerAlgorithm.BIN_PACKING, new BinPackingScheduler());
    schedulers.put(SchedulerAlgorithm.RANDOM, new RandomScheduler());
    schedulers.put(SchedulerAlgorithm.ANTI_AFFINITY, leastLoaded);
  }

  public Scheduler forAlgorithm(SchedulerAlgorithm algorithm) {
    return schedulers.getOrDefault(algorithm, schedulers.get(SchedulerAlgorithm.RESOURCE_AWARE));
  }
}
