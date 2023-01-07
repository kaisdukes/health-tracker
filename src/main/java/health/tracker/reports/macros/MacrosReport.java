package health.tracker.reports.macros;

import health.tracker.activity.ActivityService;
import health.tracker.activity.MetService;
import health.tracker.activity.TdeeCalculator;
import health.tracker.diet.Diet;
import health.tracker.diet.DietService;
import health.tracker.metrics.HealthService;
import health.tracker.metrics.Metric;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;

import static health.tracker.text.SentenceCase.toSentenceCase;
import static java.time.LocalDate.now;

public class MacrosReport {
    private final Path docsPath;
    private final HealthService healthService;
    private final ActivityService activityService;
    private final MetService metService;
    private final DietService dietService;
    private final NumberFormat VALUE_FORMATTER = new DecimalFormat("0.#");
    private static final NumberFormat MACRO_PERCENTAGE_FORMATTER = NumberFormat.getInstance();
    private static final String REPORTS_FOLDER_NAME = "reports";
    private static final String MACROS_FOLDER_NAME = "macros";

    public MacrosReport(
            Path docsPath,
            HealthService healthService,
            ActivityService activityService,
            MetService metService,
            DietService dietService) {

        this.docsPath = docsPath;
        this.healthService = healthService;
        this.activityService = activityService;
        this.metService = metService;
        this.dietService = dietService;
        MACRO_PERCENTAGE_FORMATTER.setMinimumFractionDigits(1);
        MACRO_PERCENTAGE_FORMATTER.setMaximumFractionDigits(1);
    }

    @SneakyThrows
    public void createReports() {
        var startDate = dietService.getStartDate();
        var endDate = now();
        String lastReport = null;
        for (var date = startDate; date.compareTo(endDate) <= 0; date = date.plusDays(1)) {
            var report = getMacrosReport(date);
            if (!report.equals(lastReport)) {
                var outputPath = docsPath
                        .resolve(REPORTS_FOLDER_NAME)
                        .resolve(MACROS_FOLDER_NAME)
                        .resolve(date + "-macros.md");
                Files.writeString(outputPath, report);
                lastReport = report;
            }
        }
    }

    private String getMacrosReport(LocalDate date) {
        var text = new StringBuilder();
        var diet = dietService.getDiet(date);

        String meal = null;
        for (var portion : diet.getPortions()) {

            // new meal?
            if (!portion.getMeal().equals(meal)) {
                meal = portion.getMeal();
                var macros = computeMacros(meal, diet);
                if (text.length() != 0) {
                    text.append("\n\n");
                }
                text.append("### ").append(meal);

                // macros?
                if (macros.getProtein() > 0 || macros.getCarbs() > 0) {
                    text.append(" (");
                    formatMealMacros(text, "protein", macros.getProtein());
                    text.append(", ");
                    formatMealMacros(text, "carbs", macros.getCarbs());
                    text.append(")");
                }
                text.append('\n');
            }

            // quantity
            text.append("\n- ");
            if (portion.getQuantity() != null && portion.getQuantity() != 1) {
                text.append(VALUE_FORMATTER.format(portion.getQuantity()));
                text.append(" x ");
            }

            // amount
            if (portion.getAmount() != null) {
                text.append(VALUE_FORMATTER.format(portion.getAmount().getValue()));
                text.append(portion.getAmount().getUnit().getSymbol());
                text.append(' ');
            }

            // portion
            text.append(portion.getName());

            // brand
            if (portion.getBrand() != null) {
                text.append(" (");
                text.append(portion.getBrand());
                text.append(')');
            }
        }

        // metrics
        text.append("\n\n### Metrics\n");
        formatMetric(text, date, Metric.WeightKg);
        formatMetric(text, date, Metric.BodyFatPercentage);
        formatMetric(text, date, Metric.LeanMassKg);

        // macros (kcal)
        text.append("\n\n### Macros\n");
        var proteinKcal = diet.getProtein() * 4;
        var carbsKcal = diet.getCarbs() * 4;
        var fatKcal = diet.getFat() * 9;
        var macrosKcal = proteinKcal + carbsKcal + fatKcal;

        // macros (%)
        formatMacros(text, date, "protein", diet.getProtein(), proteinKcal, macrosKcal);
        formatMacros(text, date, "carbs", diet.getCarbs(), carbsKcal, macrosKcal);
        formatMacros(text, date, "fat", diet.getFat(), fatKcal, macrosKcal);

        // fiber
        text.append("\n- Fiber: ").append(diet.getFiber()).append('g');

        // water
        text.append("\n- Water: ").append(VALUE_FORMATTER.format(diet.getWater())).append('l');

        // calories
        text.append("\n\n### Calories\n");
        text.append("\n- Diet: ").append(diet.getKcal()).append(" kcal");

        // TDEE
        var tdeeCalculator = new TdeeCalculator(healthService, activityService, metService, dietService);
        var tdee = tdeeCalculator.getTdee(date);
        text.append("\n- TDEE estimate: ").append(tdee).append(" kcal");
        var surplus = diet.getKcal() - tdee;
        if (surplus >= 0) {
            text.append("\n- Calorie surplus: ").append(surplus).append(" kcal");
            text.append("\n- Fat gain per week (if no change in lean mass): ");
            text.append((int) Math.round(surplus * 7 / 7.7));
        } else {
            text.append("\n- Calorie deficit: ").append(-surplus).append(" kcal");
            text.append("\n- Fat loss per week (if no change in lean mass): ");
            text.append((int) Math.round(-surplus * 7 / 7.7));
        }
        text.append('g');
        return text.toString();
    }

    private void formatMacros(
            StringBuilder text,
            LocalDate date,
            String name,
            int grams,
            int kcal,
            int macrosKcal) {

        text.append("\n- ");
        text.append(toSentenceCase(name));
        text.append(": ");
        text.append(grams);
        text.append("g (");
        text.append((int) Math.round(100d * kcal / macrosKcal));
        text.append("%) ");
        var weight = (int) Math.round(healthService.getAveragedMetric(date, Metric.WeightKg));
        text.append(MACRO_PERCENTAGE_FORMATTER.format(grams / (double) weight));
        text.append(" g/kg");
    }

    private void formatMetric(StringBuilder text, LocalDate date, Metric metric) {
        text.append("\n- ");
        text.append(toSentenceCase(metric.getDescription()));
        text.append(": ");
        text.append((int) Math.round(healthService.getAveragedMetric(date, metric)));
        text.append(metric.getUnit().getSymbol());
    }

    private void formatMealMacros(StringBuilder text, String name, int value) {
        if (value != 0) {
            text.append(value);
            text.append('g');
        } else {
            text.append("zero");
        }
        text.append(' ').append(name);
    }

    private MealNutrition computeMacros(String meal, Diet diet) {
        var protein = 0d;
        var carbs = 0d;
        for (var portion : diet.getPortions()) {
            if (portion.getMeal().equals(meal)) {
                protein += portion.getProtein();
                carbs += portion.getCarbs();
            }
        }
        return new MealNutrition((int) Math.round(protein), (int) Math.round(carbs));
    }
}