package sq1;

import java.awt.Graphics2D;
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
    public static final int IMAGE_DOWNLOAD_SIZE = 100;
    public static final String SHAPE_IMAGE_URL = "http://andrewknelson.com/imsq.php?stickers=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx&size="
            + IMAGE_DOWNLOAD_SIZE + "&shapes=";

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
     * <a href="http://andrewknelson.com">http://andrewknelson.com</a>.
     * 
     * <br/>
     * This uses the default size {@value #IMAGE_DOWNLOAD_SIZE}px from
     * {@link #IMAGE_DOWNLOAD_SIZE} and does not resize the image.
     * 
     * <br/>
     * If the image already exists in the {@link #SHAPE_IMAGE_DIR} then it is
     * loaded from there, otherwise from
     * <a href="http://andrewknelson.com">http://andrewknelson.com</a>.
     * 
     * @param shapeString
     *            consisting of 'c' and 'e' to describe a Square-1 shape
     */
    public ShapeImage(String shape) throws IOException {
        this(getShapeURL(shape), IMAGE_DOWNLOAD_SIZE);
    }

    /**
     * Constructs a shape image. See {@link Shape} for a description and an
     * example, however an image does not have to follow the rules with a max.
     * of 16 'c' and 'e', it can contain more or less. Less will construct an
     * incomplete shape image and more is defined by the tool from
     * <a href="http://andrewknelson.com">http://andrewknelson.com</a>.
     * 
     * If the image already exists in the {@link #SHAPE_IMAGE_DIR} then it is
     * loaded from there, otherwise from
     * <a href="http://andrewknelson.com">http://andrewknelson.com</a>.
     * 
     * @param shapeString
     *            consisting of 'c' and 'e' to describe a Square-1 shape
     * @param size
     *            size of the image in pixels
     */
    public ShapeImage(String shape, int size) throws IOException {
        this(getShapeURL(shape), size);
    }

    /**
     * Constructs a shape image on an existing URL from
     * <a href="http://andrewknelson.com">http://andrewknelson.com</a>. See
     * {@link Shape} for a description and an example for the {@code shapes=}
     * argument in the URL, however an image does not have to follow the rules
     * with a max. of 16 'c' and 'e', it can contain more or less. Less will
     * construct an incomplete shape image and more is defined by the tool from
     * <a href="http://andrewknelson.com">http://andrewknelson.com</a>.
     * 
     * <br/>
     * This uses the default size {@value #IMAGE_DOWNLOAD_SIZE}px from
     * {@link #IMAGE_DOWNLOAD_SIZE} and does not resize the image.
     * 
     * <br/>
     * If the image already exists in the {@link #SHAPE_IMAGE_DIR} then it is
     * loaded from there, otherwise from
     * <a href="http://andrewknelson.com">http://andrewknelson.com</a>.
     * 
     * @param shapeString
     *            consisting of 'c' and 'e' to describe a Square-1 shape
     */
    public ShapeImage(URL shapeURL) throws IOException {
        this(shapeURL, IMAGE_DOWNLOAD_SIZE);
    }

    /**
     * Constructs a shape image on an existing URL from
     * <a href="http://andrewknelson.com">http://andrewknelson.com</a>. See
     * {@link Shape} for a description and an example for the {@code shapes=}
     * argument in the URL, however an image does not have to follow the rules
     * with a max. of 16 'c' and 'e', it can contain more or less. Less will
     * construct an incomplete shape image and more is defined by the tool from
     * <a href="http://andrewknelson.com">http://andrewknelson.com</a>.
     * 
     * <br/>
     * If the image already exists in the {@link #SHAPE_IMAGE_DIR} then it is
     * loaded from there, otherwise from
     * <a href="http://andrewknelson.com">http://andrewknelson.com</a>.
     * 
     * @param shapeString
     *            consisting of 'c' and 'e' to describe a Square-1 shape
     * @param size
     *            size of the image in pixels
     */
    public ShapeImage(URL shapeURL, int size) throws IOException {
        this.shapeURL = shapeURL;

        Map<String, String> queryMap = HTTP.getQueryMap(shapeURL.toExternalForm());
        shape = queryMap.get("shapes");

        File shapeImageOnFS = new File(SHAPE_IMAGE_DIR, shape + ".png");
        BufferedImage tempImage;
        if (shapeImageOnFS.exists()) {
            tempImage = ImageIO.read(shapeImageOnFS);
        } else {
            tempImage = getImage(shapeURL);
            ImageIO.write(tempImage, "png", shapeImageOnFS);
        }

        image = getScaled(tempImage, size);
    }

    public static BufferedImage getScaled(BufferedImage original, int size) {
        BufferedImage scaled = original;
        if (original.getHeight(null) != size) {
            Image img = original.getScaledInstance(2 * size, size, Image.SCALE_SMOOTH);
            scaled = new BufferedImage(2 * size, size, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = scaled.createGraphics();
            g.drawImage(img, 0, 0, null);
            if (g != null) {
                g.dispose();
            }
        }
        return scaled;
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

    public static URL getShapeURLForMirror(URL existing) throws IOException {
        Map<String, String> existingParameters = HTTP.getQueryMap(existing.getQuery());
        return getShapeURLForMirror(existingParameters.get("shapes"));
    }

    public static URL getShapeURLForMirror(String shape) throws IOException {
        return getShapeURLForMirror(new Shape(shape));
    }

    public static URL getShapeURLForMirror(Shape shape) throws IOException {
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