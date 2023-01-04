package health.tracker.diet;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class Diet {
    LocalDate date;
    int protein;
    int carbs;
    int fat;
    int kcal;
    int fiber;
    List<Portion> portions = new ArrayList<>();
}