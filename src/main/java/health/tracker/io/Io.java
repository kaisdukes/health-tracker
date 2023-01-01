package health.tracker.io;

import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

public class Io {

    private Io() {
    }

    @SneakyThrows
    public static void processFiles(Path path, String extension, Consumer<Path> fileProcessor) {
        Files.walk(path)
                .filter(p -> !Files.isDirectory(p))
                .filter(p -> p.getFileName().toString().endsWith('.' + extension))
                .forEach(fileProcessor);
    }

    public static String getFilenameWithoutExtension(Path path) {
        var filename = path.getFileName().toString();
        return filename.substring(0, filename.lastIndexOf('.'));
    }
}