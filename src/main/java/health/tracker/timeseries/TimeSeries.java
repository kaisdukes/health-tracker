package health.tracker.timeseries;

import java.time.LocalDate;
import java.util.*;

public class TimeSeries implements Iterable<Point> {
    private final List<Point> points = new ArrayList<>();
    private final Map<LocalDate, Double> valuesByDate = new HashMap<>();

    public void add(Point point) {
        points.add(point);
        valuesByDate.put(point.getDate(), point.getValue());
    }

    public void add(LocalDate date, double value) {
        add(new Point(date, value));
    }

    public Point get(int index) {
        return points.get(index);
    }

    public double get(LocalDate date) {
        var value = valuesByDate.get(date);
        if (value == null) throw new UnsupportedOperationException("No time series value for date " + date);
        return value;
    }

    public double getMostRecent(LocalDate date) {
        for (var i = points.size() - 1; i >= 0; i--) {
            var point = points.get(i);
            if (point.getDate().compareTo(date) <= 0) {
                return point.getValue();
            }
        }
        throw new UnsupportedOperationException("No most recent time series value for date " + date);
    }

    public boolean hasValue(LocalDate date) {
        return valuesByDate.containsKey(date);
    }

    @Override
    public Iterator<Point> iterator() {
        return points.iterator();
    }

    public int size() {
        return points.size();
    }
}