package sq1.layerimages;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import sq1.layerimages.Piece.Position;

public class SecondBlockImageCreator extends Application {
    public static final int SIDE_LENGTH = 150;
    private static final int CANVAS_LENGTH = SIDE_LENGTH + 20;
    private static final int MIDDLE = SIDE_LENGTH / 2;
    private static final int EDGE_LENGTH = (int) (SIDE_LENGTH * 0.2);
    private static final int CORNER_LENGTH = (int) (SIDE_LENGTH * 0.4);

    private Filename filename = new Filename("");

    private static final KeyCombination jumpToTextFieldShortcut = new KeyCodeCombination(KeyCode.L, KeyCombination.SHORTCUT_DOWN);

    private Canvas top;
    private Canvas bottom;
    private ColorPairTextField text;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Square-1 Image Creator");
        Group root = new Group();
        top = new Canvas(CANVAS_LENGTH, CANVAS_LENGTH);
        bottom = new Canvas(CANVAS_LENGTH, CANVAS_LENGTH);

        Button buttonSave = new Button("_Save");
        buttonSave.setOnAction(new SaveImageAction(primaryStage, top, bottom, filename));
        buttonSave.setMnemonicParsing(true);

        text = new ColorPairTextField("UBR={red, green}; UR={green}; UFR={green, orange}");
        text.setPromptText("DFL={red, blue}; DL={blue}; DBL={blue, orange}");
        text.setPrefWidth(400);
        Button buttonRepaint = new Button("_Repaint");
        buttonRepaint.setDefaultButton(true);
        buttonRepaint.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                top.getGraphicsContext2D().clearRect(0, 0, top.getWidth(), top.getHeight());
                bottom.getGraphicsContext2D().clearRect(0, 0, bottom.getWidth(), bottom.getHeight());
                drawLayers();
            }
        });

        HBox topBox = new HBox(5);
        topBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(text, Priority.ALWAYS);
        topBox.getChildren().addAll(buttonSave, text, buttonRepaint);
        drawLayers();

        top.setRotate(30);
        top.getTransforms().add(new Translate(30, 3));
        // invert y for bottom to get this stupid x2 look -.-
        bottom.getTransforms().add(new Scale(1, -1));
        bottom.getTransforms().add(new Translate(30, -bottom.getHeight()));
        bottom.setRotate(30);
        HBox layers = new HBox(20);
        layers.getChildren().addAll(top, bottom);

        VBox vBox = new VBox(20);
        vBox.getChildren().addAll(topBox, layers);
        root.getChildren().add(vBox);

        primaryStage.setScene(new Scene(root));
        addShortcuts(primaryStage);
        primaryStage.show();
        text.requestFocus();
    }

    private void addShortcuts(Stage primaryStage) {
        primaryStage.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (jumpToTextFieldShortcut.match(event)) {
                text.requestFocus();
            }
            if (event.getCode().isDigitKey()) {
                if (event.isShortcutDown()) {
                    text.selectPositionString(Integer.parseInt(event.getCode().getName()));
                }
            }
        });

    }

    private void drawLayers() {
        filename.reset();
        int startX = 10;
        int startY = 10;

        String colorText = text.getText() + ";DFL={red, blue}; DL={blue}; DBL={blue, orange}";

        GraphicsContext topGC = top.getGraphicsContext2D();
        drawLayer(topGC, startX, startY, colorText, Piece.Layer.U);
        drawSlice(topGC, startX + CORNER_LENGTH, startY, startX + CORNER_LENGTH + EDGE_LENGTH, startY + SIDE_LENGTH);

        startX = 10;
        startY = 10;
        GraphicsContext bottomGC = bottom.getGraphicsContext2D();
        drawLayer(bottomGC, startX, startY, colorText, Piece.Layer.D);
        drawSlice(bottomGC, startX + CORNER_LENGTH + EDGE_LENGTH, startY, startX + CORNER_LENGTH, startY + SIDE_LENGTH);
    }

    private void parseAndAssociateColorText(List<Piece> pieces, String input, Piece.Layer layer) {
        String[] positionColorEntries = input.split(";");
        for (String entry : positionColorEntries) {
            try {
                PieceColorPair pieceColorPair = new PieceColorPair(entry);
                pieceColorPair.associateColors(pieces);
                if (pieceColorPair != null && pieceColorPair.getPiece() != null) {
                    filename.add(pieceColorPair.getPiece().toString());
                }
            } catch (PieceToColorParseException e) {
                Toast.makeText(e.getMessage());
            }
        }
    }

    private void drawLayer(GraphicsContext gc, int startX, int startY, String inputColorText, Piece.Layer layer) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        List<Piece> pieces = createPiecesForLayer(layer, startX, startY);
        parseAndAssociateColorText(pieces, inputColorText, layer);

        for (Piece piece : pieces) {
            drawPiece(gc, piece);
        }

        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);

        gc.strokeRect(startX, startY, SIDE_LENGTH, SIDE_LENGTH);

        // slices: top to bottom
        gc.strokeLine(startX + CORNER_LENGTH, startY, startX + SIDE_LENGTH - CORNER_LENGTH, startY + SIDE_LENGTH);
        gc.strokeLine(startX + SIDE_LENGTH - CORNER_LENGTH, startY, startX + CORNER_LENGTH, startY + SIDE_LENGTH);
        // slices: left to right
        gc.strokeLine(startX, startY + CORNER_LENGTH, startX + SIDE_LENGTH, startY + SIDE_LENGTH - CORNER_LENGTH);
        gc.strokeLine(startX, startY + SIDE_LENGTH - CORNER_LENGTH, startX + SIDE_LENGTH, startY + CORNER_LENGTH);
    }

    private static void drawPiece(GraphicsContext gc, Piece piece) {
        gc.setFill(piece.getTopColor());
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.0);
        gc.fillPolygon(piece.xs, piece.ys, piece.xs.length);
        gc.setLineWidth(8.0);
        gc.setLineCap(StrokeLineCap.BUTT);
        gc.setLineJoin(StrokeLineJoin.ROUND);
        if (piece.getSideColors()[0] != ColorScheme.GRAY) {
            gc.setStroke(piece.getSideColors()[0]);
            gc.strokeLine(piece.xs[0], piece.ys[0], piece.xs[1], piece.ys[1]);
        }
        if (!piece.isEdge() && piece.getSideColors()[1] != ColorScheme.GRAY) {
            gc.setStroke(piece.getSideColors()[1]);
            gc.strokeLine(piece.xs[1], piece.ys[1], piece.xs[2], piece.ys[2]);
        }
    }

    private static void drawSlice(GraphicsContext gc, int x1, int y1, int x2, int y2) {
        gc.setLineWidth(3);
        gc.setStroke(Color.BLACK);

        double gradientX = (x2 - x1) / SIDE_LENGTH;
        double gradientY = (y2 - y1) / SIDE_LENGTH;
        int length = 10;
        gc.strokeLine(x1 - length * gradientX, y1 - length * gradientY, x2 + length * gradientX, y2 + length * gradientY);
    }

    private static List<Piece> createPiecesForLayer(Piece.Layer layer, int startX, int startY) {
        List<Piece> pieces = new ArrayList<>(8);

        Piece bl = new Piece(new double[] { startX, startX, startX + CORNER_LENGTH, startX + MIDDLE }, new double[] { startY + CORNER_LENGTH, startY, startY, startY + MIDDLE },
                layer, Position.BL);
        pieces.add(bl);

        Piece b = new Piece(new double[] { startX + CORNER_LENGTH, startX + CORNER_LENGTH + EDGE_LENGTH, startX + MIDDLE }, new double[] { startY, startY, startY + MIDDLE }, layer,
                Position.B);
        pieces.add(b);

        Piece br = new Piece(new double[] { startX + SIDE_LENGTH - CORNER_LENGTH, startX + SIDE_LENGTH, startX + SIDE_LENGTH, startX + MIDDLE },
                new double[] { startY, startY, startY + CORNER_LENGTH, startY + MIDDLE }, layer, Position.BR);
        pieces.add(br);

        Piece r = new Piece(new double[] { startX + SIDE_LENGTH, startX + SIDE_LENGTH, startX + MIDDLE },
                new double[] { startY + CORNER_LENGTH, startY + CORNER_LENGTH + EDGE_LENGTH, startY + MIDDLE }, layer, Position.R);
        pieces.add(r);

        Piece fr = new Piece(new double[] { startX + SIDE_LENGTH, startX + SIDE_LENGTH, startX + SIDE_LENGTH - CORNER_LENGTH, startX + MIDDLE },
                new double[] { startY + SIDE_LENGTH - CORNER_LENGTH, startY + SIDE_LENGTH, startY + SIDE_LENGTH, startY + MIDDLE }, layer, Position.FR);
        pieces.add(fr);

        Piece f = new Piece(new double[] { startX + SIDE_LENGTH - CORNER_LENGTH, startX + CORNER_LENGTH, startX + MIDDLE },
                new double[] { startY + SIDE_LENGTH, startY + SIDE_LENGTH, startY + MIDDLE }, layer, Position.F);
        pieces.add(f);

        Piece fl = new Piece(new double[] { startX + CORNER_LENGTH, startX, startX, startX + MIDDLE },
                new double[] { startY + SIDE_LENGTH, startY + SIDE_LENGTH, startY + SIDE_LENGTH - CORNER_LENGTH, startY + MIDDLE }, layer, Position.FL);
        pieces.add(fl);

        Piece l = new Piece(new double[] { startX, startX, startX + MIDDLE }, new double[] { startY + SIDE_LENGTH - CORNER_LENGTH, startY + CORNER_LENGTH, startY + MIDDLE }, layer,
                Position.L);
        pieces.add(l);

        return pieces;
    }
}