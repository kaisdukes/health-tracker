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
import static java.lang.Double.parseDouble;

public class ActivityService {
    private final TsvSchemaValidator validator = new TsvSchemaValidator(
            "Activity", "HoursPerDay", "DaysPerWeek");
    private final List<DailyActivities> activities = new ArrayList<>();
    private static final String ACTIVITY_FOLDER_NAME = "activity";

    public ActivityService(Path dataPath) {
        getFilesOrderedByDate(
                dataPath.resolve(ACTIVITY_FOLDER_NAME), "tsv")
                .forEach(this::readDailyActivities);
    }

    public List<Activity> getActivities(LocalDate date) {
        var dailyActivities = getMostRecentItem(activities, DailyActivities::getDate, date);
        if (dailyActivities == null) {
            throw new RuntimeException("Failed to find most recent daily activities for " + date + '.');
        }
        return dailyActivities.getActivities();
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
            activity.setHoursPerDay(parseDouble(text[1]));
            activity.setDaysPerWeek(parseDouble(text[2]));
            dailyActivities.getActivities().add(activity);
        }
        this.activities.add(dailyActivities);
    }
}