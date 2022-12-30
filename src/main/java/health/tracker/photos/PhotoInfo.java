package health.tracker.photos;

import lombok.Value;

import java.time.LocalDate;

@Value
public class PhotoInfo {
    LocalDate date;
    String[] keywords;
}