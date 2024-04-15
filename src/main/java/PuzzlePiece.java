import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PuzzlePiece extends ImageView {
    // Expected position for puzzle to be solved
    public int x;
    public int y;
    public PuzzlePiece(Image image, int x, int y) {
        super(image);
        this.x = x;
        this.y = y;
    }
}
