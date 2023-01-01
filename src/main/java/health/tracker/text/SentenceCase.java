package health.tracker.text;

public class SentenceCase {

    private SentenceCase() {
    }

    public static String toSentenceCase(String text) {
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }
}