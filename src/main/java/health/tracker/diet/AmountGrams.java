package health.tracker.diet;

import static java.lang.Integer.parseInt;

public class AmountGrams {

    private AmountGrams() {
    }

    public static int parseAmountGrams(String text) {
        if (!text.endsWith("g")) {
            throw new RuntimeException("Expected an amount type in grams, not '" + text + "'.");
        }
        return parseInt(text.substring(0, text.length() - 1));
    }
}