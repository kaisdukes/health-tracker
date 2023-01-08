package health.tracker.text;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ShortDateFormat {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private ShortDateFormat() {
    }

    public static String formatShortDate(LocalDate date) {
        return DATE_FORMATTER.format(date);
    }
}