package health.tracker.activity;

import health.tracker.io.FilenameInfo;

import java.nio.file.Path;

import static health.tracker.io.Io.getFilesOrderedByDate;

public class ActivityService {
    private static final String ACTIVITY_FOLDER_NAME = "activity";

    public ActivityService(Path basePath) {
        getFilesOrderedByDate(
                basePath.resolve(ACTIVITY_FOLDER_NAME), "tsv")
                .forEach(this::readActivities);
    }

    private void readActivities(final FilenameInfo filenameInfo) {
    }
}