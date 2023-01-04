package health.tracker.activity;

import health.tracker.diet.DietService;
import health.tracker.metrics.HealthService;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static java.time.LocalDate.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class TdeeCalculatorTest {

    @Test
    void shouldGetTdee() {
        var basePath = Paths.get("data");
        var healthService = new HealthService(basePath);
        var activityService = new ActivityService(basePath);
        var metService = new MetService(basePath);
        var dietService = new DietService();
        var tdeeCalculator = new TdeeCalculator(healthService, activityService, metService, dietService);
        var tdee = tdeeCalculator.getTdee(now());
        assertThat(tdee, is(equalTo(2688)));
    }
}