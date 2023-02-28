package health.tracker.metrics;

import health.tracker.Unit;

import java.util.Arrays;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public enum Metric {
    WeightKg("weight", Unit.Kilogram),
    LeanMassKg("lean mass", Unit.Kilogram),
    FatMassKg("fat mass", Unit.Kilogram),
    BodyFatPercentage("body fat", Unit.Percentage);

    private static final Map<String, Metric> METRICS_BY_KEY
            = Arrays.stream(values()).collect(toMap(Metric::getKey, identity()));

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
}