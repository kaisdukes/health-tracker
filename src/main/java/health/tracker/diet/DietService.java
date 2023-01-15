package health.tracker.diet;

import health.tracker.Unit;
import health.tracker.io.FilenameInfo;
import health.tracker.tsv.TsvReader;
import health.tracker.tsv.TsvSchemaValidator;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static health.tracker.Amount.parseAmount;
import static health.tracker.io.Io.getFilesOrderedByDate;
import static health.tracker.timeseries.MostRecent.getMostRecentItem;
import static java.lang.Double.parseDouble;

public class DietService {
    private final TsvSchemaValidator validator = new TsvSchemaValidator(
            "Meal", "Name", "Brand", "Amount", "Quantity");
    private final NutritionService nutritionService;
    private final List<Diet> diets = new ArrayList<>();
    private static final String DIET_FOLDER_NAME = "diet";

    public DietService(Path dataPath, NutritionService nutritionService) {
        this.nutritionService = nutritionService;
        getFilesOrderedByDate(
                dataPath.resolve(DIET_FOLDER_NAME), "tsv")
                .forEach(this::readDiet);
    }

    public LocalDate getStartDate() {
        return diets.get(0).getDate();
    }

    public LocalDate getEndDate() {
        return diets.get(diets.size() - 1).getDate();
    }

    public Diet getDiet(LocalDate date) {
        var diet = getMostRecentItem(diets, Diet::getDate, date);
        if (diet == null) {
            throw new RuntimeException("Failed to find most recent diet for " + date + '.');
        }
        return diet;
    }

    private void readDiet(final FilenameInfo filenameInfo) {

        // columns
        var reader = new TsvReader(filenameInfo.getPath());
        validator.validate(reader);

        // rows
        var diet = new Diet();
        diet.setDate(filenameInfo.getDate());
        while (reader.next()) {
            var text = reader.getValues();
            var portion = new Portion();
            portion.setMeal(text[0]);
            portion.setName(text[1]);
            portion.setBrand(!text[2].equals("any") ? text[2] : null);
            portion.setAmount(!text[3].equals("n/a") ? parseAmount(text[3]) : null);
            portion.setQuantity(!text[4].equals("n/a") ? parseDouble(text[4]) : null);
            diet.getPortions().add(portion);
        }

        // nutrition
        computeNutrition(diet);
        this.diets.add(diet);
    }

    private void computeNutrition(Diet diet) {
        for (var portion : diet.getPortions()) {
            var nutrition = nutritionService.getNutrition(
                    portion.getName(),
                    portion.getBrand(),
                    portion.getAmount());

            var multiplier = switch (nutrition.getAmountType()) {
                case ByQuantity -> {
                    if (portion.getQuantity() == null) {
                        throw new RuntimeException("Expected a quantity to be specified for '" + portion.getName() + ".");
                    }
                    yield portion.getQuantity();
                }
                case ByUnit -> {
                    if (portion.getAmount() == null) {
                        throw new RuntimeException("Expected an amount to be specified for '" + portion.getName() + "'.");
                    }
                    if (portion.getQuantity() != null) {
                        throw new RuntimeException("Expected no quantity to be specified for '" + portion.getName() + "'.");
                    }
                    yield portion.getAmount().to(nutrition.getByUnit()).getValue();
                }
                case NotApplicable -> 1;
            };
            portion.setKcal(multiplier * nutrition.getKcal());
            portion.setProtein(multiplier * nutrition.getProtein());
            portion.setCarbs(multiplier * nutrition.getCarbs());
            portion.setFat(multiplier * nutrition.getFat());
            portion.setFiber(multiplier * nutrition.getFiber());
        }
        computeMacros(diet);
    }

    private void computeMacros(Diet diet) {
        var kcal = 0d;
        var protein = 0d;
        var carbs = 0d;
        var fat = 0d;
        var fiber = 0d;
        var water = 0d;
        for (var portion : diet.getPortions()) {
            kcal += portion.getKcal();
            protein += portion.getProtein();
            carbs += portion.getCarbs();
            fat += portion.getFat();
            fiber += portion.getFiber();
            if (portion.getName().equals("water")) {
                water += portion.getAmount().to(Unit.Liter).getValue();
            }
        }
        diet.setKcal((int) Math.round(kcal));
        diet.setProtein((int) Math.round(protein));
        diet.setCarbs((int) Math.round(carbs));
        diet.setFat((int) Math.round(fat));
        diet.setFiber((int) Math.round(fiber));
        diet.setWater(water);
    }
}