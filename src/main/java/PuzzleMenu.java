import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.util.List;
import java.util.Random;

public class PuzzleMenu {
    @FXML
    private StackPane puzzleHeap;
    @FXML
    private AnchorPane root;
    private Image puzzleImage;
    @FXML
    private GridPane puzzleArea;
    private List<ImageView> puzzlePieces;

    private int hTiles;
    private int vTiles;

    public void setPuzzleImage(Image image){
        puzzleImage = image;
    }

    public void setPuzzleSize(int w, int h){
        hTiles = w;
        vTiles = h;
    }

    @FXML
    private void initialize(){
        // Make sure stack pane is at least pref width
        puzzleHeap.minWidthProperty().bind(puzzleHeap.prefWidthProperty());
    }

    public void setPuzzlePieces(List<ImageView> pieces, int imageHeight){
        var rand = new Random();
        pieces.forEach(imageView -> {
            puzzleHeap.getChildren().add(imageView);
            imageView.setTranslateX(rand.nextInt(0, (int)puzzleHeap.getPrefWidth()));
            imageView.setTranslateY(rand.nextInt(0, imageHeight));
        });
    }

    public void postInit(){
        root.getScene().setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.EQUALS) {
                puzzleArea.setHgap(puzzleArea.getHgap() + 1);
                puzzleArea.setVgap(puzzleArea.getVgap() + 1);
            }
            if (keyEvent.getCode() == KeyCode.MINUS) {
                puzzleArea.setHgap(puzzleArea.getHgap() - 1);
                puzzleArea.setVgap(puzzleArea.getVgap() - 1);
            }
        });
    }
}
