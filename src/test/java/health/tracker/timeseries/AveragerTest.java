package health.tracker.timeseries;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class AveragerTest {

    @Test
    void shouldComputeAverageSeries() {

        var timeSeries = new TimeSeries();
        var dates = new LocalDate[10];
        for (var i = 0; i < dates.length; i++) {
            dates[i] = LocalDate.of(2020, 1, i + 1);
        }

        timeSeries.add(dates[0], 5.1);
        timeSeries.add(dates[1], 4.2);
        timeSeries.add(dates[2], 3.4);
        timeSeries.add(dates[3], 9.6);
        timeSeries.add(dates[4], 7.2);
        timeSeries.add(dates[5], 1.3);
        timeSeries.add(dates[6], 2.7);
        timeSeries.add(dates[7], 4.2);
        timeSeries.add(dates[8], 8.9);
        timeSeries.add(dates[9], 3.0);
        assertThat(timeSeries.size(), is(equalTo(10)));

        var averager = new Averager();
        var averagedTimeSeries = averager.computeAverageSeries(timeSeries, 7);
        assertThat(averagedTimeSeries.size(), is(equalTo(4)));

        var tolerance = 1e-2;

        assertThat(averagedTimeSeries.get(0).getDate(), is(equalTo(dates[6])));
        assertThat(averagedTimeSeries.get(0).getValue(), is(closeTo(4.79, tolerance)));

        assertThat(averagedTimeSeries.get(1).getDate(), is(equalTo(dates[7])));
        assertThat(averagedTimeSeries.get(1).getValue(), is(closeTo(4.66, tolerance)));

        assertThat(averagedTimeSeries.get(2).getDate(), is(equalTo(dates[8])));
        assertThat(averagedTimeSeries.get(2).getValue(), is(closeTo(5.33, tolerance)));

        assertThat(averagedTimeSeries.get(3).getDate(), is(equalTo(dates[9])));
        assertThat(averagedTimeSeries.get(3).getValue(), is(closeTo(5.27, tolerance)));
    }
}