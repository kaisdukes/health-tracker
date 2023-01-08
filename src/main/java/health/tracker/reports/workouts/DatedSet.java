package health.tracker.reports.workouts;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DatedSet {
    LocalDate date;
    Integer reps;
    Double weightKg;
}