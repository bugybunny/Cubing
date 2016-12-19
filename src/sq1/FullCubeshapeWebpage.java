package sq1;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
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
                return new Shape(queryMap.get("shapes"), alg);
            }
        }
        return null;
    }

    public String getAlgForShape(Shape shape) {
        for (Element td : tds) {
            if (td.children().size() > 0) {
                Map<String, String> queryMap = HTTP.getQueryMap(td.child(0).attr("src"));
                String shapeString = queryMap.get("shapes");
                Shape shapeToCompare;
                try {
                    shapeToCompare = new Shape(shapeString);
                    if (shape.isSameShape(shapeToCompare, true)) {
                        return td.text();
                    }
                } catch (IOException e) {
                    // do nothing, cannot be the same Shape
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
                    Shape extractedShape;
                    try {
                        extractedShape = new Shape(shapeString);
                        Algorithm extractedAlgorithm = new Algorithm(td.text());
                        extractedShape.setCubeshapeAlg(extractedAlgorithm);
                        complete.append(extractedShape.getShapeString());
                        complete.append('=');
                        complete.append(extractedShape.getCubeshapeAlg());

                        complete.append('\n');
                    } catch (IOException e) {
                        System.err.println("Could not save shape and alg for " + shapeString);
                    }
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

    public static void main(String[] args) throws IOException, InterruptedException {
        FullCubeshapeWebpage site = new FullCubeshapeWebpage();

        Shape originalShape = site.getShapeForAlg(new Algorithm("/-3,2/-4,0/0,1/3,3/"));

        Shape mirror = originalShape.mirror();
        java.net.URL shapeURL = ShapeImage.getShapeURL(mirror);
        ShapeImage.copyShapeImageToClipboard(shapeURL);
        Toolkit.getDefaultToolkit().beep();
        Thread.sleep(1300);
        Toolkit.getDefaultToolkit().beep();
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(mirror.getCubeshapeAlg().toString()), null);
    }
}
