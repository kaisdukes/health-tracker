package health.tracker.photos;

import java.awt.*;

public class AnnotationRenderer {
    private static final int FONT_SIZE = 24;
    private final Graphics2D g;
    private final int width;

    public AnnotationRenderer(Graphics2D g, int width) {
        this.g = g;
        this.width = width;
    }

    public void render(String[] annotations) {
        var font = new Font("SansSerif", Font.PLAIN, FONT_SIZE);
        var fm = g.getFontMetrics(font);

        // measure
        var textLineWidth = 0;
        var textLineHeight = 0;
        var textLineOffsetY = 0;
        for (var annotation : annotations) {
            var bounds = fm.getStringBounds(annotation, g);
            textLineWidth = (int) Math.max(textLineWidth, Math.round(bounds.getWidth()));
            textLineHeight = (int) Math.max(textLineHeight, Math.round(bounds.getHeight()));
            textLineOffsetY = (int) Math.max(textLineOffsetY, Math.round(-bounds.getY()));
        }

        var margin = (int) Math.round(0.25 * textLineHeight);

        var boxWidth = textLineWidth + 6 * margin;
        var boxHeight = annotations.length * (textLineHeight + margin) + 3 * margin;

        var ox = width - (boxWidth + margin * 2);
        var oy = margin * 2;

        g.setColor(Color.BLACK);
        g.fillRect(ox, oy, boxWidth, boxHeight);

        g.setFont(font);
        g.setColor(Color.GREEN);

        // render
        var x = ox + margin * 3;
        var y = oy + (int) Math.round(margin * 1.75) + textLineOffsetY;
        for (var annotation : annotations) {
            g.drawString(annotation, x, y);
            y += textLineHeight + margin;
        }
    }
}