package ChessGame;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import static ChessGame.ChessBoard.TILE_SIZE;

public class IconGraphic extends StackPane {
    public int type;
    private static final ImageView checkIcon;
    private static final ImageView moveIcon;
    private static final ImageView captureIcon;
    private static final ImageView originIcon;
    static {

        String path = "file:src/PNG/";
        Image img = new Image(path+"Check.png");
        checkIcon = new ImageView();
        checkIcon.setImage(img);
        img = new Image(path+"Move.png");
        moveIcon = new ImageView();
        moveIcon.setImage(img);
        img = new Image(path+"Capture.png");
        captureIcon = new ImageView();
        captureIcon.setImage(img);
        img = new Image(path+"origin.png");
        originIcon = new ImageView();
        originIcon.setImage(img);
    }
    // 0 - move, 1 - capture, 2 - check, 3 - origin
    public IconGraphic(int type, int x, int y) {
        this.type = type;
        move(x, y);
        ImageView Icon = new ImageView();
        if(type == 0){
            Icon.setImage(moveIcon.getImage());
        }else if(type == 1){
            Icon.setImage(captureIcon.getImage());
        }else if(type == 2){
            Icon.setImage(checkIcon.getImage());
        }else if(type == 3){
            Icon.setImage(originIcon.getImage());
        }
        Icon.setOpacity(0.70);
        Icon.setFitWidth(TILE_SIZE);
        Icon.setPreserveRatio(true);
        this.getChildren().add(Icon);
    }

    public void move(int x, int y) {
        relocate(x * TILE_SIZE, y * TILE_SIZE);
    }
}
