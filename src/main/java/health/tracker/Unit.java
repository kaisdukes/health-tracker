package health.tracker;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public enum Unit {
    Percentage("%"),
    Gram("g"),
    Kilogram("kg"),
    Milliliter("m");

    private static final Map<String, Unit> UNITS_BY_SYMBOL
            = Arrays.stream(values()).collect(toMap(Unit::getSymbol, x -> x));

    private final String symbol;

    Unit(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public static Unit getUnit(String symbol) {
        var unit = UNITS_BY_SYMBOL.get(symbol);
        if (unit == null) {
            throw new RuntimeException("Unit symbol '" + symbol + "' not recognized.");
        }
        return unit;
    }
}