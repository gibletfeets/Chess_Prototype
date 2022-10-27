package ChessGame;

import ChessEngine.*;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
//TODO - INTERFACE - WIP
//  TODO - PROMOTE INTERFACE
//TODO - CONNECT TO DB
//TODO - LOAD GAME - DONE
//TODO - SAVE GAME - DONE
//TODO - NEW GAME - DONE
//TODO - UNDO - DONE
//TODO - POSITION TO FEN - DONE
//TODO - PLAY AS BLACK - DONE
//  TODO - FLIP BOARD VIEW - DONE
//TODO - DEBUG MOVEGEN - WIP
//TODO - CHECKMATE - DONE?
//TODO - STALEMATE - DONE?

public class ChessBoard extends Pane {
    public static final int TILE_SIZE = 75;
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;
    //ChessEngine.Position currentPosition;
    ChessEngine.Game currGame;
    int checkSq;
    private final Tile[][] board = new Tile[WIDTH][HEIGHT];
    private final Group tileGroup = new Group();
    private final Group pieceGroup = new Group();
    private final Group iconGroup = new Group();
    private PieceGraphic selectedPiece;
    private static boolean boardView;
    public void resetBoardGfx(){
        clearMoveIcons();
        clearCheckIcon();
        refreshCheck();
        refreshGraphics();
    }
    public void flipBoard(){
        boardView = !boardView;
        resetBoardGfx();
    }
    private void setCheckIcon(int sq){
        int x = getX(sq);
        int y = getY(sq);
        IconGraphic I = new IconGraphic(2,x,y);
        iconGroup.getChildren().add(I);
        board[x][y].setIcon(I);
    }
    private void setMoveIcon(int sq){
        int x = getX(sq);
        int y = getY(sq);
        IconGraphic I = new IconGraphic(3,x,y);
        iconGroup.getChildren().add(I);
        board[x][y].setIcon(I);
        for(int i = 0; i < currGame.currentPosition.Moves.size(); i++){
            Move M = currGame.currentPosition.Moves.get(i);
            if((M.moveData&(0x3F<<6)) == (sq<<6)){
                int dest = M.moveData&0x3F;
                x = getX(dest);
                y = getY(dest);
                if(board[x][y].getPiece() != null && board[x][y].getIcon() == null){
                    I = new IconGraphic(1,x,y);
                    iconGroup.getChildren().add(I);
                    board[x][y].setIcon(I);
                } else if (board[x][y].getIcon() == null){
                    I = new IconGraphic(0,x,y);
                    iconGroup.getChildren().add(I);
                    board[x][y].setIcon(I);
                }
            }
        }
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                PieceGraphic P = board[j][i].getPiece();
                if(P != null){
                    P.toFront();
                }
            }
        }
    }
    private void clearMoveIcons(){
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                IconGraphic C = board[j][i].getIcon();
                if (C != null && C.type != 2) {
                    iconGroup.getChildren().remove(C);
                    C.getChildren().clear();
                    board[j][i].setIcon(null);
                }
            }
        }
    }
    private void clearCheckIcon(){
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                IconGraphic C = board[j][i].getIcon();
                if(C != null && C.type == 2) {
                    iconGroup.getChildren().remove(C);
                    C.getChildren().clear();
                    board[j][i].setIcon(null);
                }
            }
        }
    }
    private void refreshCheck(){
        long whiteAtt = currGame.currentPosition.getSquaresAttackedBy(false);
        long blackAtt = currGame.currentPosition.getSquaresAttackedBy(true);
        if((whiteAtt & BitBoard.squares[currGame.currentPosition.kingSquareB]) != 0){
            setCheckIcon(currGame.currentPosition.kingSquareB);
        }
        else if ((blackAtt & BitBoard.squares[currGame.currentPosition.kingSquareW]) != 0){
            setCheckIcon(currGame.currentPosition.kingSquareW);
        }
    }
    private void refreshGraphics(){
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                PieceGraphic P = board[x][y].getPiece();
                if(P != null) {
                    pieceGroup.getChildren().remove(P);
                    P.getChildren().clear();
                    board[x][y].setPiece(null);
                }
                board[x][y].setColor();
                PieceGraphic pieceGraphic;
                int sq = toSq(x,y);
                if(currGame.currentPosition.board[sq] != Piece.NULL){
                    pieceGraphic = makePiece(currGame.currentPosition.board[sq], x, y);
                    board[x][y].setPiece(pieceGraphic);
                    pieceGroup.getChildren().add(pieceGraphic);
                }
            }
        }
    }
    private int toBoard(double pixel) {
        return (int)(pixel + TILE_SIZE / 2) / TILE_SIZE;
    }
    private int toSq(int x, int y){
        if(boardView) {
            return WIDTH * (7 - y) + x;
        } else {
            return WIDTH * (y) + (7-x);
        }
    }
    private int getX(int sq){
        if(boardView) {
            return sq % 8;
        } else {
            return 7-(sq%8);
        }
    }
    private int getY(int sq){
        if(boardView) {
            return 7 - sq / 8;
        } else {
            return sq/8;
        }
    }
    private void handlePlayerMove(PieceGraphic pieceGraphic) {
        if((pieceGraphic.getType()/6 == 0) == !currGame.currentPosition.whiteTurn){
            //auto cancel moves made on the wrong turn
            return;
        }
        int x0 = toBoard(pieceGraphic.getOldX());
        int y0 = toBoard(pieceGraphic.getOldY());
        int x1 = toBoard(pieceGraphic.getLayoutX());
        int y1 = toBoard(pieceGraphic.getLayoutY());
        if(x0 != x1 || y0 != y1){
            clearMoveIcons();
        }
        Move playerMove = new Move(toSq(x1,y1) | (toSq(x0,y0)<<6));
        Move M = currGame.checkIfPlayerMoveExists(playerMove.moveData);
        if(M != null) {
            clearCheckIcon();
            currGame.makeMove(M);

            board[x0][y0].setMoved();
            board[x1][y1].setMoved();
            long relevantKing;
            if(currGame.currentPosition.whiteTurn){
                relevantKing = BitBoard.squares[currGame.currentPosition.kingSquareW];
            } else {
                relevantKing = BitBoard.squares[currGame.currentPosition.kingSquareB];
            }
            if((currGame.currentPosition.getAttacks(toSq(x1,y1))&(relevantKing)) != 0){
                int sq = (currGame.currentPosition.whiteTurn) ? currGame.currentPosition.kingSquareW : currGame.currentPosition.kingSquareB;
                checkSq = sq;
                setCheckIcon(sq);
            } else {
                checkSq = -1;
            }
            refreshGraphics();
            currGame.generateMoves();
        }
        else {
            pieceGraphic.move(x0, y0);
        }
    }
    private PieceGraphic makePiece(int type, int x, int y) {
        PieceGraphic pieceGraphic = new PieceGraphic(type, x, y);
        pieceGraphic.setOnMousePressed(e -> {
            clearMoveIcons();
            if((pieceGraphic.getType()/6 == 0) == !currGame.currentPosition.whiteTurn){
                //auto cancel moves made on the wrong turn
                return;
            }
            pieceGraphic.toFront();
            pieceGraphic.mouseX = e.getSceneX();
            pieceGraphic.mouseY = e.getSceneY();
            int i = toBoard(pieceGraphic.getOldX());
            int j = toBoard(pieceGraphic.getOldY());
            setMoveIcon(toSq(i,j));
        });
        pieceGraphic.setOnMouseDragged(e -> {
            if((pieceGraphic.getType()/6 == 0) == !currGame.currentPosition.whiteTurn){
                //auto cancel moves made on the wrong turn
                return;
            }
            pieceGraphic.relocate(e.getSceneX() - pieceGraphic.mouseX + pieceGraphic.oldX, e.getSceneY() - pieceGraphic.mouseY + pieceGraphic.oldY);
        });
        pieceGraphic.setOnMouseReleased(e -> handlePlayerMove(pieceGraphic));
        return pieceGraphic;
    }
    public ChessBoard() {
        boardView = true;
        this.setPrefSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        this.getChildren().addAll(tileGroup,iconGroup, pieceGroup);
        currGame = new Game();
        currGame.generateMoves();
        long beginTime = System.currentTimeMillis();
        perft(5);
        long totalTime = System.currentTimeMillis() - beginTime;
        System.out.println("Completed in " + totalTime + " ms.");
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Tile tile = new Tile((x + y) % 2 == 0, x, y);
                board[x][y] = tile;
                tileGroup.getChildren().add(tile);
                PieceGraphic pieceGraphic;
                int sq = toSq(x,y);
                if(currGame.currentPosition.board[sq] != Piece.NULL){
                    pieceGraphic = makePiece(currGame.currentPosition.board[sq], x, y);
                    tile.setPiece(pieceGraphic);
                    pieceGroup.getChildren().add(pieceGraphic);
                }
            }
        }
        refreshCheck();
    }
    public void perft(int n){
        //PERFT
        //captures, en passants, castles, checks
        treeNode N = new treeNode();
        int[] movTypes = {0,0,0,0,0};
        System.out.println(N.generateChildren(n,movTypes) + " Nodes");
        System.out.println(movTypes[0] + " Captures");
        System.out.println(movTypes[1] + " E.p.");
        System.out.println(movTypes[2] + " Castles");
        System.out.println(movTypes[3] + " Checks");
        System.out.println(movTypes[4] + " Checkmates");
    }
    public void perftDivide(int n){
        treeNode N = new treeNode();
        N.generateChildren(n,n);
    }
    public void perft(int n, String FEN){
        //PERFT
        //captures, en passants, castles, checks
        treeNode N = new treeNode(FEN);
        int[] movTypes = {0,0,0,0,0};
        System.out.println(N.generateChildren(n,movTypes) + " Nodes");
        System.out.println(movTypes[0] + " Captures");
        System.out.println(movTypes[1] + " E.p.");
        System.out.println(movTypes[2] + " Castles");
        System.out.println(movTypes[3] + " Checks");
        System.out.println(movTypes[4] + " Checkmates");
    }
    public void perftDivide(int n, String FEN){
        treeNode N = new treeNode(FEN);
        N.generateChildren(n,n);
    }

}