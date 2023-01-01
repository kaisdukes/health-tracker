package health.tracker.io;

import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;

public class Io {
    private Io() {
    }

    public static FilenameInfo[] getFilesOrderedByDate(Path path, String extension) {
        return getFiles(path, extension)
                .map(FilenameParser::parseFilename)
                .sorted(comparing(FilenameInfo::getDate))
                .toArray(FilenameInfo[]::new);
    }

    @SneakyThrows
    public static Stream<Path> getFiles(Path path, String extension) {
        return Files.walk(path)
                .filter(p -> !Files.isDirectory(p))
                .filter(p -> p.getFileName().toString().endsWith('.' + extension));
    }

    public static String getFilenameWithoutExtension(Path path) {
        var filename = path.getFileName().toString();
        return filename.substring(0, filename.lastIndexOf('.'));
    }
}