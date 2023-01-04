package health.tracker.reports.metrics;

import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
public class Column {
    String header;
    List<String> values = new ArrayList<>();
}