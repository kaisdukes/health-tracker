package health.tracker.tsv;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;

public class TsvReader {
    private final BufferedReader reader;
    private final String[] columns;
    private String[] values;

    @SneakyThrows
    public TsvReader(Path path) {
        this.reader = new BufferedReader(new FileReader(path.toFile()));

        // header
        var line = reader.readLine();
        if (line == null) {
            throw new RuntimeException("Failed to read header row from TSV file: " + path);
        }
        this.columns = line.split("\\t");
    }

    public String[] getColumns() {
        return columns;
    }

    @SneakyThrows
    public boolean next() {
        var line = reader.readLine();
        if (line == null) return false;
        this.values = line.split("\\t");
        return true;
    }

    public String[] getValues() {
        return values;
    }
}