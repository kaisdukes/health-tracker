package health.tracker.timeseries;

import java.time.LocalDate;
import java.util.*;

import static health.tracker.timeseries.MostRecent.getMostRecentItem;

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
        if (value == null) throw new RuntimeException("No time series value for date " + date);
        return value;
    }

    public Point getMostRecent(LocalDate date) {
        return getMostRecentItem(points, Point::getDate, date);
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