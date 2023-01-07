package health.tracker;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TestContext {
    public static final Path DATA_PATH = Paths.get("data");
    public static final Path DOCS_PATH = Paths.get("docs");

    private TestContext() {
    }
}