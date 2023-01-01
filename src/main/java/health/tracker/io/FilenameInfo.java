package health.tracker.io;

import lombok.Value;

import java.nio.file.Path;
import java.time.LocalDate;

@Value
public class FilenameInfo {
    Path path;
    LocalDate date;
    String[] tags;
}