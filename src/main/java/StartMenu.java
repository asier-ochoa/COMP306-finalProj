import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Random;
import java.util.stream.IntStream;

public class StartMenu {
    @FXML
    private StackPane imageContainer;
    @FXML
    private Canvas canvas;
    @FXML
    private TextField imagePath;
    @FXML
    private Spinner<Integer> horizontalTilesInput;
    @FXML
    private AnchorPane root;
    @FXML
    private ImageView puzzlePreview;
    @FXML
    private Spinner<Integer> verticalTilesInput;
    private Image mainImage;  // Represents the in memory image, try to have only 1

    @FXML
    private void initialize(){
        configureSpinners();
        configureCanvas();
    }

    private void configureCanvas(){
        imageContainer.widthProperty().addListener(observable -> updateCanvasSize());
        canvas.heightProperty().bind(imageContainer.heightProperty());
    }

    private void configureSpinners(){
        final var factory1 = new SpinnerValueFactory.IntegerSpinnerValueFactory(4, 26);
        final var factory2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(4, 26);
        factory1.setValue(8);
        factory2.setValue(8);
        verticalTilesInput.setValueFactory(factory1);
        horizontalTilesInput.setValueFactory(factory2);

        factory1.valueProperty().addListener(observable -> computePuzzleLines());
        factory2.valueProperty().addListener(observable -> computePuzzleLines());
    }

    // Must compute ratio based on fitHeight to size canvas to image
    private void updateCanvasSize() {
        if (mainImage != null){
            canvas.setWidth(mainImage.getWidth() / mainImage.getHeight() * puzzlePreview.getFitHeight());
        }
    }

    @FXML
    private void browseFiles(ActionEvent event){
        event.consume();
        var picker = new FileChooser();
        picker.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        var file = picker.showOpenDialog(((Node)event.getSource()).getScene().getWindow());
        if (file == null){
            return;
        }
        imagePath.setText(file.getAbsolutePath());
        showImage();
        computePuzzleLines();
    }

    @FXML
    private void setImagePath(ActionEvent event){
        event.consume();
        showImage();
        computePuzzleLines();
    }

    private void showImage() {
        mainImage = new Image("file:///" + imagePath.getText());
        puzzlePreview.setImage(mainImage);

        // For some ungodly reason, this must be placed right after the image so that it doesn't get cut off
        var s = (Stage)root.getScene().getWindow();
        s.sizeToScene();
    }

    private void computePuzzleLines() {
        updateCanvasSize();

        var hLineAdvance = canvas.getWidth() / horizontalTilesInput.getValue();
        var vLineAdvance = canvas.getHeight() / verticalTilesInput.getValue();
        var gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setStroke(Color.BLUE);
        // Draw horizontal lines
        for (int i = 1; i <= verticalTilesInput.getValue(); ++i) {
            gc.strokeLine(0, i * vLineAdvance, canvas.getWidth(), i * vLineAdvance);
        }
        // Draw vertical lines
        for (int i = 0; i < horizontalTilesInput.getValue(); ++i) {
            gc.strokeLine(i * hLineAdvance, 0, i * hLineAdvance, canvas.getHeight());
        }
    }

    @FXML
    private void loadPuzzle(ActionEvent event) throws Exception {
        event.consume();
        if (mainImage != null){
            // Load class
            var loader = new FXMLLoader(new File("src/main/resources/PuzzleMenu.fxml").toURI().toURL());
            var puzzleMenu = (Parent)loader.load();
            var controller = (PuzzleMenu)loader.getController();

            // Set image for the puzzle and compute tile size
            var hTiles = horizontalTilesInput.getValue();
            var vTiles = verticalTilesInput.getValue();
            var tiles = hTiles * vTiles;
            var hTileAdvance = mainImage.getWidth() / hTiles;
            var vTileAdvance = mainImage.getHeight() / vTiles;
            controller.setPuzzleImage(mainImage, hTileAdvance / vTileAdvance);

            // Configure image views (Pieces);
            controller.setPuzzleSize(hTiles, vTiles);
            controller.setPuzzlePieces(
                IntStream.range(0, tiles).mapToObj(i -> {
                    var piece = new PuzzlePiece(mainImage, i % vTiles, i / vTiles);
                    piece.setPreserveRatio(true);
                    piece.setViewport(
                        new Rectangle2D(
                            hTileAdvance * (i / vTiles), vTileAdvance * (i % vTiles),
                            hTileAdvance, vTileAdvance
                        )
                    );
                    return piece;
                }).toList()
            );

            // Adjust scene size
            var s = root.getScene();
            s.setRoot(puzzleMenu);

            controller.postInit();
            // Set scene size after fully initialized
            s.getWindow().sizeToScene();
        } else {
            var dialog = new Dialog<>();
            dialog.setTitle("Error!");
            dialog.setHeaderText("Invalid image file!");
            dialog.setContentText("Please choose an image");
            dialog.getDialogPane().getButtonTypes().add(new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE));
            dialog.showAndWait();
        }
    }
}
