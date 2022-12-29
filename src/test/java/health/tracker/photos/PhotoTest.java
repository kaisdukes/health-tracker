package health.tracker.photos;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.File;

class PhotoTest {

    @Test
    @SneakyThrows
    void shouldReadPhoto() {
        var image = ImageIO.read(new File("data/photos/originals/2022-12-29-preworkout-unflexed.jpeg"));
        System.out.println("Loaded: " + image);
    }
}