package health.tracker.diet;

import health.tracker.Amount;
import health.tracker.Unit;
import health.tracker.tsv.TsvReader;
import health.tracker.tsv.TsvSchemaValidator;

import java.nio.file.Path;
import java.util.*;

import static health.tracker.Amount.parseAmount;
import static java.lang.Double.parseDouble;

public class NutritionService {
    private final Map<String, List<Nutrition>> nutritionByName = new HashMap<>();

    public NutritionService(Path basePath) {

        // columns
        var reader = new TsvReader(basePath.resolve("nutrition.tsv"));
        var validator = new TsvSchemaValidator(
                "Name",
                "Brand",
                "Amount",
                "kcal",
                "Protein",
                "Carbs",
                "Fat",
                "Fiber");
        validator.validate(reader);

        // rows
        while (reader.next()) {
            var text = reader.getValues();
            var nutrition = new Nutrition();

            // name
            nutrition.setName(text[0]);

            // brand
            var brandText = text[1];
            if (!brandText.equals("any")) {
                nutrition.setBrand(brandText);
            }

            // amount
            var amountText = text[2];
            if (amountText.equals("by quantity")) {
                nutrition.setAmountType(AmountType.ByQuantity);
            } else if (amountText.startsWith("per ")) {
                nutrition.setAmountType(AmountType.ByUnit);
                nutrition.setByUnit(Unit.getUnit(amountText.substring(4)));
            } else {
                nutrition.setAmount(parseAmount(amountText));
                nutrition.setAmountType(AmountType.ByQuantity);
            }

            // values
            nutrition.setKcal(parseDouble(text[3]));
            nutrition.setProtein(parseDouble(text[4]));
            nutrition.setCarbs(parseDouble(text[5]));
            nutrition.setFat(parseDouble(text[6]));
            nutrition.setFiber(parseDouble(text[7]));

            nutritionByName
                    .computeIfAbsent(nutrition.getName(), x -> new ArrayList<>())
                    .add(nutrition);
        }
    }

    public Nutrition getNutrition(String name, String brand, Amount amount) {
        var nutritionList = nutritionByName.get(name);
        if (nutritionList != null) {
            for (var nutrition : nutritionList) {
                if (Objects.equals(nutrition.getName(), name)
                        && Objects.equals(nutrition.getBrand(), brand)) {

                    switch (nutrition.getAmountType()) {
                        case ByQuantity -> {
                            if (amount == null || Objects.equals(nutrition.getAmount(), amount)) {
                                return nutrition;
                            }
                        }
                        case ByUnit -> {
                            return nutrition;
                        }
                    }
                }
            }
        }
        throw new RuntimeException("Nutrition data for '" + name + "' not found.");
    }
}