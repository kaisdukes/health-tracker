package health.tracker.reports.records;

import health.tracker.workouts.Set;
import health.tracker.workouts.WorkoutService;
import lombok.SneakyThrows;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

import static health.tracker.text.DoubleFormat.formatDouble;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class RecordsReport {
    private final Path docsPath;
    private final WorkoutService workoutService;
    private static final int MIN_REPS = 8;
    private static final int MAX_REPS = 12;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public RecordsReport(Path docsPath, WorkoutService workoutService) {
        this.docsPath = docsPath;
        this.workoutService = workoutService;
    }

    public void createReport() {
        var records = computeRecords();
        writeReport(records);
    }

    @SneakyThrows
    private void writeReport(List<PersonalRecord> records) {
        var outputPath = docsPath.resolve("personal-records.md");
        try (var writer = new BufferedWriter(new FileWriter(outputPath.toFile()))) {
            writer.write("### Personal records (" + MIN_REPS + '-' + MAX_REPS + " reps)\n");
            writer.write("\n| Exercise | Weight (kg) | Reps | Date |");
            writer.write("\n| -------- | ----------- | ---- | ---- |");
            for (var record : records) {
                writer.write("\n| ");
                writer.write(record.getExercise());
                writer.write(" | ");
                writer.write(record.getWeightKg() != null ? formatDouble(record.getWeightKg()) : "n/a");
                writer.write(" | ");
                writer.write(Integer.toString(record.getReps()));
                writer.write(" | ");
                writer.write(DATE_FORMATTER.format(record.getDate()));
                writer.write(" |");
            }
        }
    }

    private List<PersonalRecord> computeRecords() {

        // compute
        var records = new HashMap<String, PersonalRecord>();
        for (var workout : workoutService.getWorkouts()) {
            for (var set : workout.getSets()) {
                var exercise = set.getExercise();
                var record = records.get(exercise);
                if (isRecord(record, set)) {
                    records.put(
                            exercise,
                            new PersonalRecord(
                                    workout.getDate(),
                                    exercise,
                                    set.getReps(),
                                    set.getWeightKg()));
                }
            }
        }

        // sort
        return records
                .values()
                .stream()
                .sorted(comparing(PersonalRecord::getExercise))
                .collect(toList());

    }

    private boolean isRecord(PersonalRecord record, Set set) {

        // first set?
        if (record == null) {
            return set.getReps() > 1;
        }

        // if below threshold, need a higher number of reps
        if (set.getReps() < MIN_REPS) {
            return set.getReps() > record.getReps();
        }

        // if the weight is the same, need a higher number of reps
        var existingWeight = record.getWeightKg() != null ? record.getWeightKg() : 0;
        var newWeight = set.getWeightKg() != null ? set.getWeightKg() : 0;
        if (newWeight == existingWeight) {
            return set.getReps() > record.getReps();
        }

        // compare weights
        return newWeight > existingWeight;
    }
}