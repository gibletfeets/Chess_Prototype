package ChessEngine;
import java.util.Stack;
public class Game {
    private final static String standardFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    public Stack<Position> positions;
    public Stack<Move> movesMade;
    public Position currentPosition;
    Move lastMove;
    public Game(){
        currentPosition = new Position(standardFEN);
        positions = new Stack<>();
        movesMade = new Stack<>();
    }
    public Game(String FEN){
        currentPosition = new Position(FEN);
        positions = new Stack<>();
        movesMade = new Stack<>();
    }
    public Move checkIfPlayerMoveExists(int movA){
        for(int i = 0; i < currentPosition.Moves.size(); i++){
            int movB =  currentPosition.Moves.get(i).moveData & (0xFFF);
            //same to and from
            if(movA == movB){
                //currentPosition.Moves.get(i).printMove();
                return  currentPosition.Moves.get(i);
            }
        }
        return null;
    }
    public void generateMoves(){
        currentPosition.Moves = currentPosition.generateMoves();
    }
    public void makeMove(Move M){
        //System.out.println(currentPosition.moveToStr(M));
        positions.push(currentPosition);
        currentPosition = new Position(currentPosition);
        currentPosition.makeMove(M);
        movesMade.push(lastMove);
        lastMove = M;
        //Position.printPosition(currentPosition);
    }
    public void unmakeMove(){
        if(positions.size() > 0) {
            currentPosition = positions.pop();
            lastMove = movesMade.pop();
        }
        //Position.printPosition(currentPosition);
    }
}
