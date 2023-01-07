package health.tracker.exercises;

import health.tracker.tsv.TsvReader;
import health.tracker.tsv.TsvSchemaValidator;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ExerciseService {
    private final Map<String, Exercise> exercises = new HashMap<>();

    public ExerciseService(Path dataPath) {

        // columns
        var reader = new TsvReader(dataPath.resolve("exercises.tsv"));
        var validator = new TsvSchemaValidator("Name", "MainMuscleGroups");
        validator.validate(reader);

        // rows
        while (reader.next()) {
            var text = reader.getValues();
            var exercise = new Exercise();
            exercise.setName(text[0]);
            exercise.setMainMuscleGroups(text[1].split(","));
            exercises.put(exercise.getName(), exercise);
        }
    }

    public Exercise getExercise(String name) {
        var exercise = exercises.get(name);
        if (exercise == null) {
            throw new RuntimeException("Exercise '" + name + "' not found.");
        }
        return exercise;
    }
}