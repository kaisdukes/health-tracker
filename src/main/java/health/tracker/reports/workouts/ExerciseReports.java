package health.tracker.reports.workouts;

import health.tracker.workouts.WorkoutService;
import lombok.SneakyThrows;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static health.tracker.text.DoubleFormat.formatDouble;
import static health.tracker.text.SentenceCase.toSentenceCase;
import static health.tracker.text.ShortDateFormat.formatShortDate;

public class ExerciseReports {
    private final Path docsPath;
    private final WorkoutService workoutService;
    private static final String REPORTS_FOLDER_NAME = "reports";
    private static final String EXERCISES_FOLDER_NAME = "exercises";

    public ExerciseReports(Path docsPath, WorkoutService workoutService) {
        this.docsPath = docsPath;
        this.workoutService = workoutService;
    }

    public void createReports() {
        var datedSetsByExercise = groupSetsByExercise();
        writeReports(datedSetsByExercise);
    }

    private void writeReports(Map<String, List<DatedSet>> datedSetsByExercise) {
        for (var e : datedSetsByExercise.entrySet()) {
            writeReport(e.getKey(), e.getValue());
        }
    }

    @SneakyThrows
    private void writeReport(String exercise, List<DatedSet> datedSets) {
        var outputPath = docsPath
                .resolve(REPORTS_FOLDER_NAME)
                .resolve(EXERCISES_FOLDER_NAME)
                .resolve(exercise.replace(' ', '-') + ".md");

        LocalDate date = null;
        try (var writer = new BufferedWriter(new FileWriter(outputPath.toFile()))) {
            writer.write("## " + toSentenceCase(exercise));
            for (var datedSet : datedSets) {

                // new date?
                if (!datedSet.getDate().equals(date)) {
                    date = datedSet.getDate();
                    writer.write("\n\n### " + formatShortDate(date) + '\n');
                    writer.write("\n| Weight (kg) | Reps |");
                    writer.write("\n| ----------- | ---- |");
                }

                // set
                writer.write("\n| ");
                writer.write(datedSet.getWeightKg() != null ? formatDouble(datedSet.getWeightKg()) : "n/a");
                writer.write(" | ");
                writer.write(datedSet.getReps() != null ? formatDouble(datedSet.getReps()) : "n/a");
                writer.write(" |");
            }
        }
    }

    private Map<String, List<DatedSet>> groupSetsByExercise() {
        var datedSetsByExercise = new HashMap<String, List<DatedSet>>();
        for (var workout : workoutService.getWorkouts()) {
            for (var set : workout.getSets()) {

                var datedSet = new DatedSet();
                datedSet.setDate(workout.getDate());
                datedSet.setReps(set.getReps());
                datedSet.setWeightKg(set.getWeightKg());

                datedSetsByExercise
                        .computeIfAbsent(set.getExercise(), x -> new ArrayList<>())
                        .add(datedSet);
            }
        }
        return datedSetsByExercise;
    }
}