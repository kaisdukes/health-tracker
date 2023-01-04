package health.tracker.diet;

import org.junit.jupiter.api.Test;

import static health.tracker.TestContext.BASE_PATH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class NutritionServiceTest {

    @Test
    void shouldGetNutrition() {
        var nutritionService = new NutritionService(BASE_PATH);
        var nutrition = nutritionService.getNutrition("egg", null, 0);
        assertThat(nutrition.getAmountType(), is(equalTo(AmountType.ByQuantity)));
    }
}