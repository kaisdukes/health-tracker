package health.tracker.diet;

import java.time.LocalDate;

public class DietService {

    public Diet getDiet(LocalDate date) {
        var diet = new Diet();
        diet.setProteinGrams(184);
        diet.setCarbsGrams(165);
        diet.setFatGrams(76);
        diet.setKcal(2088);
        return diet;
    }
}