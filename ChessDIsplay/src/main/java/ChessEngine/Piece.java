package ChessEngine;
import java.util.*;

public class Piece {
    public static final HashMap<Character,Integer> map;
    public static final HashMap<Integer,Character> reverseMap;
    public static final int NUM_TYPES = 13; // 6 of each color and empty squares
    public static final int NULL = -1;

    public static final int KING_W = 0;
    public static final int QUEEN_W = 1;
    public static final int ROOK_W = 2;
    public static final int BISHOP_W = 3;
    public static final int KNIGHT_W = 4;
    public static final int PAWN_W = 5;
    public static final int KING_B = 6;
    public static final int QUEEN_B = 7;
    public static final int ROOK_B = 8;
    public static final int BISHOP_B = 9;
    public static final int KNIGHT_B = 10;
    public static final int PAWN_B = 11;
    static {
        map = new HashMap<>();
        map.put('K',KING_W);
        map.put('Q',QUEEN_W);
        map.put('R',ROOK_W);
        map.put('B',BISHOP_W);
        map.put('N',KNIGHT_W);
        map.put('P',PAWN_W);
        map.put('k',KING_B);
        map.put('q',QUEEN_B);
        map.put('r',ROOK_B);
        map.put('b',BISHOP_B);
        map.put('n',KNIGHT_B);
        map.put('p',PAWN_B);
        reverseMap = new HashMap<>();
        reverseMap.put(KING_W,'K');
        reverseMap.put(QUEEN_W,'Q');
        reverseMap.put(ROOK_W,'R');
        reverseMap.put(BISHOP_W,'B');
        reverseMap.put(KNIGHT_W,'N');
        reverseMap.put(PAWN_W,'P');
        reverseMap.put(KING_B,'k');
        reverseMap.put(QUEEN_B,'q');
        reverseMap.put(ROOK_B,'r');
        reverseMap.put(BISHOP_B,'b');
        reverseMap.put(KNIGHT_B,'n');
        reverseMap.put(PAWN_B,'p');

    }
}
