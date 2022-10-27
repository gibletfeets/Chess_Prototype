package ChessGame;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
public class Tile extends Rectangle {

    private PieceGraphic pieceGraphic;
    private IconGraphic iconGraphic;
    boolean COLOR; // true for light, false for dark
    public PieceGraphic getPiece() {
        return pieceGraphic;
    }
    public void setPiece(PieceGraphic pieceGraphic) {
        this.pieceGraphic = pieceGraphic;
    }
    public void setColor(){
        setFill(COLOR ? Color.valueOf("#9F90B0") : Color.valueOf("#7D4A8D"));
    }
    public void setMoved(){
        setFill(COLOR ? Color.valueOf("#9EA768") : Color.valueOf("#8A7E53"));
    }
    public IconGraphic getIcon() {
        return iconGraphic;
    }
    public void setIcon(IconGraphic iconGraphic){
        this.iconGraphic = iconGraphic;
    }
    public Tile(boolean light, int x, int y) {
        setWidth(ChessBoard.TILE_SIZE);
        setHeight(ChessBoard.TILE_SIZE);

        relocate(x * ChessBoard.TILE_SIZE, y * ChessBoard.TILE_SIZE);
        COLOR = light;
        setColor();
    }
}
