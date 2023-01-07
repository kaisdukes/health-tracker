package health.tracker.workouts;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static health.tracker.TestContext.DATA_PATH;

class HeavySetImporterTest {

    @Test
    @Disabled
    void shouldImportWorkouts() {
        var home = System.getProperty("user.home");
        var path = Paths.get(home, "Downloads", "heavyset-export.csv");
        var importer = new HeavySetImporter(DATA_PATH);
        importer.importFile(path);
    }
}