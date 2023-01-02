package health.tracker.activity;

import lombok.Value;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Value
public class DailyActivities {
    LocalDate date;
    List<Activity> activities;

    public DailyActivities(LocalDate date) {
        this.date = date;
        this.activities = new ArrayList<>();
    }
}