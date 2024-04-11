import javafx.scene.control.Spinner;
import javafx.fxml.FXML;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class StartMenu {
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
        final var factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(4, 26);
        verticalTilesInput.setValueFactory(factory);
        horizontalTilesInput.setValueFactory(factory);
    }
}
