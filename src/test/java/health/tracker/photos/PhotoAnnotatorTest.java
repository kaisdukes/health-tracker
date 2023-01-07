package health.tracker.photos;

import health.tracker.metrics.HealthService;
import org.junit.jupiter.api.Test;

import static health.tracker.TestContext.DATA_PATH;

class PhotoAnnotatorTest {

    @Test
    void shouldAnnotatePhotos() {
        var healthService = new HealthService(DATA_PATH);
        var annotator = new PhotoAnnotator(DATA_PATH, healthService);
        annotator.annotate();
    }
}