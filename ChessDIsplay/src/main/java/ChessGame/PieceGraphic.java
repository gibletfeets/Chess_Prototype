package ChessGame;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import static ChessGame.ChessBoard.TILE_SIZE;

public class PieceGraphic extends StackPane {

    private final int type;

    public double mouseX, mouseY;
    public double oldX, oldY;

    public int getType() {
        return type;
    }

    public double getOldX() {
        return oldX;
    }

    public double getOldY() {
        return oldY;
    }
    public static ImageView[] pieceGraphics;
    static {

        String path = "file:src/PNG/";
        pieceGraphics = new ImageView[12];
        String chars = "KQRBNPwb";
        for(int i = 0; i < 12; i++){
            String str = path + chars.charAt(6+i/6) + chars.charAt(i%6) + ".png";
            //System.out.println(str);
            Image img = new Image(str);
            pieceGraphics[i] = new ImageView();
            pieceGraphics[i].setImage(img);
        }
    }
    public PieceGraphic(int type, int x, int y) {
        this.type = type;
        move(x, y);
        ImageView pieceIcon = new ImageView();
        pieceIcon.setImage(pieceGraphics[type].getImage());
        pieceIcon.setFitWidth(TILE_SIZE);
        pieceIcon.setPreserveRatio(true);
        this.getChildren().add(pieceIcon);
    }

    public void move(int x, int y) {
        oldX = x * TILE_SIZE;
        oldY = y * TILE_SIZE;
        relocate(oldX, oldY);
    }
}
