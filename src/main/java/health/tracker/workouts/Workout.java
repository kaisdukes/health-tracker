package health.tracker.workouts;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class Workout {
    String name;
    LocalDate date;
    List<Set> sets = new ArrayList<>();
}