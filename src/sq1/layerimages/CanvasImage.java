package sq1.layerimages;

import java.awt.image.BufferedImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;

/**
 * Represents a drawable area with two canvases that can be easily converted to a {@link BufferedImage}.
 */
public class CanvasImage {
    private final Canvas topCanvas;
    private final Canvas bottomCanvas;

    private BufferedImage image;
    private BufferedImage topImage;
    private BufferedImage bottomImage;

    public static double canvasResizeFactor = 1.32;

    public CanvasImage(Canvas top, Canvas bottom) {
        this.topCanvas = top;
        this.bottomCanvas = bottom;
    }

    public BufferedImage getTopImage() {
        if (topImage == null) {
            topImage = getImageFrom(topCanvas);
        }

        return topImage;
    }

    public BufferedImage getBottomImage() {
        if (bottomImage == null) {
            bottomImage = getImageFrom(bottomCanvas);
        }

        return bottomImage;
    }

    /**
     * Returns a combined image of the two canvases with the top canvas on the left and the bottom one on the right.
     */
    public BufferedImage getCombinedVerticalImage() {
        if (image == null) {
            image = new BufferedImage((int) (getTopImage().getWidth() + getBottomImage().getWidth()), (int) (getTopImage().getHeight()), BufferedImage.TYPE_INT_ARGB);
            image.getGraphics().drawImage(getTopImage(), 0, 0, null);
            image.getGraphics().drawImage(getBottomImage(), (int) getBottomImage().getWidth(), 0, null);
        }

        return image;
    }

    private static BufferedImage getImageFrom(Canvas canvas) {
        WritableImage canvasSnapshot = new WritableImage((int) (canvas.getWidth() * canvasResizeFactor), (int) (canvas.getHeight() * canvasResizeFactor));
        canvas.snapshot(null, canvasSnapshot);
        BufferedImage image = SwingFXUtils.fromFXImage(canvasSnapshot, null);

        return image;
    }

}
