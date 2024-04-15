import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class PuzzleMenu {
    @FXML
    private Text timerText;
    @FXML
    private StackPane puzzleHeap;
    @FXML
    private AnchorPane root;
    private Image puzzleImage;
    @FXML
    private GridPane puzzleArea;
    private final int PUZZLE_WIDTH = 600;

    private int hTiles;
    private int vTiles;
    private double imageAspectRatio;

    private PuzzlePiece selectedPiece = null;

    private AnimationTimer timer;

    public void setPuzzleImage(Image image){
        puzzleImage = image;
        imageAspectRatio = puzzleImage.getHeight() / puzzleImage.getWidth();
        System.out.format("Image AR: %.3f\n", imageAspectRatio);
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

        configureTimer();
    }

    private void configureTimer(){
        timer = new AnimationTimer(){
            private long startTime;
            private long endTime;

            @Override
            public void start() {
                startTime = System.currentTimeMillis();
                super.start();
            }

            @Override
            public void stop() {
                super.stop();
            }

            @Override
            public void handle(long timestamp) {
                var elapsed = System.currentTimeMillis() - startTime;
                timerText.setText(
                    String.format(
                        "%02d:%02d:%02d:%03d",
                        elapsed / 1000 / 60 / 60,
                        elapsed / 1000 / 60 % 60,
                        elapsed / 1000 % 60,
                        elapsed % 1000
                    )
                );
            }
        };
        timer.start();
    }

    @FXML
    private void backgroundClicked(MouseEvent event){
        event.consume();
        if (selectedPiece != null){
            selectPiece(null, false);
        }
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

            // For some reason, getIntersectedNode() returns the puzzlepiece instead of the parent >:(
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
                    // Swap the pieces (PuzzlePiece)
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
                selectPiece((PuzzlePiece) node.getChildren().getFirst(), true);
            }

            if(checkIfSolved()){
                timer.stop();
                System.out.println("Solved!!!");
                var dialog = new Dialog<>();
                dialog.setTitle("Puzzle solved!");
                dialog.setHeaderText(String.format("You managed to solve the puzzle in %s!", timerText.getText()));
                dialog.setContentText("Thank you for playing");
                dialog.getDialogPane().getButtonTypes().add(new ButtonType("Exit", ButtonBar.ButtonData.OK_DONE));
                dialog.showAndWait();
                Platform.exit();
                System.exit(0);
            }
        });
    }

    private boolean checkIfSolved(){
        for (var node: puzzleArea.getChildren()){
            if (!(node instanceof Group)){
                if (!((Pane)node).getChildren().isEmpty()){
                    var piece = (PuzzlePiece)((Pane)node).getChildren().getFirst();
                    if (
                        GridPane.getRowIndex(piece.getParent()) != piece.x ||
                        GridPane.getColumnIndex(piece.getParent()) != piece.y
                    ){
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
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
        }
    }

    private void selectPiece(PuzzlePiece piece, boolean fromGrid){
        if (selectedPiece != null){
            selectedPiece.setEffect(null);
        }
        selectedPiece = piece;

        // Check for when deselecting piece
        if (piece != null){
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
    }

    private int getPieceWidth(){
        return PUZZLE_WIDTH / hTiles;
    }

    private int getPieceHeight(){
        return (int)(PUZZLE_WIDTH * imageAspectRatio / vTiles);
    }

    public void setPuzzlePieces(List<PuzzlePiece> pieces){
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
                var _piece = (PuzzlePiece)mouseEvent.getSource();
                selectPiece(_piece, false);
            });

            piece.setOnMouseDragged(event -> {
                event.consume();
                piece.setTranslateX(piece.getTranslateX() + event.getX() - 10);
                piece.setTranslateY(piece.getTranslateY() + event.getY() - 10);

                // Bounds check
                if (piece.getTranslateX() < 0) {
                    piece.setTranslateX(1);
                }
                if (piece.getTranslateX() > puzzleHeap.getPrefWidth() - getPieceWidth()){
                    piece.setTranslateX(puzzleHeap.getPrefWidth() - getPieceWidth());
                }
                if (piece.getTranslateY() < 0){
                    piece.setTranslateY(1);
                }
                if (piece.getTranslateY() > PUZZLE_WIDTH * imageAspectRatio - getPieceHeight()){
                    piece.setTranslateY(PUZZLE_WIDTH * imageAspectRatio - getPieceHeight());
                }
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
