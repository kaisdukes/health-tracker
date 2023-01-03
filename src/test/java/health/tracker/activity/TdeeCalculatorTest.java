package health.tracker.activity;

import health.tracker.metrics.HealthService;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static java.time.LocalDate.now;

class TdeeCalculatorTest {

    @Test
    void shouldGetTdee() {
        var basePath = Paths.get("data");
        var healthService = new HealthService(basePath);
        var activityService = new ActivityService(basePath);
        var tdeeCalculator = new TdeeCalculator(healthService, activityService);
        tdeeCalculator.getTdee(now());
    }
}