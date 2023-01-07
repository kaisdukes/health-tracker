package health.tracker.activity;

import health.tracker.tsv.TsvReader;
import health.tracker.tsv.TsvSchemaValidator;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Double.parseDouble;

public class MetService {
    private final Map<String, Met> mets = new HashMap<>();

    public MetService(Path dataPath) {

        // columns
        var reader = new TsvReader(dataPath.resolve("met.tsv"));
        var validator = new TsvSchemaValidator("Name", "MET");
        validator.validate(reader);

        // rows
        while (reader.next()) {
            var text = reader.getValues();
            var met = new Met();
            met.setName(text[0]);
            met.setMet(parseDouble(text[1]));
            mets.put(met.getName(), met);
        }
    }

    public double getMet(String name) {
        var met = mets.get(name);
        if (met == null) {
            throw new RuntimeException("Met '" + name + "' not recognized.");
        }
        return met.getMet();
    }
}