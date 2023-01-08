package health.tracker.reports.workouts;

import health.tracker.workouts.Set;
import health.tracker.workouts.WorkoutService;
import lombok.SneakyThrows;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.stream.StreamSupport;

import static health.tracker.text.DoubleFormat.formatDouble;
import static health.tracker.text.SentenceCase.toSentenceCase;
import static health.tracker.text.ShortDateFormat.formatShortDate;
import static java.util.Comparator.reverseOrder;

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
        var exercises = groupSetsByExercise();
        for (var exercise : exercises) {
            writeReport(exercise);
        }
    }

    @SneakyThrows
    private void writeReport(ExerciseSetMap exercise) {
        var dates = StreamSupport
                .stream(exercise.getDates().spliterator(), false)
                .sorted(reverseOrder());

        var outputPath = docsPath
                .resolve(REPORTS_FOLDER_NAME)
                .resolve(EXERCISES_FOLDER_NAME)
                .resolve(exercise.getName().replace(' ', '-') + ".md");

        try (var writer = new BufferedWriter(new FileWriter(outputPath.toFile()))) {
            writer.write("## " + toSentenceCase(exercise.getName()));
            dates.forEach(date -> writeFoo(writer, date, exercise.getSets(date)));
        }
    }

    @SneakyThrows
    private void writeFoo(Writer writer, LocalDate date, List<Set> sets) {

        // header
        writer.write("\n\n### " + formatShortDate(date) + '\n');
        writer.write("\n| Weight (kg) | Reps |");
        writer.write("\n| ----------- | ---- |");

        // sets
        for (var set : sets) {
            writer.write("\n| ");
            writer.write(set.getWeightKg() != null ? formatDouble(set.getWeightKg()) : "n/a");
            writer.write(" | ");
            writer.write(set.getReps() != null ? formatDouble(set.getReps()) : "n/a");
            writer.write(" |");
        }
    }

    private Iterable<ExerciseSetMap> groupSetsByExercise() {
        var exercises = new HashMap<String, ExerciseSetMap>();
        for (var workout : workoutService.getWorkouts()) {
            for (var set : workout.getSets()) {
                exercises
                        .computeIfAbsent(set.getExercise(), x -> new ExerciseSetMap(set.getExercise()))
                        .add(workout.getDate(), set);
            }
        }
        return exercises.values();
    }
}