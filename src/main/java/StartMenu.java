import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.fxml.FXML;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class StartMenu {
    @FXML
    private TextField imagePath;
    @FXML
    private Spinner<Integer> horizontalTilesInput;
    @FXML
    private VBox root;
    @FXML
    private ImageView puzzlePreview;
    @FXML
    private Spinner<Integer> verticalTilesInput;

    @FXML
    private void initialize(){
        configureSpinners();
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

    private void showImage() {
        puzzlePreview.setImage(
            new Image("file:///" + imagePath.getText())
        );
    }
}
