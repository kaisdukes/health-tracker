package health.tracker.photos;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.time.LocalDate;

import static health.tracker.photos.PhotoFilenameParser.parsePhotoFilename;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class PhotoFilenameParserTest {

    @Test
    void shouldParsePhotoFilename() {
        var photoInfo = parsePhotoFilename(Paths.get("2022-12-29-preworkout-unflexed.jpeg"));

        assertThat(
                photoInfo,
                is(equalTo(
                        new PhotoInfo(
                                LocalDate.of(2022, 12, 29),
                                new String[]{"pre-workout", "unflexed"}))));
    }
}