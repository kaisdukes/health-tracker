package health.tracker.timeseries;

import health.tracker.tsv.TsvReader;
import health.tracker.tsv.TsvSchemaValidator;

import java.nio.file.Path;

import static java.lang.Double.parseDouble;
import static java.time.LocalDate.parse;

public class TimeSeriesTsvReader {
    private final TsvSchemaValidator validator = new TsvSchemaValidator("Date", "Value");

    public TimeSeries read(Path path) {

        // columns
        var reader = new TsvReader(path);
        validator.validate(reader);

        // rows
        var timeSeries = new TimeSeries();
        while (reader.next()) {
            var text = reader.getValues();
            var date = parse(text[0]);
            var value = parseDouble(text[1]);
            timeSeries.add(date, value);
        }
        return timeSeries;
    }
}