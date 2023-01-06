package health.tracker;

public enum Unit {
    Percentage("%"),
    Gram("g"),
    Kilogram("kg"),
    Milliliter("m");

    private final String symbol;

    Unit(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}