package health.tracker.reports.workouts;

import health.tracker.workouts.Set;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExerciseSetMap {
    private final String name;
    private final Map<LocalDate, List<Set>> setsByDate = new HashMap<>();

    public ExerciseSetMap(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void add(LocalDate date, Set set) {
        setsByDate
                .computeIfAbsent(date, x -> new ArrayList<>())
                .add(set);
    }

    public Iterable<LocalDate> getDates() {
        return setsByDate.keySet();
    }

    public List<Set> getSets(LocalDate date) {
        return setsByDate.get(date);
    }
}