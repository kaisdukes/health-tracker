package health.tracker.activity;

import health.tracker.diet.DietService;
import health.tracker.metrics.HealthService;
import health.tracker.metrics.Metric;

import java.time.LocalDate;

public class TdeeCalculator {
    private final HealthService healthService;
    private final ActivityService activityService;
    private final MetService metService;
    private final DietService dietService;

    public TdeeCalculator(
            HealthService healthService,
            ActivityService activityService,
            MetService metService,
            DietService dietService) {

        this.healthService = healthService;
        this.activityService = activityService;
        this.metService = metService;
        this.dietService = dietService;
    }

    public int getTdee(LocalDate date) {
        return getAee(date) + getTef(date);
    }

    private int getAee(LocalDate date) {
        var leanMass = (int) Math.round(healthService.getAveragedMetric(date, Metric.LeanMassKg));
        var rmr = getKatchMcArdleRmr(leanMass);
        var kcal = 0;
        var totalHours = 0d;
        for (var activity : activityService.getActivities(date)) {
            var met = metService.getMet(activity.name);
            var hours = activity.hoursPerDay * activity.daysPerWeek / 7d;
            kcal += (int) Math.round(rmr / 24d * hours * met);
            totalHours += hours;
        }
        if (totalHours > 24) {
            throw new RuntimeException("Total activity hours per day " + totalHours + " > 24 hours.");
        }
        return kcal + (int) Math.round(rmr / 24d * (24 - totalHours));
    }

    private int getTef(LocalDate date) {
        var diet = dietService.getDiet(date);
        return (int) Math.round(
                4 * 0.25 * diet.getProteinGrams()
                        + 4 * 0.075 * diet.getCarbsGrams()
                        + 9 * 0.015 * diet.getFatGrams());
    }

    private static int getKatchMcArdleRmr(int leanMass) {
        return (int) Math.round(370 + 21.6 * leanMass);
    }
}