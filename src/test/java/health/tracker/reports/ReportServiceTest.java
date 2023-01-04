package health.tracker.reports;

import health.tracker.activity.ActivityService;
import health.tracker.activity.MetService;
import health.tracker.diet.DietService;
import health.tracker.diet.NutritionService;
import health.tracker.metrics.HealthService;
import org.junit.jupiter.api.Test;

import static health.tracker.TestContext.BASE_PATH;

class ReportServiceTest {

    @Test
    void shouldCreateReports() {
        var healthService = new HealthService(BASE_PATH);
        var activityService = new ActivityService(BASE_PATH);
        var metService = new MetService(BASE_PATH);
        var nutritionService = new NutritionService(BASE_PATH);
        var dietService = new DietService(BASE_PATH, nutritionService);
        var reportService = new ReportService(BASE_PATH, healthService, activityService, metService, dietService);
        reportService.createReports();
    }
}