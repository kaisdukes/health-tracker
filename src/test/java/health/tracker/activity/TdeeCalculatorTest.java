package health.tracker.activity;

import health.tracker.diet.DietService;
import health.tracker.metrics.HealthService;
import org.junit.jupiter.api.Test;

import static health.tracker.TestContext.BASE_PATH;
import static java.time.LocalDate.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class TdeeCalculatorTest {

    @Test
    void shouldGetTdee() {
        var healthService = new HealthService(BASE_PATH);
        var activityService = new ActivityService(BASE_PATH);
        var metService = new MetService(BASE_PATH);
        var dietService = new DietService();
        var tdeeCalculator = new TdeeCalculator(healthService, activityService, metService, dietService);
        var tdee = tdeeCalculator.getTdee(now());
        assertThat(tdee, is(equalTo(2688)));
    }
}