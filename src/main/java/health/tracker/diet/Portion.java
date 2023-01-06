package health.tracker.diet;

import health.tracker.Amount;
import lombok.Data;

@Data
public class Portion {
    String meal;
    String name;
    String brand;
    Amount amount;
    Double quantity;
    double kcal;
    double protein;
    double carbs;
    double fat;
    double fiber;
}