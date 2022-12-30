package health.tracker.photos;

import health.tracker.metrics.HealthService;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

class PhotoAnnotatorTest {

    @Test
    void shouldAnnotatePhotos() {
        var basePath = Paths.get("data");
        var healthService = new HealthService(basePath);
        var annotator = new PhotoAnnotator(basePath, healthService);
        annotator.annotate();
    }
}