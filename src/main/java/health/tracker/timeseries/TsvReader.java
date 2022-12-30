package health.tracker.timeseries;

import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

public class TsvReader {

    @SneakyThrows
    public TimeSeries read(Path path) {
        var timeSeries = new TimeSeries();
        for (var line : Files.readAllLines(path)) {
            var parts = line.split("\\t");
            var date = LocalDate.parse(parts[0]);
            var value = Double.parseDouble(parts[1]);
            timeSeries.add(date, value);
        }
        return timeSeries;
    }
}