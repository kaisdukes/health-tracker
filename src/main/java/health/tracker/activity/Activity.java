package health.tracker.activity;

import lombok.Data;

@Data
public class Activity {
    String name;
    int minutesPerDay;
    double daysPerWeek;
}