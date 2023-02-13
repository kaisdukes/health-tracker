package health.tracker.forecasting;

import health.tracker.diet.DietMode;
import health.tracker.metrics.HealthService;
import health.tracker.metrics.Metric;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static health.tracker.text.DoubleFormat.formatDouble;
import static java.time.LocalDate.now;

public class Forecaster {
    private final HealthService healthService;

    public Forecaster(HealthService healthService) {
        this.healthService = healthService;
    }

    public void projectBodyFatPercentage(double targetBodyFatPercentage) {

        // already at target?
        var dietMode = getDietMode(targetBodyFatPercentage);
        if (dietMode == DietMode.Maintenance) {
            return;
        }

        // projections
        var projections = getProjections(targetBodyFatPercentage, dietMode);
        writeReport(targetBodyFatPercentage, projections);
    }

    private void writeReport(double targetBodyFatPercentage, List<Projection> projections) {

        // report
        System.out.println("projecting " + formatDouble(targetBodyFatPercentage) + "% body fat...");
        for (var projection : projections) {
            System.out.println(
                    "  using "
                            + projection.getLookbackWeekCount()
                            + "-week lookback: "
                            + projection.getWeightKg()
                            + "kg on "
                            + projection.getDate());
        }
    }

    private List<Projection> getProjections(double targetBodyFatPercentage, DietMode dietMode) {
        var projections = new ArrayList<Projection>();
        for (var i = 1; i <= 7; i++) {
            projections.add(getProjection(targetBodyFatPercentage, dietMode, i));
        }
        return projections;
    }

    private Projection getProjection(double targetBodyFatPercentage, DietMode dietMode, int lookbackWeekCount) {
        var leanMassChangePerWeek = getChange(Metric.LeanMassKg, lookbackWeekCount) / lookbackWeekCount;
        var fatMassChangePerWeek = getChange(Metric.FatMassKg, lookbackWeekCount) / lookbackWeekCount;

        Predicate<Double> targetReached
                = dietMode == DietMode.Bulking
                ? x -> x >= targetBodyFatPercentage
                : x -> x <= targetBodyFatPercentage;

        LocalDate date = now();
        double leanMass = healthService.getAveragedMetric(date, Metric.LeanMassKg);
        double fatMass = healthService.getAveragedMetric(date, Metric.FatMassKg);
        double bodyFatPercentage = (int) Math.round(100 * fatMass / (leanMass + fatMass));
        while (!targetReached.test(bodyFatPercentage)) {
            date = date.plusDays(7);
            leanMass += leanMassChangePerWeek;
            fatMass += fatMassChangePerWeek;
            bodyFatPercentage = (int) Math.round(100 * fatMass / (leanMass + fatMass));
        }

        var weight = (int) Math.round(leanMass + fatMass);
        return new Projection(lookbackWeekCount, date, weight);
    }

    private double getChange(Metric metric, int lookbackWeekCount) {
        var today = now();
        var startDate = today.minusDays(lookbackWeekCount * 7L);
        return healthService.getAveragedMetric(today, metric)
                - healthService.getAveragedMetric(startDate, metric);
    }

    private DietMode getDietMode(double targetBodyFatPercentage) {
        var bodyFatPercentage = Math.round(healthService.getAveragedMetric(now(), Metric.BodyFatPercentage));
        if (targetBodyFatPercentage == bodyFatPercentage) {
            return DietMode.Maintenance;
        }
        return targetBodyFatPercentage > bodyFatPercentage ? DietMode.Bulking : DietMode.Cutting;
    }
}