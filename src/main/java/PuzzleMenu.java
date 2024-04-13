import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.awt.event.ActionEvent;
import java.util.List;

public class PuzzleMenu {
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

    public void setPuzzlePieces(List<ImageView> pieces){
        System.out.format("--- Populating with hTiles: %d, vTiles: %d ---\n", hTiles, vTiles);
        for (int i = 0; i < pieces.size(); i++) {
            System.out.format("Placing tile #%d at x: %d, y: %d\n", i, i / vTiles, i % vTiles);
            puzzleArea.add(pieces.get(i), i / vTiles, i % vTiles);
        }
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
