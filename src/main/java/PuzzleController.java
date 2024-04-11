import java.util.HashSet;

public class PuzzleController {
    private final int SIZE_X;
    private final int SIZE_Y;

    private HashSet<PuzzlePiece> solved = new HashSet<PuzzlePiece>();
    private HashSet<PuzzlePiece> heap = new HashSet<PuzzlePiece>();

    public PuzzleController(int sizeX, int sizeY){
        SIZE_X = sizeX;
        SIZE_Y = sizeY;
        generatePieces();
    }

    private void generatePieces() {
        for (int i = 0; i < SIZE_X; ++i) {
            for (int j = 0; j < SIZE_Y; ++j){
                heap.add(new PuzzlePiece(i, j));
            }
        }
    }

    public void solvePiece() {}

    public void restart() {}

    public void solve() {}
}

record PuzzlePiece(int solutionPosX, int solutionPosY) {}