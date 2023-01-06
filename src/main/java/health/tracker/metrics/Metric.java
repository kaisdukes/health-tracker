package health.tracker.metrics;

import health.tracker.Unit;

import java.util.HashMap;
import java.util.Map;

public enum Metric {
    WeightKg("weight", Unit.Kilogram),
    LeanMassKg("lean mass", Unit.Kilogram),
    BodyFatPercentage("body fat", Unit.Percentage);

    private static final Map<String, Metric> METRICS_BY_KEY = new HashMap<>();
    private final String key;
    private final String description;
    private final Unit unit;

    Metric(String description, Unit unit) {
        this.key = description.replace(' ', '-');
        this.description = description;
        this.unit = unit;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    public Unit getUnit() {
        return unit;
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