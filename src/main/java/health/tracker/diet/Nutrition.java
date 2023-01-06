package health.tracker.diet;

import health.tracker.Amount;
import lombok.Data;

@Data
public class Nutrition {
    String name;
    String brand;
    Amount amount;
    AmountType amountType;
    double kcal;
    double protein;
    double carbs;
    double fat;
    double fiber;
}