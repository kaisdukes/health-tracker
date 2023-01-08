package health.tracker.reports.workouts;

import health.tracker.exercises.ExerciseService;
import health.tracker.workouts.Set;
import health.tracker.workouts.WorkoutService;
import lombok.SneakyThrows;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static health.tracker.text.DoubleFormat.formatDouble;
import static health.tracker.text.SentenceCase.toSentenceCase;
import static java.util.Comparator.comparing;
import static java.util.Map.Entry.comparingByKey;
import static java.util.stream.Collectors.toList;

public class RecordsReport {
    private final Path docsPath;
    private final WorkoutService workoutService;
    private final ExerciseService exerciseService;
    private static final int MIN_REPS = 8;
    private static final int MAX_REPS = 12;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public RecordsReport(Path docsPath, WorkoutService workoutService, ExerciseService exerciseService) {
        this.docsPath = docsPath;
        this.workoutService = workoutService;
        this.exerciseService = exerciseService;
    }

    public void createReport() {
        var records = computeRecords();
        var recordsByMainMuscleGroup = groupRecordsByMainMuscleGroup(records);
        writeReport(recordsByMainMuscleGroup);
    }

    @SneakyThrows
    private void writeReport(Map<String, List<PersonalRecord>> recordsByMainMuscleGroup) {
        var outputPath = docsPath.resolve("personal-records.md");
        try (var writer = new BufferedWriter(new FileWriter(outputPath.toFile()))) {
            writer.write("## Personal records (" + MIN_REPS + '-' + MAX_REPS + " reps)");
            recordsByMainMuscleGroup
                    .entrySet()
                    .stream()
                    .sorted(comparingByKey())
                    .forEach(e -> writeRecords(writer, e.getKey(), e.getValue()));
        }
    }

    @SneakyThrows
    private void writeRecords(Writer writer, String mainMuscleGroup, List<PersonalRecord> records) {
        writer.write("\n\n### " + toSentenceCase(mainMuscleGroup) + '\n');
        writer.write("\n| Exercise | Weight (kg) | Reps | Date |");
        writer.write("\n| -------- | ----------- | ---- | ---- |");
        for (var record : records) {
            writer.write("\n| ");
            writer.write(toSentenceCase(record.getExercise()));
            writer.write(" | ");
            writer.write(record.getWeightKg() != null ? formatDouble(record.getWeightKg()) : "n/a");
            writer.write(" | ");
            writer.write(Integer.toString(record.getReps()));
            writer.write(" | ");
            writer.write(DATE_FORMATTER.format(record.getDate()));
            writer.write(" |");
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

    private Map<String, List<PersonalRecord>> groupRecordsByMainMuscleGroup(List<PersonalRecord> records) {
        var recordsByMainMuscleGroup = new HashMap<String, List<PersonalRecord>>();
        for (var record : records) {
            var exercise = exerciseService.getExercise(record.getExercise());
            for (var mainMuscleGroup : exercise.getMainMuscleGroups()) {
                recordsByMainMuscleGroup
                        .computeIfAbsent(mainMuscleGroup, x -> new ArrayList<>())
                        .add(record);
            }
        }
        return recordsByMainMuscleGroup;
    }

    private boolean isRecord(PersonalRecord record, Set set) {

        // no reps?
        if (set.getReps() == null) {
            return false;
        }

        // first set?
        if (record == null) {
            return true;
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