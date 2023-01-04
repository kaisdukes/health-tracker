package health.tracker.timeseries;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

public class MostRecent {

    private MostRecent() {
    }

    // Uses binary search to find the most recent item in a
    // list by date. Assumes that the list is already ordered.
    public static <T> T getMostRecentItem(
            List<T> items, Function<T, LocalDate> dateSelector, LocalDate date) {

        var lowIndex = 0;
        var highIndex = items.size() - 1;
        T mostRecent = null;
        while (lowIndex <= highIndex) {

            // middle item
            var midIndex = (lowIndex + highIndex) / 2;
            var midItem = items.get(midIndex);
            var midDate = dateSelector.apply(midItem);

            // compare
            var comparison = midDate.compareTo(date);
            if (comparison > 0) {
                highIndex = midIndex - 1;
            } else if (comparison < 0) {
                mostRecent = midItem;
                lowIndex = midIndex + 1;
            } else {
                return midItem;
            }
        }

        // not found?
        if (mostRecent == null) {
            throw new RuntimeException("No most recent value for date " + date);
        }
        return mostRecent;
    }
}