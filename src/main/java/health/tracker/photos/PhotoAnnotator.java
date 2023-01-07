package health.tracker.photos;

import health.tracker.metrics.HealthService;
import health.tracker.metrics.Metric;
import lombok.SneakyThrows;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static health.tracker.io.Io.getFiles;
import static health.tracker.photos.PhotoFilenameParser.parsePhotoFilename;
import static health.tracker.text.SentenceCase.toSentenceCase;

public class PhotoAnnotator {
    private final Path dataPath;
    private final HealthService healthService;
    private static final int SCALED_HEIGHT = 1000;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final String PHOTOS_FOLDER_NAME = "photos";
    private static final String ORIGINALS_FOLDER_NAME = "originals";
    private static final String ANNOTATED_FOLDER_NAME = "annotated";
    private static final String IMAGE_FORMAT = "jpeg";

    public PhotoAnnotator(Path dataPath, HealthService healthService) {
        this.dataPath = dataPath;
        this.healthService = healthService;
    }

    @SneakyThrows
    public void annotate() {
        getFiles(
                dataPath.resolve(PHOTOS_FOLDER_NAME).resolve(ORIGINALS_FOLDER_NAME),
                IMAGE_FORMAT)
                .forEach(this::annotatePhoto);
    }

    @SneakyThrows
    private void annotatePhoto(Path originalPath) {

        // parse filename
        var photoInfo = parsePhotoFilename(originalPath);

        // read original image
        var image = ImageIO.read(originalPath.toFile());

        //  create a graphics context
        var height = SCALED_HEIGHT;
        var width = (int) Math.round(image.getWidth() * ((double) height / image.getHeight()));
        var bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var g = bufferedImage.createGraphics();
        g.addRenderingHints(Map.of(
                RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY,
                RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY,
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

        // render
        g.drawImage(resizeImage(image, width, height), 0, 0, null);
        var annotationRenderer = new AnnotationRenderer(g, width);
        annotationRenderer.render(getAnnotations(photoInfo));

        // done
        g.dispose();

        // write
        var originalFilename = originalPath.getFileName().toString();
        var outputPath = dataPath
                .resolve(PHOTOS_FOLDER_NAME)
                .resolve(Paths.get(ANNOTATED_FOLDER_NAME, originalFilename));
        writeHighQualityJpeg(bufferedImage, outputPath);
    }

    @SneakyThrows
    private static void writeHighQualityJpeg(BufferedImage image, Path path) {

        // specify quality
        var params = new JPEGImageWriteParam(null);
        params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        params.setCompressionQuality(1);

        // write image
        var writer = ImageIO.getImageWritersByFormatName(IMAGE_FORMAT).next();
        writer.setOutput(new FileImageOutputStream(path.toFile()));
        writer.write(null, new IIOImage(image, null, null), params);
    }

    private static BufferedImage resizeImage(BufferedImage image, int width, int height) {
        var transform = AffineTransform.getScaleInstance(
                (double) width / image.getWidth(),
                (double) height / image.getHeight());
        var transformOp = new AffineTransformOp(
                transform,
                new RenderingHints(Map.of(
                        RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY,
                        RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY,
                        RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON,
                        RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)));
        var scaledImage = new BufferedImage(width, height, image.getType());
        return transformOp.filter(image, scaledImage);
    }

    private String[] getAnnotations(PhotoInfo photoInfo) {
        var date = photoInfo.getDate();
        return new String[]{
                DATE_FORMATTER.format(date),
                formatMetric(Metric.WeightKg, date),
                formatMetric(Metric.BodyFatPercentage, date),
                formatKeywords(photoInfo.getKeywords())};
    }

    private String formatMetric(Metric metric, LocalDate date) {
        var value = (int) Math.round(healthService.getAveragedMetric(date, metric));
        return toSentenceCase(metric.getDescription() + ": " + value + metric.getUnit().getSymbol());
    }

    private static String formatKeywords(String[] keywords) {
        return toSentenceCase(String.join(", ", keywords));
    }
}