package health.tracker.activity;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static java.time.LocalDate.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

class ActivityServiceTest {

    @Test
    void shouldGetActivities() {
        var basePath = Paths.get("data");
        var activityService = new ActivityService(basePath);
        var today = now();
        var dailyActivities = activityService.getActivities(today);
        assertThat(dailyActivities.getDate(), is(Matchers.lessThanOrEqualTo(today)));
        assertThat(dailyActivities.getActivities().size(), is(greaterThan(0)));
    }
}