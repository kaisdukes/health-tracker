package health.tracker;

import lombok.Value;

import static java.lang.Double.parseDouble;

@Value
public class Amount {
    double value;
    Unit unit;

    public Amount to(Unit unit) {

        // no conversion?
        if (unit == this.unit) {
            return this;
        }

        // liter -> milliliter
        if (this.unit == Unit.Liter && unit == Unit.Milliliter) {
            return new Amount(value * 1000, Unit.Milliliter);
        }

        // milliliter -> liter
        if (this.unit == Unit.Milliliter && unit == Unit.Liter) {
            return new Amount(value * 0.001, Unit.Liter);
        }

        // no match
        throw new RuntimeException("Can't convert " + this.unit + " to " + unit);
    }

    public static Amount parseAmount(String text) {

        // no text?
        if (text == null || text.length() == 0) {
            throw new RuntimeException("No amount specified.");
        }

        // split
        var i = 0;
        while (i < text.length() && isNumeric(text.charAt(i))) {
            i++;
        }

        // no value?
        if (i == 0) {
            throw new RuntimeException("Invalid amount without value: '" + text + "'.");
        }

        // no unit?
        if (i == text.length()) {
            throw new RuntimeException("Invalid amount without unit: '" + text + "'.");
        }

        // parse
        var value = parseDouble(text.substring(0, i));
        var unit = Unit.getUnit(text.substring(i));
        return new Amount(value, unit);
    }

    private static boolean isNumeric(char ch) {
        return (ch >= '0' && ch <= '9') || ch == '.' || ch == '-';
    }
}