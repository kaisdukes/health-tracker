package health.tracker.io;

import java.nio.file.Path;
import java.time.LocalDate;

import static health.tracker.io.Io.getFilenameWithoutExtension;
import static java.lang.Integer.parseInt;
import static java.lang.System.arraycopy;

public class FilenameParser {

    private FilenameParser() {
    }

    public static FilenameInfo parseFilename(Path path) {

        // parts
        var name = getFilenameWithoutExtension(path);
        var parts = name.split("-");

        // date
        var year = parseInt(parts[0]);
        var month = parseInt(parts[1]);
        var day = parseInt(parts[2]);
        var date = LocalDate.of(year, month, day);

        // tags
        var size = parts.length - 3;
        var tags = new String[size];
        arraycopy(parts, 3, tags, 0, size);
        return new FilenameInfo(path, date, tags);
    }
}