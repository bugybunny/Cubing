package sq1;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.imageio.ImageIO;

import util.HTTP;
import util.ImageSelection;

/**
 * @author <marco.syfrig@gmail.com>
 */
public class ShapeImage {

    public static final File SHAPE_IMAGE_DIR = new File("images");
    public static final int IMAGE_SIZE = 500;
    public static final String SHAPE_IMAGE_URL = "http://andrewknelson.com/imsq.php?size=" + IMAGE_SIZE
            + "&stickers=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx&shapes=";

    static {
        SHAPE_IMAGE_DIR.mkdirs();
    }

    public final URL shapeURL;
    public final BufferedImage image;
    public final String shape;

    /**
     * Constructs a shape image. See {@link Shape} for a description and an
     * example, however an image does not have to follow the rules with a max.
     * of 16 'c' and 'e', it can contain more or less. Less will construct an
     * incomplete shape image and more is defined by the tool from
     * {@link http://andrewknelson.com}.
     * 
     * @param shapeString
     *            consisting of 'c' and 'e' to describe a Square-1 shape
     * @throws IOException
     */
    public ShapeImage(String shape) throws IOException {
        this(getShapeURL(shape));
    }

    public ShapeImage(URL shapeURL) throws IOException {
        this.shapeURL = shapeURL;

        Map<String, String> queryMap = HTTP.getQueryMap(shapeURL.toExternalForm());
        shape = queryMap.get("shapes");

        File shapeImageOnFS = new File(SHAPE_IMAGE_DIR, shape + ".png");
        if (shapeImageOnFS.exists()) {
            image = ImageIO.read(shapeImageOnFS);
        } else {
            image = getImage(shapeURL);
        }
    }

    /**
     * @param shape
     *            must not be 16 characters long, can be shorter (for incomplete
     *            images) or longer for undefined results
     * @return URL for this shape image
     * @throws MalformedURLException
     */
    public static URL getShapeURL(String shape) throws MalformedURLException {
        return new URL(SHAPE_IMAGE_URL + shape);
    }

    public static URL getShapeURL(Shape shape) throws MalformedURLException {
        return new URL(SHAPE_IMAGE_URL + shape.getShapeString());
    }

    public static URL getShapeURLForMirror(URL existing) throws MalformedURLException {
        Map<String, String> existingParameters = HTTP.getQueryMap(existing.getQuery());
        return getShapeURLForMirror(existingParameters.get("shapes"));
    }

    public static URL getShapeURLForMirror(String shape) throws MalformedURLException {
        return getShapeURLForMirror(new Shape(shape));
    }

    public static URL getShapeURLForMirror(Shape shape) throws MalformedURLException {
        String mirroredShape = shape.mirror().getShapeString();
        return new URL(SHAPE_IMAGE_URL + mirroredShape);
    }

    public static BufferedImage getImage(URL shapeImageURL) throws IOException {
        return ImageIO.read(shapeImageURL);
    }

    public static void copyShapeImageToClipboard(URL shapeImageURL) throws IOException {
        setClipboard(getImage(shapeImageURL));
    }

    public static void setClipboard(Image image) {
        ImageSelection imgSelection = new ImageSelection(image);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSelection, null);
    }
}