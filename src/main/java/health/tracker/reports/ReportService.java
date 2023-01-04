package health.tracker.reports;

import health.tracker.activity.ActivityService;
import health.tracker.activity.MetService;
import health.tracker.diet.DietService;
import health.tracker.metrics.HealthService;
import health.tracker.metrics.Metric;
import health.tracker.reports.macros.MacrosReport;
import health.tracker.reports.metrics.MetricsReport;

import java.nio.file.Path;

public class ReportService {
    private final Path basePath;
    private final HealthService healthService;
    private final ActivityService activityService;
    private final MetService metService;
    private final DietService dietService;

    public ReportService(Path basePath,
                         HealthService healthService,
                         ActivityService activityService,
                         MetService metService,
                         DietService dietService) {

        this.basePath = basePath;
        this.healthService = healthService;
        this.activityService = activityService;
        this.metService = metService;
        this.dietService = dietService;
    }

    public void createReports() {

        // metrics
        var metricsReport = new MetricsReport(basePath, healthService);
        metricsReport.createReport(Metric.WeightKg);

        // macros
        var macrosReport = new MacrosReport(
                basePath,
                healthService,
                activityService,
                metService,
                dietService);
        macrosReport.createReports();
    }
}