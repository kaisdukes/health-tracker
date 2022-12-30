package health.tracker.timeseries;

import lombok.Value;

import java.time.LocalDate;

@Value
public class Point {
    LocalDate date;
    double value;
}