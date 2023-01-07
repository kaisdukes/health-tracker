package health.tracker.text;

public class DoubleFormat {

    public static String formatDouble(double value) {
        return (int) value == value
                ? String.format("%d", (int) value)
                : String.format("%s", value);
    }
}