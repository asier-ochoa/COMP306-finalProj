import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Spinner;
import javafx.fxml.FXML;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
        configureImageView();
    }

    private void configureImageView(){
    }

    private void configureSpinners(){
        final var factory1 = new SpinnerValueFactory.IntegerSpinnerValueFactory(4, 26);
        final var factory2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(4, 26);
        factory1.setValue(8);
        factory2.setValue(8);
        verticalTilesInput.setValueFactory(factory1);
        horizontalTilesInput.setValueFactory(factory2);
    }

    @FXML
    private void browseFiles(ActionEvent event){
        event.consume();
        var picker = new FileChooser();
        picker.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        var file = picker.showOpenDialog(((Node)event.getSource()).getScene().getWindow());
        imagePath.setText(file.getAbsolutePath());
        showImage();
    }

    @FXML
    private void setImagePath(ActionEvent event){
        event.consume();
        showImage();
        computePuzzleLines();
        System.out.println();

        var s = (Stage)root.getScene().getWindow();
        s.sizeToScene();
    }

    private void showImage() {
        mainImage = new Image("file:///" + imagePath.getText());
        puzzlePreview.setImage(mainImage);
        imageContainer
    }

    private void computePuzzleLines() {
        canvas.setHeight(mainImage.getHeight());
        canvas.setWidth(mainImage.getWidth());
    }
}
