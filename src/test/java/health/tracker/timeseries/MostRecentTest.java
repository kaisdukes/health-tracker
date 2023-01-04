package health.tracker.timeseries;

import lombok.Value;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static health.tracker.timeseries.MostRecent.getMostRecentItem;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MostRecentTest {

    @Test
    void shouldGetMostRecentItemBeforeMidpoint() {
        var items = list(13, 14, 20, 28, 31);
        var mostRecent = getMostRecentItem(items, x -> x.date, date(21));
        assertThat(mostRecent.date, is(equalTo(date(20))));
    }

    @Test
    void shouldGetMostRecentItemAfterMidpoint() {
        var items = list(13, 14, 20, 28);
        var mostRecent = getMostRecentItem(items, x -> x.date, date(13));
        assertThat(mostRecent.date, is(equalTo(date(13))));
    }

    @Test
    void shouldGetMostRecentItemFromSingletonList() {
        var items = list(15);
        var mostRecent = getMostRecentItem(items, x -> x.date, date(20));
        assertThat(mostRecent.date, is(equalTo(date(15))));
    }

    @Test
    void shouldGetMostRecentItemFromSingletonListWithExactMatch() {
        var items = list(15);
        var mostRecent = getMostRecentItem(items, x -> x.date, date(15));
        assertThat(mostRecent.date, is(equalTo(date(15))));
    }

    @Test
    void shouldGetMostRecentItemFromListWithoutGaps() {
        var items = list(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
        var mostRecent = getMostRecentItem(items, x -> x.date, date(4));
        assertThat(mostRecent.date, is(equalTo(date(4))));
    }

    @Test
    void shouldThrowForMissingMostRecentItem() {
        var items = list(12, 13, 14, 15);
        assertThrows(
                RuntimeException.class,
                () -> getMostRecentItem(items, x -> x.date, date(11)));
    }

    private static List<Item> list(int... values) {
        return Arrays
                .stream(values)
                .mapToObj(v -> new Item(v, date(v)))
                .collect(toList());
    }

    @Value
    private static class Item {
        int value;
        LocalDate date;
    }

    private static LocalDate date(int value) {
        return LocalDate.of(2020, 1, value);
    }
}