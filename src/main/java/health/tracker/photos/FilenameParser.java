package health.tracker.photos;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class FilenameParser {
    private final Map<String, String> keywords = new HashMap<>();

    public FilenameParser() {
        keywords.put("preworkout", "pre-workout");
        keywords.put("postworkout", "post-workout");
        keywords.put("flexed", "flexed");
        keywords.put("unflexed", "unflexed");
    }

    public PhotoInfo parse(String filename) {

        // parts
        var name = getFilenameWithoutExtension(filename);
        var parts = name.split("-");

        // date
        var year = parseInt(parts[0]);
        var month = parseInt(parts[1]);
        var day = parseInt(parts[2]);
        var date = LocalDate.of(year, month, day);

        // keywords
        var size = parts.length - 3;
        var mappedKeywords = new String[size];
        for (var i = 0; i < size; i++) {
            var keyword = parts[i + 3];
            var mappedKeyword = keywords.get(keyword);
            if (mappedKeyword == null) {
                throw new RuntimeException("Keyword '" + keyword + "' not recognized.");
            }
            mappedKeywords[i] = mappedKeyword;
        }

        return new PhotoInfo(date, mappedKeywords);
    }

    private static String getFilenameWithoutExtension(String filename) {
        return filename.substring(0, filename.lastIndexOf('.'));
    }
}