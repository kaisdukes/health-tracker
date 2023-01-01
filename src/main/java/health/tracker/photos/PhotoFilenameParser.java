package health.tracker.photos;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static health.tracker.io.FilenameParser.parseFilename;

public class PhotoFilenameParser {
    private static final Map<String, String> keywords = new HashMap<>();

    private PhotoFilenameParser() {
    }

    static {
        keywords.put("preworkout", "pre-workout");
        keywords.put("postworkout", "post-workout");
        keywords.put("flexed", "flexed");
        keywords.put("unflexed", "unflexed");
    }

    public static PhotoInfo parsePhotoFilename(Path path) {
        var filenameInfo = parseFilename(path);
        var keywords = Arrays.stream(filenameInfo.getTags()).map(tag -> {
            var keyword = PhotoFilenameParser.keywords.get(tag);
            if (keyword == null) {
                throw new RuntimeException("Tag '" + tag + "' not recognized.");
            }
            return keyword;
        }).toArray(String[]::new);
        return new PhotoInfo(filenameInfo.getDate(), keywords);
    }
}