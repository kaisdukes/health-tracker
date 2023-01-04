package health.tracker.diet;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Diet {
    LocalDate date;
    int proteinGrams;
    int carbsGrams;
    int fatGrams;
    int kcal;
}