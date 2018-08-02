package sq1.layerimages;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.ImageSelection;

public class SaveImageAction implements EventHandler<ActionEvent> {
    private Stage stage;
    private Canvas top;
    private Canvas bottom;
    public final Filename filename;

    public SaveImageAction(Stage stage, Canvas top, Canvas bottom, Filename filename) {
        this.stage = stage;
        this.top = top;
        this.bottom = bottom;
        this.filename = filename;
    }

    @Override
    public void handle(ActionEvent t) {
        FileChooser fileChooser = new FileChooser();
        File downloadFolder = new File(System.getProperty("user.home"), "Downloads");
        if (downloadFolder.exists()) {
            File imageFolder = new File(downloadFolder, "Sq1_SB_Images");
            imageFolder.mkdir();
            fileChooser.setInitialDirectory(imageFolder);
        }

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName(filename.toString());

        // Show save file dialog
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                CanvasImage canvasImage = new CanvasImage(top, bottom);
                BufferedImage combinedImage = canvasImage.getCombinedImage();

                ImageIO.write(combinedImage, "png", file);
                ImageSelection.setClipboard(combinedImage);
            } catch (IOException ex) {
            }
        }
    }
}