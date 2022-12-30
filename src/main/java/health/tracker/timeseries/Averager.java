package health.tracker.timeseries;

public class Averager {

    public TimeSeries computeAverageSeries(TimeSeries timeSeries, int dayCount) {
        var averagedTimeSeries = new TimeSeries();
        var sum = 0d;
        for (var i = 0; i < timeSeries.size(); i++) {
            var point = timeSeries.get(i);
            sum += point.getValue();
            if (i >= dayCount - 1) {
                if (i > dayCount - 1) sum -= timeSeries.get(i - dayCount).getValue();
                var average = sum / dayCount;
                averagedTimeSeries.add(new Point(point.getDate(), average));
            }
        }
        return averagedTimeSeries;
    }
}