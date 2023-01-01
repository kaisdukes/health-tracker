package health.tracker.metrics;

import health.tracker.timeseries.Averager;
import health.tracker.timeseries.TimeSeries;
import health.tracker.timeseries.TimeSeriesTsvReader;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class HealthService {
    private final Path basePath;
    private final Map<Metric, TimeSeries> metrics = new HashMap<>();
    private final Map<Metric, TimeSeries> averagedMetrics = new HashMap<>();
    private static final String METRICS_FOLDER_NAME = "metrics";
    private static final int AVERAGE_DAY_COUNT = 7;

    public HealthService(Path basePath) {
        this.basePath = basePath;
        loadMetrics();
    }

    public TimeSeries getTimeSeries(Metric metric) {
        var timeSeries = metrics.get(metric);
        if (timeSeries == null) {
            if (metric == Metric.BodyFatPercentage) {
                timeSeries = computeBodyFatPercentage();
                metrics.put(metric, timeSeries);
            } else {
                throw new UnsupportedOperationException("No time series data found for metric '" + metric + "'.");
            }
        }
        return timeSeries;
    }

    public TimeSeries getAveragedTimeSeries(Metric metric) {
        var averagedTimeSeries = averagedMetrics.get(metric);
        if (averagedTimeSeries == null) {
            var averager = new Averager();
            averagedTimeSeries = averager.computeAverageSeries(getTimeSeries(metric), AVERAGE_DAY_COUNT);
            averagedMetrics.put(metric, averagedTimeSeries);
        }
        return averagedTimeSeries;
    }

    public double getAveragedMetric(LocalDate date, Metric metric) {
        var averagedTimeSeries = getAveragedTimeSeries(metric);
        return averagedTimeSeries.hasValue(date)
                ? averagedTimeSeries.get(date)
                : getTimeSeries(metric).getMostRecent(date);
    }

    @SneakyThrows
    private void loadMetrics() {
        Files.walk(basePath.resolve(METRICS_FOLDER_NAME))
                .filter(p -> !Files.isDirectory(p))
                .map(p -> p.getFileName().toString())
                .filter(p -> p.endsWith(".tsv"))
                .forEach(this::readTimeSeries);
    }

    private void readTimeSeries(String filename) {
        var metric = Metric.getMetric(getFilenameWithoutExtension(filename));
        var path = basePath.resolve(METRICS_FOLDER_NAME).resolve(filename);
        var reader = new TimeSeriesTsvReader();
        var timeSeries = reader.read(path);
        metrics.put(metric, timeSeries);
    }

    private static String getFilenameWithoutExtension(String filename) {
        return filename.substring(0, filename.lastIndexOf('.'));
    }

    private TimeSeries computeBodyFatPercentage() {
        var bodyFat = new TimeSeries();
        var leanMass = getTimeSeries(Metric.LeanMassKg);
        var weight = getTimeSeries(Metric.WeightKg);

        for (var point : leanMass) {
            var date = point.getDate();
            if (weight.hasValue(date)) {
                var weightValue = weight.get(date);
                var value = 100 * (weightValue - point.getValue()) / weightValue;
                bodyFat.add(date, value);
            }
        }

        return bodyFat;
    }
}