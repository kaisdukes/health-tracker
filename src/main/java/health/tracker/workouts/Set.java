package health.tracker.workouts;

import lombok.Data;

@Data
public class Set {
    String exercise;
    Integer reps;
    Double weightKg;
}