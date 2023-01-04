package health.tracker.diet;

import lombok.Data;

@Data
public class Nutrition {
    String name;
    String brand;
    Integer amountGrams;
    AmountType amountType;
    double kcal;
    double protein;
    double carbs;
    double fat;
    double fiber;
}