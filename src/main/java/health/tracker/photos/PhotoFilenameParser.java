package health.tracker.photos;

import health.tracker.io.FilenameParser;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PhotoFilenameParser {
    private final Map<String, String> keywords = new HashMap<>();
    private final FilenameParser filenameParser = new FilenameParser();

    public PhotoFilenameParser() {
        keywords.put("preworkout", "pre-workout");
        keywords.put("postworkout", "post-workout");
        keywords.put("flexed", "flexed");
        keywords.put("unflexed", "unflexed");
    }

    public PhotoInfo parse(Path path) {
        var filenameInfo = filenameParser.parse(path);
        var keywords = Arrays.stream(filenameInfo.getTags()).map(tag -> {
            var keyword = this.keywords.get(tag);
            if (keyword == null) {
                throw new RuntimeException("Tag '" + tag + "' not recognized.");
            }
            return keyword;
        }).toArray(String[]::new);
        return new PhotoInfo(filenameInfo.getDate(), keywords);
    }
}