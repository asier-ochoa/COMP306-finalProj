import javafx.fxml.FXML;
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
    private double imageAspectRatio;
    private double tileAspectRatio;

    private ImageView selectedPiece = null;

    public void setPuzzleImage(Image image, double tileAspectRatio){
        puzzleImage = image;
        imageAspectRatio = puzzleImage.getWidth() / puzzleImage.getHeight();
        this.tileAspectRatio = tileAspectRatio;
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
        // Configure cell sizes
        puzzleArea.getColumnConstraints().addAll(
            IntStream.range(0, hTiles)
                .mapToObj(i -> new ColumnConstraints(getPieceWidth()))
                .toArray(ColumnConstraints[]::new)
        );
        puzzleArea.getRowConstraints().addAll(
            IntStream.range(0, vTiles)
                .mapToObj(i -> new RowConstraints(getPieceHeight()))
                .toArray(RowConstraints[]::new)
        );

        // Set click events
        puzzleArea.setOnMouseClicked(mouseEvent -> {
//            var x = mouseEvent.getSceneX() / getPieceWidth();
//            var y = mouseEvent.getSceneY() / getPieceHeight();
//            System.out.format("Clicked on cell x: %.0f, y: %.0f\n", x, y);
        });
    }

    private int getPieceWidth(){
        return PUZZLE_WIDTH / hTiles;
    }

    private int getPieceHeight(){
        return (int)(PUZZLE_WIDTH * imageAspectRatio / vTiles);
    }

    public void setPuzzlePieces(List<ImageView> pieces){
        var rand = new Random();
        pieces.forEach(piece -> {
            piece.setFitHeight(getPieceHeight());
            puzzleHeap.getChildren().add(piece);


            // Randomly distribute pieces across the heap
            piece.setTranslateX(
                rand.nextInt(0,
            (int)puzzleHeap.getPrefWidth() - getPieceWidth()
                )
            );
            piece.setTranslateY(
                rand.nextInt(0,
            (int)(PUZZLE_WIDTH * imageAspectRatio) - getPieceHeight()
                )
            );

            // Register events for selecting
            piece.setOnMouseClicked(mouseEvent -> {
                mouseEvent.consume();
                var _piece = (ImageView)mouseEvent.getSource();
                System.out.format(
                    "Clicked on piece at: x = %.0f, y = %.0f\n", _piece.getTranslateX(), _piece.getTranslateY()
                );
                selectPiece(_piece);
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
            imageAspectRatio > 1 ? (double)getPieceWidth() / 1.90: (double)getPieceHeight() / 1.90,
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
