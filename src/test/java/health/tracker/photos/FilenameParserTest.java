package health.tracker.photos;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class FilenameParserTest {

    @Test
    void shouldParseFilename() {
        var filenameParser = new FilenameParser();
        var photoInfo = filenameParser.parse("2022-12-29-preworkout-unflexed.jpeg");

        assertThat(
                photoInfo,
                is(equalTo(
                        new PhotoInfo(
                                LocalDate.of(2022, 12, 29),
                                new String[]{"pre-workout", "unflexed"}))));
    }
}