package health.tracker.forecasting;

import lombok.Value;

import java.time.LocalDate;

@Value
public class Projection {
    int lookbackWeekCount;
    LocalDate date;
    int weightKg;
}