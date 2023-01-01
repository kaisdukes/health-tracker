package health.tracker.timeseries;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

class TimeSeriesTsvReaderTest {

    @Test
    void shouldReadTimeSeries() {
        var reader = new TimeSeriesTsvReader();
        var timeSeries = reader.read(Paths.get("data/metrics/weight.tsv"));
        assertThat(timeSeries.size(), is(greaterThan(0)));
    }
}