package health.tracker.photos;

import health.tracker.metrics.HealthService;
import org.junit.jupiter.api.Test;

import static health.tracker.TestContext.BASE_PATH;

class PhotoAnnotatorTest {

    @Test
    void shouldAnnotatePhotos() {
        var healthService = new HealthService(BASE_PATH);
        var annotator = new PhotoAnnotator(BASE_PATH, healthService);
        annotator.annotate();
    }
}