package health.tracker.activity;

import lombok.Data;

@Data
public class Activity {
    String name;
    double hoursPerDay;
    double daysPerWeek;
}