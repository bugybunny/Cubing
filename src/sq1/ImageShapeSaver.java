package sq1;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.imageio.ImageIO;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import util.HTTP;

/**
 * Saves all shape images found on <a href=
 * "http://andrewknelson.com/2012/03/23/square-1-full-cubeshape">http://andrewknelson.com/2012/03/23/square-1-full-cubeshape/</a>
 * and all mirrors of these images to a directory {@code images} in the working
 * directory.
 * 
 * @author <marco.syfrig@gmail.com>
 */
public class ImageShapeSaver {

    public static File saveImagesFromWebsite(File outputDir) throws IOException {
        FullCubeshapeWebpage site = new FullCubeshapeWebpage();
        outputDir.mkdirs();

        Elements tds = site.getTds();
        for (Element td : tds) {
            if (td.children().size() > 0) {
                String shapeImageURL = td.child(0).attr("src");
                // extract shape string and do not use shapeImageURL directly,
                // otherwise we are using the original image as displayed on the
                // site (100x100px) which we do not want
                Map<String, String> existingParameters = HTTP.getQueryMap(shapeImageURL);
                ShapeImage extractedshapeImage = new ShapeImage(existingParameters.get("shapes"));
                saveImage(outputDir, extractedshapeImage);

                // also save the mirror of the above shape
                URL shapeURLForMirror = ShapeImage.getShapeURLForMirror(extractedshapeImage.shape);
                ShapeImage mirroredShapeImage = new ShapeImage(shapeURLForMirror);
                saveImage(outputDir, mirroredShapeImage);
            }
        }
        return outputDir;
    }

    public static File saveImage(File outputDir, ShapeImage shapeImage) throws IOException {
        File outputFile = new File(outputDir, shapeImage.shape + ".png");
        System.out.println(outputFile.getAbsolutePath() + " created");
        outputFile.createNewFile();
        ImageIO.write(shapeImage.image, "png", outputFile);

        return outputFile;
    }

    public static void main(String[] args) throws IOException {
        saveImagesFromWebsite(ShapeImage.SHAPE_IMAGE_DIR);
    }
}
