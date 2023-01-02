package health.tracker.activity;

import health.tracker.io.FilenameInfo;
import health.tracker.tsv.TsvReader;
import health.tracker.tsv.TsvSchemaValidator;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static health.tracker.io.Io.getFilesOrderedByDate;
import static health.tracker.timeseries.MostRecent.getMostRecentItem;

public class ActivityService {
    private final TsvSchemaValidator validator = new TsvSchemaValidator(
            "Activity", "MinutesPerDay", "DaysPerWeek");
    private final List<DailyActivities> activities = new ArrayList<>();
    private static final String ACTIVITY_FOLDER_NAME = "activity";

    public ActivityService(Path basePath) {
        getFilesOrderedByDate(
                basePath.resolve(ACTIVITY_FOLDER_NAME), "tsv")
                .forEach(this::readDailyActivities);
    }

    public DailyActivities getActivities(LocalDate date) {
        return getMostRecentItem(activities, DailyActivities::getDate, date);
    }

    private void readDailyActivities(final FilenameInfo filenameInfo) {

        // columns
        var reader = new TsvReader(filenameInfo.getPath());
        validator.validate(reader);

        // rows
        var dailyActivities = new DailyActivities(filenameInfo.getDate());
        while (reader.next()) {
            var text = reader.getValues();
            var activity = new Activity();
            activity.setName(text[0]);
            activity.setMinutesPerDay(Integer.parseInt(text[1]));
            activity.setDaysPerWeek(Double.parseDouble(text[2]));
            dailyActivities.getActivities().add(activity);
        }
        this.activities.add(dailyActivities);
    }
}