package health.tracker.reports;

import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Value
public class Table {
    List<Column> columns = new ArrayList<>();

    public List<String> allText() {
        return columns
                .stream()
                .flatMap(c -> Stream.concat(
                        Stream.of(c.getHeader()),
                        c.getValues().stream())).collect(toList());
    }
}