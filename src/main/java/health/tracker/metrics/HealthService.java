package health.tracker.metrics;

import health.tracker.timeseries.Averager;
import health.tracker.timeseries.TimeSeries;
import health.tracker.timeseries.TimeSeriesTsvReader;
import lombok.SneakyThrows;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static health.tracker.io.Io.getFilenameWithoutExtension;
import static health.tracker.io.Io.getFiles;

public class HealthService {
    private final Path dataPath;
    private final Map<Metric, TimeSeries> metrics = new HashMap<>();
    private final Map<Metric, TimeSeries> averagedMetrics = new HashMap<>();
    private static final String METRICS_FOLDER_NAME = "metrics";
    private static final int AVERAGE_DAY_COUNT = 7;

    public HealthService(Path dataPath) {
        this.dataPath = dataPath;
        loadMetrics();
    }

    public TimeSeries getTimeSeries(Metric metric) {

        // already exists
        var timeSeries = metrics.get(metric);
        if (timeSeries != null) {
            return timeSeries;
        }

        // create on first use
        switch (metric) {
            case FatMassKg -> {
                timeSeries = computeFatMass();
                metrics.put(metric, timeSeries);
                return timeSeries;
            }
            case BodyFatPercentage -> {
                timeSeries = computeBodyFatPercentage();
                metrics.put(metric, timeSeries);
                return timeSeries;
            }
        }

        // not found
        throw new RuntimeException("No time series data found for metric '" + metric + "'.");
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

        // use the averaged value for the date specified if we have one
        var averagedTimeSeries = getAveragedTimeSeries(metric);
        if (averagedTimeSeries.hasValue(date)) {
            return averagedTimeSeries.get(date);
        }

        // otherwise, try to fall back to the most recent averaged value
        var averaged = averagedTimeSeries.getMostRecent(date);
        if (averaged != null) {
            return averaged.getValue();
        }

        // if we don't have a most recent averaged value, use the most recent raw value if available
        var raw = getTimeSeries(metric).getMostRecent(date);
        if (raw != null) {
            return raw.getValue();
        }

        // not found
        throw new RuntimeException("Failed to find averaged " + metric.getDescription() + " for " + date + '.');
    }

    @SneakyThrows
    private void loadMetrics() {
        getFiles(
                dataPath.resolve(METRICS_FOLDER_NAME),
                "tsv")
                .forEach(path -> {
                    var metric = Metric.getMetric(getFilenameWithoutExtension(path));
                    var reader = new TimeSeriesTsvReader();
                    var timeSeries = reader.read(path);
                    metrics.put(metric, timeSeries);
                });
    }

    private TimeSeries computeFatMass() {
        var fatMass = new TimeSeries();
        var leanMass = getTimeSeries(Metric.LeanMassKg);
        var weight = getTimeSeries(Metric.WeightKg);

        for (var point : leanMass) {
            var date = point.getDate();
            if (weight.hasValue(date)) {
                fatMass.add(date, weight.get(date) - point.getValue());
            }
        }

        return fatMass;
    }

    private TimeSeries computeBodyFatPercentage() {
        var bodyFat = new TimeSeries();
        var fatMass = getTimeSeries(Metric.FatMassKg);
        var weight = getTimeSeries(Metric.WeightKg);

        for (var point : fatMass) {
            var date = point.getDate();
            if (weight.hasValue(date)) {
                bodyFat.add(date, 100 * point.getValue() / weight.get(date));
            }
        }

        return bodyFat;
    }
}