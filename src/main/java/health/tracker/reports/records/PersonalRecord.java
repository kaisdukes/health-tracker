package health.tracker.reports.records;

import lombok.Value;

import java.time.LocalDate;

@Value
public class PersonalRecord {
    LocalDate date;
    String exercise;
    int reps;
    Double weightKg;
}