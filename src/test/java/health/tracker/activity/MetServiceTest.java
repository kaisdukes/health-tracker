package health.tracker.activity;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MetServiceTest {
    private final MetService metService = new MetService(Paths.get("data"));

    @Test
    void shouldGetMets() {
        assertThat(metService.getMet("sleeping"), is(equalTo(0.9)));
    }

    @Test
    void shouldThrowForMissingMetLookup() {
        assertThrows(
                RuntimeException.class,
                () -> metService.getMet("invalid"));
    }
}