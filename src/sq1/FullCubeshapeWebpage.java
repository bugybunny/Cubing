package sq1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import util.HTTP;

/**
 * For one-time-use to extract all shapes and their cubeshape-alg from
 * http://andrewknelson.com/2012/03/23/square-1-full-cubeshape/ into a better
 * format and store locally.
 * 
 * @author <marco.syfrig@gmail.com>
 */
public class FullCubeshapeWebpage {
    public static final String URL = "http://andrewknelson.com/2012/03/23/square-1-full-cubeshape/";
    private final Document doc;
    private final Elements tds;

    public FullCubeshapeWebpage() throws IOException {
        doc = Jsoup.connect(URL).get();
        tds = doc.select("td");
    }

    public Shape getShapeForAlg(Algorithm alg) throws IOException {
        for (Element td : tds) {
            if (td.text().equals(alg.toString()) && td.children().size() > 0) {
                Map<String, String> queryMap = HTTP.getQueryMap(td.child(0).attr("src"));
                return new Shape(queryMap.get("shapes"));
            }
        }
        return null;
    }

    public String getAlgForShape(Shape shape) {
        for (Element td : tds) {
            if (td.children().size() > 0) {
                Map<String, String> queryMap = HTTP.getQueryMap(td.child(0).attr("src"));
                String shapeString = queryMap.get("shapes");
                if (shape.isSameShape(new Shape(shapeString), true)) {
                    return td.text();
                }
            }
        }
        return "";
    }

    public StringBuilder getAllShapesWithAlgs() {
        StringBuilder complete = new StringBuilder(1000);
        for (Element td : tds) {
            if (td.children().size() > 0) {
                Map<String, String> queryMap = HTTP.getQueryMap(td.child(0).attr("src"));
                String shapeString = queryMap.get("shapes");
                if (shapeString != null) {
                    Shape extractedShape = new Shape(shapeString);
                    Algorithm extractedAlgorithm = new Algorithm(td.text());
                    extractedShape.setCubeshapeAlg(extractedAlgorithm);
                    complete.append(extractedShape.getShapeString());
                    complete.append('=');
                    complete.append(extractedShape.getCubeshapeAlg());

                    // add the mirror after ;
                    Shape mirroredShape = extractedShape.mirror();
                    complete.append(';');
                    complete.append(mirroredShape.getShapeString());
                    complete.append('=');
                    complete.append(mirroredShape.getCubeshapeAlg());

                    complete.append('\n');
                }
            }
        }
        return complete;
    }

    public Path writeAllShapesWithAlgsToFile() {
        StringBuilder allShapesWithAlgs = getAllShapesWithAlgs();
        Path outputPath = Paths.get("shapes_from_website_plus_mirrors.txt");
        try {
            Files.write(outputPath, allShapesWithAlgs.toString().getBytes());
        } catch (IOException e) {
            System.err.println("Could not write to " + outputPath);
        }
        return outputPath;
    }

    public Document getDoc() {
        return doc;
    }

    public Elements getTds() {
        return tds;
    }

    public static void main(String[] args) throws IOException {
        FullCubeshapeWebpage site = new FullCubeshapeWebpage();

        Shape shape = new Shape(KnownShape.BARREL.getShape() + KnownShape.LEFT_FIST.getShape());
        java.net.URL shapeURL = ShapeImage.getShapeURL(shape);
        System.out.println(shapeURL);
        ShapeImage.copyShapeImageToClipboard(shapeURL);
    }
}
