package health.tracker.reports;

import health.tracker.activity.ActivityService;
import health.tracker.activity.MetService;
import health.tracker.diet.DietService;
import health.tracker.diet.NutritionService;
import health.tracker.metrics.HealthService;
import health.tracker.workouts.WorkoutService;
import org.junit.jupiter.api.Test;

import static health.tracker.TestContext.DATA_PATH;
import static health.tracker.TestContext.DOCS_PATH;

class ReportServiceTest {

    @Test
    void shouldCreateReports() {
        var healthService = new HealthService(DATA_PATH);
        var activityService = new ActivityService(DATA_PATH);
        var metService = new MetService(DATA_PATH);
        var nutritionService = new NutritionService(DATA_PATH);
        var dietService = new DietService(DATA_PATH, nutritionService);
        var workoutService = new WorkoutService(DATA_PATH);
        var reportService = new ReportService(
                DATA_PATH,
                DOCS_PATH,
                healthService,
                activityService,
                metService,
                dietService,
                workoutService);
        reportService.createReports();
    }
}