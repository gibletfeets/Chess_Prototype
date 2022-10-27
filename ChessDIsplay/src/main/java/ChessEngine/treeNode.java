package ChessEngine;
import java.util.ArrayList;
//this is very WIP and is mostly being used for doing perft
//but in the future it can be cleaned up and optimized for doing search functions for computer evaluation
public class treeNode {
    public static Game internalGame;
    public Move mov; //null is root
    public ArrayList<treeNode> children;
    public int generateChildren(int depth,int [] movTypes){
        int cnt = 0;
        if(depth != 0){
            Position searchPos = internalGame.currentPosition;
            ArrayList<Move> Moves = searchPos.Moves;
            //System.out.println(Moves.size());
            for(Move M : Moves) {
                internalGame.makeMove(M);
                internalGame.generateMoves();
                Position searchPos2 = internalGame.currentPosition;
                treeNode N = new treeNode(M);
                //children.add(N);
                ArrayList<Move> Moves2 = searchPos2.Moves;
                if (Moves2.size() != 0) {
                    if(depth != 1){
                        cnt += generateChildren(depth - 1, movTypes);
                    } else {
                        cnt += 1;
                    };
                } else {
                    cnt += 1;
                }
                long to = BitBoard.squares[M.moveData&0x3F];
                long relevantKingsq = searchPos2.whiteTurn ? BitBoard.squares[searchPos2.kingSquareW] : BitBoard.squares[searchPos2.kingSquareB];
                if((to & (searchPos.blackBB | searchPos.whiteBB)) != 0){
                    movTypes[0]++;
                }
                if((((M.moveData>>>12)&(0b0110)) == 0b0110)){
                    movTypes[1]++;
                }
                if((((M.moveData>>>12)&(0b0011)) == 0b0011)){
                    movTypes[2]++;
                }
                if(searchPos2.gameState == Position.State.MATE_BLACK || searchPos2.gameState == Position.State.MATE_WHITE){
                    movTypes[4]++;
                } else if((searchPos2.getAttacks((M.moveData)&0x3F) & (relevantKingsq)) != 0){
                    movTypes[3]++;
                }

                internalGame.unmakeMove();
            }
        }
        return cnt;
    }
    public int generateChildren(int depth, int initdepth){
        int cnt = 0;
        if(depth != 0){
            Position searchPos = internalGame.currentPosition;
            ArrayList<Move> Moves = searchPos.Moves;
            //System.out.println(Moves.size());
            for(Move M : Moves) {
                String mov = M.moveToStr();
                internalGame.makeMove(M);
                internalGame.generateMoves();
                Position searchPos2 = internalGame.currentPosition;
                treeNode N = new treeNode(M);
                //children.add(N);
                ArrayList<Move> Moves2 = searchPos2.Moves;
                if (Moves2.size() != 0) {
                    if(depth == initdepth){
                        int n = generateChildren(depth - 1, initdepth);
                        cnt += n;
                        System.out.println(mov + ": " + n);
                    } else if (depth != 1) {
                        cnt += generateChildren(depth - 1, initdepth);
                    } else {
                        cnt += 1;
                    };
                } else {
                    cnt += 1;
                    if(depth == initdepth){
                        System.out.println(mov + ": " + 0);
                    }
                }
                internalGame.unmakeMove();
            }
        }
        return cnt;
    }
    public treeNode(Move M){
        children = new ArrayList<>();
        mov = M;
    }
    public treeNode(){
        children = new ArrayList<>();
        internalGame = new Game();
        internalGame.generateMoves();
        mov = null;
    }
    public treeNode(String FEN){
        children = new ArrayList<>();
        internalGame = new Game(FEN);
        internalGame.generateMoves();
        mov = null;
    }
}
