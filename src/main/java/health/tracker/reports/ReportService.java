package health.tracker.reports;

import health.tracker.metrics.HealthService;
import health.tracker.metrics.Metric;
import health.tracker.timeseries.TimeSeries;
import lombok.SneakyThrows;
import org.jfree.chart.ChartFactory;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeriesCollection;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

public class ReportService {
    private final Path basePath;
    private final HealthService healthService;
    private static final NumberFormat NUMBER_FORMATTER = NumberFormat.getInstance();
    private static final String REPORTS_FOLDER_NAME = "reports";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final int FONT_SIZE = 12;
    private static final int IMAGE_WIDTH = 1000;
    private static final int IMAGE_HEIGHT = 700;
    private static final int AVERAGE_DAY_COUNT = 7;

    public ReportService(Path basePath) {
        this.basePath = basePath;
        this.healthService = new HealthService(basePath);
        NUMBER_FORMATTER.setMinimumFractionDigits(2);
        NUMBER_FORMATTER.setMaximumFractionDigits(2);
    }

    public void createReports() {
        createReport(Metric.WeightKg);
    }

    @SneakyThrows
    private void createReport(Metric metric) {

        //  create a graphics context
        var width = IMAGE_WIDTH;
        var height = IMAGE_HEIGHT;
        var bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var g = bufferedImage.createGraphics();
        g.addRenderingHints(Map.of(
                RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY,
                RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY,
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

        // background
        g.setPaint(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // render time series
        var timeSeries = healthService.getAveragedTimeSeries(metric);
        var chartHeight = 2 * height / 3;
        var image = renderTimeSeries(
                timeSeries,
                toSentenceCase(metric.getDescription()) + " (7-Day Average)",
                width,
                chartHeight);
        g.drawImage(image, null, 0, 20);

        // render table
        var font = new Font("SansSerif", Font.PLAIN, FONT_SIZE);
        renderTable(g, 70, chartHeight + 40, font, getMetricsTable(metric));

        // done
        g.dispose();

        // write
        var outputPath = basePath.resolve(REPORTS_FOLDER_NAME).resolve(metric.getKey() + ".png");
        ImageIO.write(bufferedImage, "png", outputPath.toFile());
    }

    public BufferedImage renderTimeSeries(TimeSeries timeSeries, String title, int width, int height) {
        var series = new org.jfree.data.time.TimeSeries("series");
        var zoneId = ZoneId.systemDefault();
        for (var point : timeSeries) {
            series.add(
                    new Day(Date.from(point.getDate().atStartOfDay(zoneId).toInstant())),
                    point.getValue());
        }

        var dataset = new TimeSeriesCollection();
        dataset.addSeries(series);

        var chart = ChartFactory.createTimeSeriesChart(
                title,
                "date",
                "value",
                dataset,
                false,
                false,
                false);

        var plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.BLACK);
        plot.setOutlinePaint(Color.BLACK);
        plot.setDomainGridlinePaint(Color.GREEN);
        plot.setRangeGridlinePaint(Color.GREEN);

        plot.getDomainAxis().setLabelPaint(Color.WHITE);
        plot.getDomainAxis().setAxisLinePaint(Color.WHITE);
        plot.getDomainAxis().setTickLabelPaint(Color.BLACK);
        plot.getDomainAxis().setTickMarksVisible(false);

        plot.getRangeAxis().setLabelPaint(Color.WHITE);
        plot.getRangeAxis().setAxisLinePaint(Color.WHITE);
        plot.getRangeAxis().setTickLabelPaint(Color.BLACK);
        plot.getRangeAxis().setTickMarksVisible(false);

        return chart.createBufferedImage(width, height);
    }

    private void renderTable(Graphics2D g, int x, int y, Font font, Table table) {

        // measure
        var fm = g.getFontMetrics(font);
        var textWidth = 0;
        var textHeight = 0;
        var textOffsetY = 0;
        for (var text : table.allText()) {
            var bounds = fm.getStringBounds(text, g);
            textWidth = (int) Math.max(textWidth, Math.round(bounds.getWidth()));
            textHeight = (int) Math.max(textHeight, Math.round(bounds.getHeight()));
            textOffsetY = (int) Math.max(textOffsetY, Math.round(-bounds.getY()));
        }

        // render
        g.setFont(font);
        g.setPaint(Color.BLACK);
        var margin = (int) Math.round(0.25 * textHeight);
        var tx = x;
        for (var column : table.getColumns()) {
            var ty = y + textOffsetY;
            g.drawString(column.getHeader(), tx, ty);
            ty += textHeight + margin;
            for (var value : column.getValues()) {
                g.drawString(value, tx, ty);
                ty += textHeight + margin;
            }
            tx += textWidth + 3 * margin;
        }
    }

    private Table getMetricsTable(Metric metric) {
        var timeSeries = healthService.getTimeSeries(metric);
        var averagedTimeSeries = healthService.getAveragedTimeSeries(metric);

        var dateColumn = new Column("Date");
        var valueColumn = new Column(toSentenceCase(metric.getDescription() + " " + metric.getUnits()));
        var averageColumn = new Column(AVERAGE_DAY_COUNT + "-Day Average");
        var changeColumn = new Column(AVERAGE_DAY_COUNT + "-Day Change");

        var valueCount = AVERAGE_DAY_COUNT + 1;
        for (var i = 0; i < valueCount; i++) {
            var timeSeriesIndex = timeSeries.size() - valueCount + i;

            dateColumn.getValues().add(DATE_FORMATTER.format(
                    timeSeries.get(timeSeriesIndex).getDate()));

            valueColumn.getValues().add(NUMBER_FORMATTER.format(
                    timeSeries.get(timeSeriesIndex).getValue()));

            var averagedTimeSeriesIndex = averagedTimeSeries.size() - valueCount + i;

            averageColumn.getValues().add(NUMBER_FORMATTER.format(
                    averagedTimeSeries.get(averagedTimeSeriesIndex).getValue()));

            changeColumn.getValues().add(NUMBER_FORMATTER.format(
                    averagedTimeSeries.get(averagedTimeSeriesIndex - AVERAGE_DAY_COUNT).getValue()
                            - averagedTimeSeries.get(averagedTimeSeriesIndex).getValue()));
        }

        var table = new Table();
        table.getColumns().add(dateColumn);
        table.getColumns().add(valueColumn);
        table.getColumns().add(averageColumn);
        table.getColumns().add(changeColumn);
        return table;
    }

    private static String toSentenceCase(String text) {
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }
}