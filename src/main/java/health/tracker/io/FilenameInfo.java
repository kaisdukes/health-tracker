package health.tracker.io;

import lombok.Value;

import java.time.LocalDate;

@Value
public class FilenameInfo {
    LocalDate date;
    String[] tags;
}