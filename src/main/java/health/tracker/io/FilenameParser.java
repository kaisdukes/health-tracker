package health.tracker.io;

import java.time.LocalDate;

import static java.lang.Integer.parseInt;
import static java.lang.System.arraycopy;

public class FilenameParser {

    public FilenameInfo parse(String filename) {

        // parts
        var name = getFilenameWithoutExtension(filename);
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
        return new FilenameInfo(date, tags);
    }

    private static String getFilenameWithoutExtension(String filename) {
        return filename.substring(0, filename.lastIndexOf('.'));
    }
}