package health.tracker.timeseries;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TimeSeriesTest {

    @Test
    void shouldGetPoints() {
        var timeSeries = new TimeSeries();
        timeSeries.add(LocalDate.of(2020, 1, 1), 5.5);
        timeSeries.add(LocalDate.of(2020, 1, 2), 5.6);
        timeSeries.add(LocalDate.of(2020, 1, 3), 5.7);

        assertThat(timeSeries.size(), is(equalTo(3)));

        assertThat(
                timeSeries.get(0),
                is(equalTo(new Point(LocalDate.of(2020, 1, 1), 5.5))));

        assertThat(
                timeSeries.get(1),
                is(equalTo(new Point(LocalDate.of(2020, 1, 2), 5.6))));

        assertThat(
                timeSeries.get(2),
                is(equalTo(new Point(LocalDate.of(2020, 1, 3), 5.7))));
    }

    @Test
    void shouldGetValueByDate() {
        var timeSeries = new TimeSeries();
        var date = LocalDate.of(2020, 1, 1);
        timeSeries.add(date, 5.5);

        assertThat(timeSeries.get(date), is(equalTo(5.5)));
    }

    @Test
    void shouldThrowForMissingValueLookup() {
        var timeSeries = new TimeSeries();
        assertThrows(
                RuntimeException.class,
                () -> timeSeries.get(LocalDate.of(2020, 1, 1)));
    }
}