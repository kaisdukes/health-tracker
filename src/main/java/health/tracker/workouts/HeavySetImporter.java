package health.tracker.workouts;

import de.siegmar.fastcsv.reader.NamedCsvReader;
import lombok.SneakyThrows;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static health.tracker.text.DoubleFormat.formatDouble;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.time.LocalDate.parse;

public class HeavySetImporter {
    private final Path dataPath;
    private static final String WORKOUTS_FOLDER_NAME = "workouts";

    public HeavySetImporter(Path dataPath) {
        this.dataPath = dataPath;
    }

    public void importFile(Path path) {

        // import
        var workouts = importWorkouts(path);

        // export
        for (var workout : workouts) {
            exportWorkout(workout);
        }
    }

    @SneakyThrows
    private List<Workout> importWorkouts(Path path) {
        Workout workout = null;
        var workouts = new ArrayList<Workout>();
        try (var reader = NamedCsvReader.builder().build(path)) {
            for (var row : reader) {

                // new workout?
                var date = parse(row.getField("Date").substring(0, 10));
                var workoutName = row.getField("Workout Name").toLowerCase();
                if (workout == null || !workout.getDate().equals(date) || !workout.getName().equals(workoutName)) {
                    workout = new Workout();
                    workout.setName(workoutName);
                    workout.setDate(date);
                    workouts.add(workout);
                }

                // set
                var set = new Set();
                set.setExercise(row.getField("Exercise Name").toLowerCase());
                set.setReps(parseInt(row.getField("Reps")));
                var weightKg = parseDouble(row.getField("Weight (kg)"));
                if (weightKg != 0) {
                    set.setWeightKg(weightKg);
                }
                workout.getSets().add(set);
            }
        }
        return workouts;
    }

    @SneakyThrows
    private void exportWorkout(Workout workout) {
        var outputPath = dataPath
                .resolve(WORKOUTS_FOLDER_NAME)
                .resolve(workout.getDate() + "-workout.tsv");

        try (var writer = new BufferedWriter(new FileWriter(outputPath.toFile()))) {

            // header
            writer.write("Workout\tExercise\tReps\tWeight");

            // sets
            for (var set : workout.getSets()) {
                writer.write('\n');
                writer.write(workout.getName());
                writer.write('\t');
                writer.write(set.getExercise());
                writer.write('\t');
                writer.write(Integer.toString(set.getReps()));
                writer.write('\t');
                if (set.getWeightKg() != null) {
                    writer.write(formatDouble(set.getWeightKg()));
                } else {
                    writer.write("n/a");
                }
            }
        }
    }
}