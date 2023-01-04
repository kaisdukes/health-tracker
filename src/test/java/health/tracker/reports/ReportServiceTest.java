package health.tracker.reports;

import org.junit.jupiter.api.Test;

import static health.tracker.TestContext.BASE_PATH;

class ReportServiceTest {

    @Test
    void shouldCreateReports() {
        var reportService = new ReportService(BASE_PATH);
        reportService.createReports();
    }
}