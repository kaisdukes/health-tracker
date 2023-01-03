package health.tracker.activity;

import health.tracker.metrics.HealthService;
import health.tracker.metrics.Metric;

import java.time.LocalDate;

public class TdeeCalculator {
    private final HealthService healthService;
    private final ActivityService activityService;

    public TdeeCalculator(HealthService healthService, ActivityService activityService) {
        this.healthService = healthService;
        this.activityService = activityService;
    }

    public void getTdee(final LocalDate date) {
        System.err.println("Calculating TDEE for " + date);
        var weightKg = (int) Math.round(healthService.getAveragedMetric(date, Metric.WeightKg));
        System.err.println("Weight (kg): " + weightKg);
    }
}