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
    private final int PUZZLE_WIDTH = 600;

    private int hTiles;
    private int vTiles;
    private double imageAspectRatio;
    private double tileAspectRatio;

    private ImageView selectedPiece = null;

    public void setPuzzleImage(Image image, double tileAspectRatio){
        puzzleImage = image;
        imageAspectRatio = puzzleImage.getHeight() / puzzleImage.getWidth();
        System.out.format("Image AR: %.3f\n", imageAspectRatio);
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

        // Add dummy nodes to click on
        IntStream.range(0, vTiles)
            .forEach(i -> puzzleArea.addRow(i,
                IntStream.range(0, hTiles).mapToObj(i1 -> {
                    var pane = new Pane();
                    // Make SURE it doesn't go over the image's size
                    pane.setMaxSize(getPieceWidth(), getPieceHeight());
                    pane.setPrefSize(getPieceWidth(), getPieceHeight());
                    return pane;
                }).toArray(Pane[]::new)
            ));

        // Set click events
        puzzleArea.setOnMouseClicked(mouseEvent -> {
            mouseEvent.consume();
            var uncastedNode = mouseEvent.getPickResult().getIntersectedNode();

            // For some reason, getIntersectedNode() returns the imageview instead of the parent >:(
            // so I have to add this casting nonsense. Cursed be java and its creators! Minecraft is cool tho
            Pane node;
            if (uncastedNode instanceof Pane){
                node = (Pane)uncastedNode;
            } else {
                node = (Pane)uncastedNode.getParent();
            }

            // Case when a piece is selected prior to clicking
            if (selectedPiece != null) {
                // Case when selection is in grid
                if (
                    !node.getChildren().isEmpty() &&
                    selectedPiece.getParent().getClass().equals(Pane.class)
                ){
                    // Swap the pieces (ImageViews)
                    var temp = node.getChildren().removeFirst();
                    var containerRef = (Pane)selectedPiece.getParent();
                    placePiece(node);
                    containerRef.getChildren().add(temp);
                // Case when selection is in heap
                } else {
                    selectedPiece.setMouseTransparent(true);
                    placePiece(node);
                 }
            // Case when selecting a piece from the grid without prior selection
            } else if (!node.getChildren().isEmpty()) {
                node.toBack();
                selectPiece((ImageView)node.getChildren().getFirst(), true);
            }
        });
    }

    // Handles placing a piece at a location in the grid
    // The variant "A piece is already there" is detected as there being a child in the pane
    private void placePiece(Pane parent){
        // Case when
        if (parent.getChildren().isEmpty()){
            parent.getChildren().add(selectedPiece);

            // Clear the transforms and effect from being on the heap
            selectedPiece.setTranslateX(0);
            selectedPiece.setTranslateY(0);
            selectedPiece.setEffect(null);

            selectedPiece = null;
        // Case when replacing piece with piece from heap
        } else if (selectedPiece != null) {

        }
    }

    private void selectPiece(ImageView piece, boolean fromGrid){
        if (selectedPiece != null){
            selectedPiece.setEffect(null);
        }
        selectedPiece = piece;

        // Set piece to show above the rest
        if (!fromGrid){
            puzzleHeap.getChildren().remove(piece);
            puzzleHeap.getChildren().addLast(piece);
        }

        // Pick a shadow radius equal to the largest of the 2 sizes
        var border = new DropShadow(
            imageAspectRatio > 1 ? (double)getPieceWidth() / 1.90: (double)getPieceHeight() / 1.90,
            Color.YELLOW
        );
        piece.setEffect(border);
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
                selectPiece(_piece, false);
            });
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
        configurePuzzleGrid();
    }
}
