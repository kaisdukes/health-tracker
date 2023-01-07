package health.tracker.workouts;

import health.tracker.io.FilenameInfo;
import health.tracker.tsv.TsvReader;
import health.tracker.tsv.TsvSchemaValidator;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static health.tracker.io.Io.getFilesOrderedByDate;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class WorkoutService {
    private final TsvSchemaValidator validator = new TsvSchemaValidator(
            "Workout", "Exercise", "Reps", "Weight");
    private final List<Workout> workouts = new ArrayList<>();
    private static final String WORKOUTS_FOLDER_NAME = "workouts";

    public WorkoutService(Path dataPath) {
        getFilesOrderedByDate(
                dataPath.resolve(WORKOUTS_FOLDER_NAME), "tsv")
                .forEach(this::readWorkout);
    }

    public List<Workout> getWorkouts() {
        return workouts;
    }

    private void readWorkout(FilenameInfo filenameInfo) {

        // columns
        var reader = new TsvReader(filenameInfo.getPath());
        validator.validate(reader);

        // rows
        var workout = new Workout();
        workout.setDate(filenameInfo.getDate());
        while (reader.next()) {
            var text = reader.getValues();
            if (workout.getName() == null) {
                workout.setName(text[0]);
            }
            var set = new Set();
            set.setExercise(text[1]);
            set.setReps(!text[2].equals("n/a") ? parseInt(text[2]) : null);
            set.setWeightKg(!text[3].equals("n/a") ? parseDouble(text[3]) : null);
            workout.getSets().add(set);
        }
        this.workouts.add(workout);
    }
}