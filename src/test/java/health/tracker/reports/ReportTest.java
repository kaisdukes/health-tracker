package health.tracker.reports;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

class ReportTest {

    @Test
    void shouldCreateReports() {
        var basePath = Paths.get("data");
        var reportService = new ReportService(basePath);
        reportService.createReports();
    }
}