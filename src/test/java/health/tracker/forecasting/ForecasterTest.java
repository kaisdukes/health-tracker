package health.tracker.forecasting;

import health.tracker.metrics.HealthService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static health.tracker.TestContext.DATA_PATH;

class ForecasterTest {

    @Test
    @Disabled
    void shouldProjectBodyFatPercentage() {
        var healthService = new HealthService(DATA_PATH);
        var forecaster = new Forecaster(healthService);
        forecaster.projectBodyFatPercentage(12);
    }
}