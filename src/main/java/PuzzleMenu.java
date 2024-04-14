import javafx.fxml.FXML;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class PuzzleMenu {
    @FXML
    private StackPane puzzleHeap;
    @FXML
    private AnchorPane root;
    private Image puzzleImage;
    @FXML
    private GridPane puzzleArea;
    private List<ImageView> puzzlePieces;
    private final int PUZZLE_WIDTH = 800;

    private int hTiles;
    private int vTiles;
    private double aspectRatio;

    private ImageView selectedPiece = null;

    public void setPuzzleImage(Image image){
        puzzleImage = image;
        aspectRatio = puzzleImage.getWidth() / puzzleImage.getHeight();
    }

    public void setPuzzleSize(int w, int h){
        hTiles = w;
        vTiles = h;
    }

    @FXML
    private void initialize(){
        // Make sure stack pane is at least pref width
        puzzleHeap.minWidthProperty().bind(puzzleHeap.prefWidthProperty());

        // Bind height of heap to height of grid
        puzzleHeap.prefHeightProperty().bind(puzzleArea.prefHeightProperty());
        puzzleHeap.minHeightProperty().bind(puzzleHeap.prefHeightProperty());
    }

    private void configurePuzzleGrid(){
        puzzleArea.getColumnConstraints().addAll(
            IntStream.range(0, hTiles)
                .mapToObj(i -> new ColumnConstraints(PUZZLE_WIDTH / hTiles))
                .toArray(ColumnConstraints[]::new)
        );
        puzzleArea.getRowConstraints().addAll(
            IntStream.range(0, vTiles)
                .mapToObj(i -> new RowConstraints(PUZZLE_WIDTH * aspectRatio / vTiles))
                .toArray(RowConstraints[]::new)
        );
    }

    private int getPieceWidth(){
        return PUZZLE_WIDTH / hTiles;
    }

    private int getPieceHeight(){
        return (int)(PUZZLE_WIDTH * aspectRatio / vTiles);
    }

    public void setPuzzlePieces(List<ImageView> pieces){
        var rand = new Random();
        pieces.forEach(imageView -> {
            puzzleHeap.getChildren().add(imageView);

            // Randomly distribute pieces across the heap
            imageView.setTranslateX(
                rand.nextInt(0,
            (int)puzzleHeap.getPrefWidth() - getPieceWidth()
                )
            );
            imageView.setTranslateY(
                rand.nextInt(0,
            (int)(PUZZLE_WIDTH * aspectRatio) - getPieceHeight()
                )
            );

            // Register events for selecting
            imageView.setOnMouseClicked(mouseEvent -> {
                mouseEvent.consume();
                var piece = (ImageView)mouseEvent.getSource();
                System.out.format(
                    "Clicked on piece at: x = %.0f, y = %.0f\n", piece.getTranslateX(), piece.getTranslateY()
                );
                selectPiece(piece);
            });
        });
    }

    private void selectPiece(ImageView piece){
        if (selectedPiece != null){
            selectedPiece.setEffect(null);
        }
        selectedPiece = piece;

        // Set piece to show above the rest
        puzzleHeap.getChildren().remove(piece);
        puzzleHeap.getChildren().addLast(piece);
        // Pick a shadow radius equal to the largest of the 2 sizes
        var border = new DropShadow(
                aspectRatio > 1 ? (double)getPieceWidth() / 1.90: (double)getPieceHeight() / 1.90,
                Color.YELLOW
        );
        piece.setEffect(border);
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
        configurePuzzleGrid();
    }
}
