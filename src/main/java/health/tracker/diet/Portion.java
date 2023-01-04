package health.tracker.diet;

import lombok.Data;

@Data
public class Portion {
    String meal;
    String name;
    String brand;
    Integer amountGrams;
    Double quantity;
    double kcal;
    double protein;
    double carbs;
    double fat;
    double fiber;
}