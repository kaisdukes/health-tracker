package health.tracker.metrics;

import java.util.HashMap;
import java.util.Map;

public enum Metric {
    WeightKg("weight", "kg"),
    LeanMassKg("lean mass", "kg"),
    BodyFatPercentage("body fat", "%");

    private static final Map<String, Metric> METRICS_BY_KEY = new HashMap<>();
    private final String key;
    private final String description;
    private final String units;

    Metric(String description, String units) {
        this.key = description.replace(' ', '-');
        this.description = description;
        this.units = units;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    public String getUnits() {
        return units;
    }

    public static Metric getMetric(String key) {
        var metric = METRICS_BY_KEY.get(key);
        if (metric == null) {
            throw new RuntimeException("Metric key '" + key + "' not recognized.");
        }
        return metric;
    }

    static {
        for (var metric : Metric.values()) {
            METRICS_BY_KEY.put(metric.getKey(), metric);
        }
    }
}