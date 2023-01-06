package health.tracker;

import lombok.Value;

import static java.lang.Double.parseDouble;

@Value
public class Amount {
    double value;
    Unit unit;

    public static Amount parseAmount(String text) {

        // no text?
        if (text == null || text.length() == 0) {
            throw new RuntimeException("No amount specified.");
        }

        // split
        var i = text.length() - 1;
        while (i >= 0 && isText(text.charAt(i))) {
            i--;
        }

        // no value?
        if (i == -1) {
            throw new RuntimeException("Invalid amount without value: '" + text + "'.");
        }

        // no unit?
        if (i == text.length() - 1) {
            throw new RuntimeException("Invalid amount without unit: '" + text + "'.");
        }

        // parse
        var value = parseDouble(text.substring(0, i + 1));
        var unit = Unit.getUnit(text.substring(i + 1));
        return new Amount(value, unit);
    }

    private static boolean isText(char ch) {
        return (ch >= 'a' && ch <= 'z') || ch == '%';
    }
}