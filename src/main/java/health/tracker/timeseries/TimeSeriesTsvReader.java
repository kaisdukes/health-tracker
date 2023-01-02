package health.tracker.timeseries;

import health.tracker.tsv.TsvReader;
import health.tracker.tsv.TsvSchemaValidator;

import java.nio.file.Path;
import java.time.LocalDate;

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
            var date = LocalDate.parse(text[0]);
            var value = Double.parseDouble(text[1]);
            timeSeries.add(date, value);
        }
        return timeSeries;
    }
}