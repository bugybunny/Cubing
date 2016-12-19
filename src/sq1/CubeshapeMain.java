package sq1;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class CubeshapeMain extends Application {
    private Map<Shape, Algorithm> shapeToAlg = new HashMap<>(125);
    private Map<Algorithm, Shape> algToShape = new HashMap<>(125);

    @Override
    public void start(Stage primaryStage) throws Exception {
        populateShapeList();

        primaryStage.setTitle("Hello World!");
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                copyMirrorToClipboard(new Algorithm("/-2,-1/-3,0/"));
            }
        });

        StackPane root = new StackPane();
        root.getChildren().add(btn);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }

    private void populateShapeList() {
        try (Scanner scanner = new Scanner(Shape.SHAPE_FILE).useDelimiter("\n")) {
            while (scanner.hasNext()) {
                String line = scanner.next();
                String[] splittedLine = line.split("=");
                if (splittedLine.length == 2) {
                    try {
                        Shape shape = new Shape(splittedLine[0], new Algorithm(splittedLine[1]));
                        Shape mirror = shape.mirror();
                        shapeToAlg.put(shape, shape.getCubeshapeAlg());
                        shapeToAlg.put(mirror, mirror.getCubeshapeAlg());
                        algToShape.put(shape.getCubeshapeAlg(), shape);
                        algToShape.put(mirror.getCubeshapeAlg(), mirror);

                    } catch (IOException e) {
                        System.err.println("Could not create shape image for " + line + ".");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read file " + Shape.SHAPE_FILE.getFileName());
            System.exit(1);
        }
    }

    public void copyMirrorToClipboard(Algorithm alg) {
        Shape shape = algToShape.get(alg);
        if (shape != null) {
            try {
                Shape mirror = shape.mirror();
                ShapeImage.setClipboard(mirror.image.image);
            } catch (IOException e) {
                System.err.println("Could not mirror shape " + shape + ".");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
