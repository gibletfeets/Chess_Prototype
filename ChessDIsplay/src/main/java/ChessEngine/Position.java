package ChessEngine;
import java.util.Arrays;
import java.util.ArrayList;
public class Position {

    private static final long CASTLE_OO_W = BitBoard.ranks[0] & (BitBoard.files[5] | BitBoard.files[6]);
    private static final long CASTLE_OOO_W = BitBoard.ranks[0] & (BitBoard.files[1] | BitBoard.files[2] | BitBoard.files[3]);
    private static final long CASTLE_OO_B = BitBoard.ranks[7] & (BitBoard.files[5] | BitBoard.files[6]);
    private static final long CASTLE_OOO_B = BitBoard.ranks[7] & (BitBoard.files[1] | BitBoard.files[2] | BitBoard.files[3]);
    private static final int OO_W  = 0b1000;
    private static final int OOO_W = 0b0100;
    private static final int OO_B  = 0b0010;
    private static final int OOO_B = 0b0001;
    public enum State{
        ONGOING,
        MATE_WHITE,
        MATE_BLACK,
        STALEMATE_WHITE,        //White unable to move
        STALEMATE_BLACK,        //Black unable to move
        DRAW_REPETITION,        //Draw by 3 move repetition rule
        DRAW_FIFTY,             //Draw by 50 move rule
        DRAW_MATE_IMPOSSIBLE,   //Draw due to mating being impossible
        RESIGN_WHITE,
        RESIGN_BLACK
    }
    //TODO - break check with en passant
    public State gameState;
    public ArrayList<Move> Moves;
    public long[] pieceBBs = new long[Piece.NUM_TYPES];
    public long whiteBB;
    public long blackBB;
    public int[] board = new int[64];
    public int kingSquareW;
    public int kingSquareB;
    public boolean whiteTurn;
    //castling availability flag
    public int castleFlags;
    public int enPassantsq;
    public int halfTurnClock;
    public int turn;
    //castling masks



    public void setOccupancy(){
        whiteBB = 0L;
        blackBB = 0L;
        for(int i = 0; i < 6; i++) {
            whiteBB |= pieceBBs[i];
            blackBB |= pieceBBs[i+6];
        }
    }
    //init constructor that parses FEN notation
    public Position (String FEN){
        castleFlags = 0xF;
        //i love how gross it looks to try to make the regex [\\/\s]
        String[] strs = FEN.split("[\\\\/\\s]",-2);
        for(int i = 7; i >= 0; i--){
            int x = 0;
            for(int j = 0; j < strs[i].length(); j++){
                char c = strs[i].charAt(j);
                int sq = ((7-i)*8)+x;
                if(Character.isDigit(c)){
                    c -= 48;
                    for(int k = 0; k < c; k++){
                        sq = ((7-i)*8)+x+k;
                        board[sq] = Piece.NULL;
                    }
                    x += c;
                } else {
                    int piece = Piece.map.get(c);
                    pieceBBs[piece] |= BitBoard.squares[sq];
                    board[sq] = piece;
                    if(piece == Piece.KING_W){
                        kingSquareW = sq;
                    }
                    else if(piece == Piece.KING_B){
                        kingSquareB = sq;
                    }
                    x++;
                }
            }
        }
        setOccupancy();
        whiteTurn = strs[8].charAt(0) == 'w';
        try {
            for (int i = 0; i < strs[9].length(); i++) {
                char c = strs[9].charAt(i);
                switch (c) {
                    case 'K' -> castleFlags |= CASTLE_OO_W;
                    case 'Q' -> castleFlags |= CASTLE_OOO_W;
                    case 'k' -> castleFlags |= CASTLE_OO_B;
                    case 'q' -> castleFlags |= CASTLE_OOO_B;
                }
            }
        } catch (Exception E){
            E.printStackTrace();
            E.getCause();
        }
        try {
            if (strs[10].charAt(0) != '-') {
                int x = strs[10].charAt(0) - 'a';
                int y = strs[10].charAt(1) - '1';
                enPassantsq = 8 * y + x;
            } else {
                enPassantsq = -1;
            }
        } catch (Exception E){
            enPassantsq = -1;
        }
        try {
            halfTurnClock = Integer.parseInt(strs[11]);
            turn = Integer.parseInt(strs[12]);
        } catch (Exception E){
            halfTurnClock = 0;
            turn = 0;
        }
        gameState = State.ONGOING;
        //printPosition(this);
    }
    public Position (Position P){
        this.pieceBBs = Arrays.copyOf(P.pieceBBs,Piece.NUM_TYPES);
        this.whiteBB = P.whiteBB;
        this.blackBB = P.blackBB;
        this.board = Arrays.copyOf(P.board,64);
        this.kingSquareW = P.kingSquareW;
        this.kingSquareB = P.kingSquareB;
        this.whiteTurn = P.whiteTurn;
        this.castleFlags = P.castleFlags;
        this.enPassantsq = P.enPassantsq;
        this.halfTurnClock = P.halfTurnClock;
        this.turn = P.turn;
        this.Moves = new ArrayList<>();
        this.gameState = P.gameState;
        for(Move M : P.Moves){
            this.Moves.add(new Move(M.moveData));
        }
    }
    public static void printPosition(Position pos) {
        System.out.println("    a  b  c  d  e  f  g  h");
        System.out.println("  --------------------------");
        for (int i = 7; i >= 0; i--) {
            System.out.print(Character.toString('1' + i) + " |");
            for (int j = 0; j < 8; j++) {
                int piece = pos.board[i*8+j]+1;
                String chars = ".KQRBNPkqrbnp";
                System.out.print(" " +chars.charAt(piece)+" ");
            }
            System.out.print("| " + Character.toString('1' + i));
            System.out.print('\n');
        }
        System.out.println("  --------------------------");
        System.out.println("    a  b  c  d  e  f  g  h");
        System.out.print('\n');
    }
    public String getFEN(){
        String str = "";
        int c = 0;
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                int sq = j+(7-i)*8;
                if(board[sq] == Piece.NULL){
                    c++;
                } else {
                    if(c != 0){
                        str += c;
                        c = 0;
                    }
                    str += Piece.reverseMap.get(board[sq]);
                }
            }
            if(c != 0){
                str += c;
                c = 0;
            }
            if (i != 7) str += "/";
        }
        str += " ";
        str += (whiteTurn) ? 'w' : 'b';
        str += " ";
        if(castleFlags == 0){
            str += "-";
        } else {
            String castle = "qkQK";
            for(int i = 3; i >= 0; i--){
                if (((castleFlags>>i)&1)!=0){
                    str += castle.charAt(i);
                }
            }
        }
        str += " ";
        if(enPassantsq != -1){
            char rank = (char)('a'+(enPassantsq%8));
            char file = (char)('1'+(enPassantsq/8));
            str += rank;
            str += file;
        } else {
            str += "-";
        }
        str += " ";
        str += halfTurnClock;
        str += " ";
        str += turn;
        return str;
    }
    public String moveToStr(Move M){
        String fileStr = "abcdefgh";
        String rankStr = "12345678";
        String[] pieceNames = {
                "K","Q","R","B","N",""
        };
        String str = "";
        int to = M.moveData & 0x3F;
        int from = (M.moveData & (0x3F << 6)) >>> 6;
        int pc = board[from];
        int flag = (M.moveData>>>12)&0b1111;
        if(flag == 0b011) {
            if(to%8 == 6){
                str += "O-O";
            }
            else {
                str += "O-O-O";
            }
        }
        else if (flag == 0b0110) {
            str += fileStr.charAt(from%8);
            str += "x";
            str += fileStr.charAt(to%8);
            str += rankStr.charAt(to/8);
            str += " e.p.";
        }
        else {
            ArrayList<Move> sameFile = new ArrayList<>();
            ArrayList<Move> sameRank = new ArrayList<>();
            str += pieceNames[pc % 6];
            for (Move M2 : Moves) {
                if(M2 != M) {
                    int to2 = M2.moveData & 0x3F;
                    int from2 = (M2.moveData & (0x3F << 6)) >>> 6;
                    int pc2 = board[from2];
                    if (to == to2 && pc == pc2) {
                        if (from % 8 == from2 % 8) {
                            sameRank.add(M2);
                        }
                        else if (from / 8 == from2 / 8) {
                            sameFile.add(M2);
                        }
                        else {
                            sameRank.add(M2);
                        }
                    }
                }
            }
            if (sameRank.size() != 0 && sameFile.size() != 0) {
                str += fileStr.charAt(from % 8);
                str += rankStr.charAt(from / 8);
            }
            else if (sameRank.size() != 0 || ((pc%6) == Piece.PAWN_W && board[to] != Piece.NULL)) {
                str += fileStr.charAt(from % 8);
            }
            else if (sameFile.size() != 0) {
                str += rankStr.charAt(from / 8);
            }
            if (board[to] != Piece.NULL) {
                str += "x";
            }
            str += fileStr.charAt(to % 8);
            str += rankStr.charAt(to / 8);
        }
        return str;
    }
    public long getCheckMask(int sq){
        if(board[sq] < 6){
            if (board[sq] < Piece.KNIGHT_W && board[sq] != Piece.KING_W){

                return BitBoard.checkMasks.get(BitBoard.squares[sq]|BitBoard.squares[kingSquareB]);
            } else {
                return BitBoard.squares[sq];
            }
        } else {
            if (board[sq] < Piece.KNIGHT_B && board[sq] != Piece.KING_B){
                return BitBoard.checkMasks.get(BitBoard.squares[sq] | BitBoard.squares[kingSquareW]);
            } else {
                return BitBoard.squares[sq];
            }
        }
    }
    public long getAttacks(int sq){
        int pieceType = board[sq];
        long attacks = 0x0000000000000000L;
        //TODO - make this branchless
        if (pieceType%6 == Piece.ROOK_W){
            attacks = BitBoard.rookAttacks(sq,(whiteBB|blackBB)^BitBoard.squares[sq]);
        }
        else if (pieceType%6 == Piece.QUEEN_W){
            attacks = BitBoard.queenAttacks(sq,(whiteBB|blackBB)^BitBoard.squares[sq]);
        }
        else if (pieceType%6 == Piece.BISHOP_W){
            attacks = BitBoard.bishopAttacks(sq,(whiteBB|blackBB)^BitBoard.squares[sq]);
        }
        else if (pieceType%6 == Piece.KNIGHT_W){
            attacks = BitBoard.knightAttacks[sq];
        }
        else if (pieceType%6 == Piece.KING_W){
            attacks = BitBoard.kingAttacks[sq];
        }
        else if (pieceType == Piece.PAWN_W){
            attacks = BitBoard.wPawnAttacks[sq];
        }
        else if (pieceType == Piece.PAWN_B){
            attacks = BitBoard.bPawnAttacks[sq];
        }
        if(pieceType < 6) {
            attacks &= ~whiteBB;
        }
        else{
            attacks &= ~blackBB;
        }
        return attacks;
    }
    private long getAttackSquares(int sq, int k){
        int pieceType = board[sq];
        long attacks = 0x0000000000000000L;
        //TODO - make this branchless
        if (pieceType%6 == Piece.ROOK_W){
            attacks = BitBoard.rookAttacks(sq,(whiteBB|blackBB)^BitBoard.squares[sq]^BitBoard.squares[k]);
        }
        else if (pieceType%6 == Piece.QUEEN_W){
            attacks = BitBoard.queenAttacks(sq,(whiteBB|blackBB)^BitBoard.squares[sq]^BitBoard.squares[k]);
        }
        else if (pieceType%6 == Piece.BISHOP_W){
            attacks = BitBoard.bishopAttacks(sq,(whiteBB|blackBB)^BitBoard.squares[sq]^BitBoard.squares[k]);
        }
        else if (pieceType%6 == Piece.KNIGHT_W){
            attacks = BitBoard.knightAttacks[sq];
        }
        else if (pieceType%6 == Piece.KING_W){
            attacks = BitBoard.kingAttacks[sq];
        }
        else if (pieceType == Piece.PAWN_W){
            attacks = BitBoard.wPawnAttacks[sq];
        }
        else if (pieceType == Piece.PAWN_B){
            attacks = BitBoard.bPawnAttacks[sq];
        }
        return attacks;
    }
    /*
    public long getSquaresAttackedBy_DEPRECATED(boolean side){
        long mask = 0L;
        if (!side){
            for(int i = 0; i < 6; i++){
                long board = pieceBBs[i];
                while(board != 0){
                    int sq = BitBoard.bitScanForwardDbl(board);
                    mask |= getAttackSquares(sq, kingSquareB);
                    board &= board-1;
                }
            }
        } else {
            for(int i = 6; i < 12; i++){
                long board = pieceBBs[i];
                while(board != 0){
                    int sq = BitBoard.bitScanForwardDbl(board);
                    mask |= getAttackSquares(sq, kingSquareW);
                    board &= board-1;
                }
            }
        }
        return mask;
    }
    */

    public long getSquaresAttackedBy(boolean side){
        long mask = 0L;
        long board;
        if (!side){
            board = pieceBBs[Piece.KING_W];
            while(board != 0){
                int sq = BitBoard.bitScanForwardDbl(board);
                mask |= BitBoard.kingAttacks[sq];
                board &= board-1;
            }
            board = pieceBBs[Piece.QUEEN_W];
            while(board != 0){
                int sq = BitBoard.bitScanForwardDbl(board);
                long mv = BitBoard.queenAttacks(sq,(whiteBB|blackBB)^BitBoard.squares[sq]^BitBoard.squares[kingSquareW]);
                mask |= mv;
                board &= board-1;
                if((mv & BitBoard.squares[relevantKing]) != 0){
                    checkMask &= getCheckMask(sq);
                }
            }
            board = pieceBBs[Piece.ROOK_W];
            while(board != 0){
                int sq = BitBoard.bitScanForwardDbl(board);
                long mv = BitBoard.rookAttacks(sq,(whiteBB|blackBB)^BitBoard.squares[sq]^BitBoard.squares[kingSquareW]);
                mask |= mv;
                board &= board-1;
                if((mv & BitBoard.squares[relevantKing]) != 0){
                    checkMask &= getCheckMask(sq);
                }
            }
            board = pieceBBs[Piece.BISHOP_W];
            while(board != 0){
                int sq = BitBoard.bitScanForwardDbl(board);
                long mv = BitBoard.bishopAttacks(sq,(whiteBB|blackBB)^BitBoard.squares[sq]^BitBoard.squares[kingSquareW]);
                mask |= mv;
                board &= board-1;
                if((mv & BitBoard.squares[relevantKing]) != 0){
                    checkMask &= getCheckMask(sq);
                }
            }
            board = pieceBBs[Piece.KNIGHT_W];
            while(board != 0){
                int sq = BitBoard.bitScanForwardDbl(board);
                long mv = BitBoard.knightAttacks[sq];
                mask |= mv;
                board &= board-1;
                if((mv & BitBoard.squares[relevantKing]) != 0){
                    checkMask &= getCheckMask(sq);
                }
            }
            board = pieceBBs[Piece.PAWN_W];
            while(board != 0){
                int sq = BitBoard.bitScanForwardDbl(board);
                long mv = BitBoard.wPawnAttacks[sq];
                mask |= mv;
                board &= board-1;
                if((mv & BitBoard.squares[relevantKing]) != 0){
                    checkMask &= getCheckMask(sq);
                }
            }
        } else {
            board = pieceBBs[Piece.KING_B];
            while(board != 0){
                int sq = BitBoard.bitScanForwardDbl(board);
                mask |= BitBoard.kingAttacks[sq];
                board &= board-1;
            }
            board = pieceBBs[Piece.QUEEN_B];
            while(board != 0){
                int sq = BitBoard.bitScanForwardDbl(board);
                long mv = BitBoard.queenAttacks(sq,(whiteBB|blackBB)^BitBoard.squares[sq]^BitBoard.squares[kingSquareB]);
                mask |= mv;
                board &= board-1;
                if((mv & BitBoard.squares[relevantKing]) != 0){
                    checkMask &= getCheckMask(sq);
                }
            }
            board = pieceBBs[Piece.ROOK_B];
            while(board != 0){
                int sq = BitBoard.bitScanForwardDbl(board);
                long mv = BitBoard.rookAttacks(sq,(whiteBB|blackBB)^BitBoard.squares[sq]^BitBoard.squares[kingSquareB]);
                mask |= mv;
                board &= board-1;
                if((mv & BitBoard.squares[relevantKing]) != 0){
                    checkMask &= getCheckMask(sq);
                }
            }
            board = pieceBBs[Piece.BISHOP_B];
            while(board != 0){
                int sq = BitBoard.bitScanForwardDbl(board);
                long mv = BitBoard.bishopAttacks(sq,(whiteBB|blackBB)^BitBoard.squares[sq]^BitBoard.squares[kingSquareB]);
                mask |= mv;
                board &= board-1;
                if((mv & BitBoard.squares[relevantKing]) != 0){
                    checkMask &= getCheckMask(sq);
                }
            }
            board = pieceBBs[Piece.KNIGHT_B];
            while(board != 0){
                int sq = BitBoard.bitScanForwardDbl(board);
                long mv = BitBoard.knightAttacks[sq];
                mask |= mv;
                board &= board-1;
                if((mv & BitBoard.squares[relevantKing]) != 0){
                    checkMask &= getCheckMask(sq);
                }
            }
            board = pieceBBs[Piece.PAWN_B];
            while(board != 0){
                int sq = BitBoard.bitScanForwardDbl(board);
                long mv = BitBoard.bPawnAttacks[sq];
                mask |= mv;
                board &= board-1;
                if((mv & BitBoard.squares[relevantKing]) != 0){
                    checkMask &= getCheckMask(sq);
                }
            }
        }
        return mask;
    }

    long checkMask = ~0L;
    int relevantKing = -1;
    public ArrayList<Move> generateMoves(){
        //TODO -- Recomment
        //TODO -- Fix issues where results do not match perft
        //TODO -- More optimization
        //TODO -- See if there's something weird going on in pinning logic?
        ArrayList<Move> generatedMoves = new ArrayList<>();
        relevantKing = (whiteTurn) ? kingSquareW : kingSquareB;
        long attMask = getSquaresAttackedBy(whiteTurn);
        checkMask = ~0L;
        long rookPin = 0L;
        long bishopPin = 0L;
        long occupied = whiteBB|blackBB;
        long kingBlockers = BitBoard.queenAttacks(relevantKing,occupied)&occupied;
        long opponent_RQ;
        long opponent_BQ;
        if(whiteTurn){
            opponent_RQ = pieceBBs[Piece.ROOK_B] | pieceBBs[Piece.QUEEN_B];
            opponent_BQ = pieceBBs[Piece.BISHOP_B] | pieceBBs[Piece.QUEEN_B];
        } else {
            opponent_RQ = pieceBBs[Piece.ROOK_W] | pieceBBs[Piece.QUEEN_W];
            opponent_BQ = pieceBBs[Piece.BISHOP_W] | pieceBBs[Piece.QUEEN_W];
        }
        long pinners = BitBoard.xrayRookAttacks(occupied,kingBlockers,relevantKing) & opponent_RQ;
        while(pinners != 0){
            int sq = BitBoard.bitScanForwardDbl(pinners);
            rookPin |= BitBoard.checkMasks.get(BitBoard.squares[sq]|BitBoard.squares[relevantKing]);
            pinners &= pinners - 1;
        }
        pinners = BitBoard.xrayBishopAttacks(occupied,kingBlockers,relevantKing) & opponent_BQ;
        while(pinners != 0){
            int sq = BitBoard.bitScanForwardDbl(pinners);
            bishopPin |= BitBoard.checkMasks.get(BitBoard.squares[sq]|BitBoard.squares[relevantKing]);
            pinners &= pinners - 1;
        }

        long[] mvsArr = new long[64];
        if(whiteTurn){
            long board;
            int sq;
            //Calculate king move and prune with the inverse of the opponent attack mask
            mvsArr[kingSquareW] = BitBoard.kingAttacks[kingSquareW]&~attMask&checkMask&~whiteBB;
            //Calculate pawn moves
            board = pieceBBs[Piece.PAWN_W];
            while(board != 0){
                sq = BitBoard.bitScanForwardDbl(board);
                mvsArr[sq] = BitBoard.wPawnAttacks[sq]&blackBB;
                board &= board-1;
                if ((BitBoard.squares[sq] & rookPin) != 0){
                    mvsArr[sq] = 0L;
                } else if ((BitBoard.squares[sq] & bishopPin) != 0) {
                    mvsArr[sq] &= bishopPin;
                }
            }
            //Calculate knight moves
            board = pieceBBs[Piece.KNIGHT_W];
            while(board != 0){
                sq = BitBoard.bitScanForwardDbl(board);
                mvsArr[sq] = BitBoard.knightAttacks[sq]&~whiteBB;
                board &= board-1;
                if((BitBoard.squares[sq] & (rookPin | bishopPin)) != 0) {
                    mvsArr[sq] &= 0L;
                }
            }
            //Calculate bishop moves
            board = pieceBBs[Piece.BISHOP_W];
            while(board != 0){
                sq = BitBoard.bitScanForwardDbl(board);
                mvsArr[sq] = BitBoard.bishopAttacks(sq,(whiteBB|blackBB)^BitBoard.squares[sq]^BitBoard.squares[kingSquareW])&~whiteBB;
                board &= board-1;
                if ((BitBoard.squares[sq] & rookPin) != 0) {
                    mvsArr[sq] = 0L;
                } else if ((BitBoard.squares[sq] & bishopPin) != 0) {
                    mvsArr[sq] &= bishopPin;
                }
            }
            //Calculate rook moves
            board = pieceBBs[Piece.ROOK_W];
            while(board != 0){
                sq = BitBoard.bitScanForwardDbl(board);
                mvsArr[sq] = BitBoard.rookAttacks(sq,(whiteBB|blackBB)^BitBoard.squares[sq]^BitBoard.squares[kingSquareW])&~whiteBB;
                board &= board-1;
                if ((BitBoard.squares[sq] & rookPin) != 0) {
                    mvsArr[sq] = 0L;
                } else if ((BitBoard.squares[sq] & bishopPin) != 0) {
                    mvsArr[sq] &= bishopPin;
                }
            }
            //Calculate queen moves
            board = pieceBBs[Piece.QUEEN_W];
            while(board != 0){
                sq = BitBoard.bitScanForwardDbl(board);
                mvsArr[sq] = BitBoard.queenAttacks(sq,(whiteBB|blackBB)^BitBoard.squares[sq]^BitBoard.squares[kingSquareW])&~whiteBB;
                board &= board-1;
                if ((BitBoard.squares[sq] & rookPin) != 0) {
                    mvsArr[sq] &= (BitBoard.rookAttackMasksPin[sq]&rookPin);
                } else if ((BitBoard.squares[sq] & bishopPin) != 0) {
                    mvsArr[sq] &= (BitBoard.bishopAttackMasksPin[sq]&bishopPin);
                }
            }
        } else {
            long board;
            int sq;
            //Calculate king move and prune with the inverse of the opponent attack mask
            mvsArr[kingSquareB] = BitBoard.kingAttacks[kingSquareB]&~attMask&checkMask&~blackBB;
            //Calculate pawn moves
            board = pieceBBs[Piece.PAWN_B];
            while(board != 0){
                sq = BitBoard.bitScanForwardDbl(board);
                mvsArr[sq] = BitBoard.bPawnAttacks[sq]&whiteBB;
                board &= board-1;
                if ((BitBoard.squares[sq] & rookPin) != 0){
                    mvsArr[sq] = 0L;
                } else if ((BitBoard.squares[sq] & bishopPin) != 0) {
                    mvsArr[sq] &= bishopPin;
                }
            }
            //Calculate knight moves
            board = pieceBBs[Piece.KNIGHT_B];
            while(board != 0){
                sq = BitBoard.bitScanForwardDbl(board);
                mvsArr[sq] = BitBoard.knightAttacks[sq]&~blackBB;
                board &= board-1;
                if((BitBoard.squares[sq] & (rookPin | bishopPin)) != 0) {
                    mvsArr[sq] &= 0L;
                }
            }
            //Calculate bishop moves
            board = pieceBBs[Piece.BISHOP_B];
            while(board != 0){
                sq = BitBoard.bitScanForwardDbl(board);
                mvsArr[sq] = BitBoard.bishopAttacks(sq,(whiteBB|blackBB)^BitBoard.squares[sq]^BitBoard.squares[kingSquareB])&~blackBB;
                board &= board-1;
                if ((BitBoard.squares[sq] & rookPin) != 0) {
                    mvsArr[sq] = 0L;
                } else if ((BitBoard.squares[sq] & bishopPin) != 0) {
                    mvsArr[sq] &= bishopPin;
                }
            }
            //Calculate rook moves
            board = pieceBBs[Piece.ROOK_B];
            while(board != 0){
                sq = BitBoard.bitScanForwardDbl(board);
                mvsArr[sq] = BitBoard.rookAttacks(sq,(whiteBB|blackBB)^BitBoard.squares[sq]^BitBoard.squares[kingSquareB])&~blackBB;
                board &= board-1;
                if ((BitBoard.squares[sq] & rookPin) != 0) {
                    mvsArr[sq] = 0L;
                } else if ((BitBoard.squares[sq] & bishopPin) != 0) {
                    mvsArr[sq] &= bishopPin;
                }
            }
            //Calculate queen moves
            board = pieceBBs[Piece.QUEEN_B];
            while(board != 0){
                sq = BitBoard.bitScanForwardDbl(board);
                mvsArr[sq] = BitBoard.queenAttacks(sq,(whiteBB|blackBB)^BitBoard.squares[sq]^BitBoard.squares[kingSquareB])&~blackBB;
                board &= board-1;
                if ((BitBoard.squares[sq] & rookPin) != 0) {
                    mvsArr[sq] &= (BitBoard.rookAttackMasksPin[sq]&rookPin);
                } else if ((BitBoard.squares[sq] & bishopPin) != 0) {
                    mvsArr[sq] &= (BitBoard.bishopAttackMasksPin[sq]&bishopPin);
                }
            }
        }

        for(int i = 0; i < 64; i++){
            while(mvsArr[i] != 0){
                int j = BitBoard.bitScanForwardDbl(mvsArr[i]);
                //long toFrom = BitBoard.squares[i]|BitBoard.squares[j];
                Move m = new Move(i, j, 0b0000);
                if(board[i]%6 == Piece.PAWN_W){
                    if(j/8 == 0 || j/8 == 7){
                        for(int k = 0; k < 4; k++) {
                            m = new Move(i, j, (k<<2)|0b0001);
                            generatedMoves.add(m);
                        }
                        mvsArr[i] &= mvsArr[i] - 1;
                        continue;
                    }
                }
                generatedMoves.add(m);
                mvsArr[i] &= mvsArr[i] - 1;
            }
        }

        //handle pawn pushes
        int pawnType = whiteTurn ? Piece.PAWN_W : Piece.PAWN_B;
        long pushPawns;
        long doublePushPawns;
        long empty = ~(whiteBB | blackBB);
        if(whiteTurn){
            long dblCheckMask = checkMask | (checkMask >>> 8);
            pushPawns = BitBoard.wPawnsAbleToPush(pieceBBs[Piece.PAWN_W],empty&checkMask);
            doublePushPawns = BitBoard.wPawnsAbleToDoublePush(pieceBBs[Piece.PAWN_W],empty&dblCheckMask);
        } else {
            long dblCheckMask = checkMask | (checkMask << 8);
            pushPawns = BitBoard.bPawnsAbleToPush(pieceBBs[Piece.PAWN_B],empty&checkMask);
            doublePushPawns = BitBoard.bPawnsAbleToDoublePush(pieceBBs[Piece.PAWN_B],empty&dblCheckMask);
        }
        int offset = whiteTurn ? 8 : -8;
        while (pushPawns != 0){
            int i = BitBoard.bitScanForwardDbl(pushPawns);
            long toFrom = BitBoard.squares[i]|BitBoard.squares[i+offset];
            if((BitBoard.squares[i] & rookPin) == 0 && (BitBoard.squares[i] & bishopPin) == 0) {
                //if going to back rank they can promote
                if((i+offset)/8 == ((whiteTurn) ? 7 : 0)){
                    for(int j = 0; j < 4; j++){
                        Move m = new Move(i, i + offset, (j<<2)|0b0001);
                        generatedMoves.add(m);
                    }
                } else {
                    Move m = new Move(i, i + offset, 0b0000);
                    generatedMoves.add(m);
                }
            } else if (Long.bitCount(toFrom&rookPin) == 2){
                if((i+offset)/8 == ((whiteTurn) ? 7 : 0)){
                    for(int j = 0; j < 4; j++){
                        Move m = new Move(i, i + offset, (j<<2)|0b0001);
                        generatedMoves.add(m);
                    }
                } else {
                    Move m = new Move(i, i + offset, 0b0000);
                    generatedMoves.add(m);
                }
            }
            pushPawns &= pushPawns - 1;
        }
        while (doublePushPawns != 0){
            int i = BitBoard.bitScanForwardDbl(doublePushPawns);
            long toFrom = (BitBoard.squares[i]|BitBoard.squares[i+offset*2]);
            if((BitBoard.squares[i] & rookPin) == 0 && (BitBoard.squares[i] & bishopPin) == 0) {
                Move m = new Move(i, i + offset*2, 0b0010);
                generatedMoves.add(m);
            } else if (Long.bitCount(toFrom&rookPin) == 2){
                Move m = new Move(i, i + offset*2, 0b0010);
                generatedMoves.add(m);
            }
            doublePushPawns &= doublePushPawns - 1;
        }
        //handle potential en passant captures
        if(enPassantsq != -1){
            int toSq = enPassantsq;
            if(enPassantsq%8 != 0){
                int wSq = whiteTurn ? enPassantsq-9 : enPassantsq+7;
                generateEnPassant(generatedMoves, checkMask, rookPin, bishopPin, pawnType, toSq, wSq);
            }
            if(enPassantsq%8 != 7){
                int eSq = whiteTurn ? enPassantsq-7 : enPassantsq+9;
                generateEnPassant(generatedMoves, checkMask, rookPin, bishopPin, pawnType, toSq, eSq);
            }
        }

        // just wanted to generalize this via ternaries rather than writing so many lines . . .
        boolean CASTLE_OO;
        boolean CASTLE_OOO;
        if(whiteTurn){
            CASTLE_OO = ((castleFlags&Position.OO_W) != 0);
            CASTLE_OOO = ((castleFlags&Position.OOO_W) != 0);
        } else {
            CASTLE_OO = ((castleFlags&Position.OO_B) != 0);
            CASTLE_OOO = ((castleFlags&Position.OOO_B) != 0);
        }
        //king not in check
        if(checkMask == (~0L) && (CASTLE_OO | CASTLE_OOO)){
            long bb_OO = (whiteTurn ? CASTLE_OO_W : CASTLE_OO_B);
            long bb_OOO = (whiteTurn ? CASTLE_OOO_W : CASTLE_OOO_B);
            if(CASTLE_OO && (bb_OO & (attMask|occupied)) == 0){
                int dat = whiteTurn ? 0b0011000100000110 : 0b0011111100111110;
                Move m = new Move(dat);
                generatedMoves.add(m);
            }
            if(CASTLE_OOO && (bb_OOO & (attMask|occupied)) == 0){
                int dat = whiteTurn ? 0b0011000100000010 : 0b0011111100111010;
                Move m = new Move(dat);
                generatedMoves.add(m);
            }
        }
        //System.out.println("Generated " + generatedMoves.size() + " moves.");
        if(generatedMoves.size() == 0){
            //king in check
            if(checkMask != ~0L){
                //System.out.println("Checkmate");
                if(whiteTurn){
                    gameState = State.MATE_BLACK;
                } else {
                    gameState = State.MATE_WHITE;
                }
            } else {
                //System.out.println("Stalemate");
                if(whiteTurn){
                    gameState = State.STALEMATE_BLACK;
                } else {
                    gameState = State.STALEMATE_WHITE;
                }
            }
        }
        return generatedMoves;

    }

    /*
    public ArrayList<Move> generateMoves_DEPRECATED() {
        ArrayList<Move> generatedMoves = new ArrayList<>();
        int relevantKing = (whiteTurn) ? kingSquareW : kingSquareB;
        long attMask = getSquaresAttackedBy(whiteTurn);
        long checkMask = ~0L;
        long rookPin = 0L;
        long bishopPin = 0L;
        //TODO - ENSURE KINGS CANNOT MOVE TO ATTACKED SQUARES - DONE?
        //TODO - PINS - DONE?
        //TODO - EN PASSANT - DONE?
        //TODO - PROPER PAWN PUSHES - DONE?
        //TODO - CASTLING - DONE?
        //this represents the squares that a relevant piece can move to. all squares when not in check
        //blocking squares or capturing squares when in single check vs a sliding piece
        //capturing square when in single check vs a pawn or knight
        //no options to block check or halt check via capture when in double check



        long occupied = whiteBB|blackBB;
        long kingBlockers = BitBoard.queenAttacks(relevantKing,occupied)&occupied;
        long opponent_RQ;
        long opponent_BQ;
        if(whiteTurn){
            opponent_RQ = pieceBBs[Piece.ROOK_B] | pieceBBs[Piece.QUEEN_B];
            opponent_BQ = pieceBBs[Piece.BISHOP_B] | pieceBBs[Piece.QUEEN_B];
        } else {
            opponent_RQ = pieceBBs[Piece.ROOK_W] | pieceBBs[Piece.QUEEN_W];
            opponent_BQ = pieceBBs[Piece.BISHOP_W] | pieceBBs[Piece.QUEEN_W];
        }
        long pinners = BitBoard.xrayRookAttacks(occupied,kingBlockers,relevantKing) & opponent_RQ;
        while(pinners != 0){
            int sq = BitBoard.bitScanForwardDbl(pinners);
            rookPin |= BitBoard.checkMasks.get(BitBoard.squares[sq]|BitBoard.squares[relevantKing]);
            pinners &= pinners - 1;
        }
        pinners = BitBoard.xrayBishopAttacks(occupied,kingBlockers,relevantKing) & opponent_BQ;
        while(pinners != 0){
            int sq = BitBoard.bitScanForwardDbl(pinners);
            bishopPin |= BitBoard.checkMasks.get(BitBoard.squares[sq]|BitBoard.squares[relevantKing]);
            pinners &= pinners - 1;
        }
        long[] mvsArr = new long[64];
        //initialize move tables and prune check mask subtractively
        for(int i = 0; i < 64; i++){
            if(board[i] != Piece.NULL){
                long mvs = getAttacks(i);
                if(board[i] == Piece.PAWN_W){
                    mvs &= blackBB;
                } else if(board[i] == Piece.PAWN_B){
                    mvs &= whiteBB;
                } else if(i == relevantKing){
                    mvs &= ~attMask;
                }
                mvsArr[i] = mvs;
                if((mvs & BitBoard.squares[relevantKing]) != 0) {
                    checkMask &= getCheckMask(i);
                }
            }
        }
        //AND move tables down to moves available during check, then generate Moves from them
        for(int i = 0; i < 64; i++){
            if(board[i] < 6 == whiteTurn){
                if(i != relevantKing) {
                    mvsArr[i] &= checkMask;
                }
                //apply pin logic
                //TODO - actually refactor this to involve bitscanning thru individual piece boards instead of doing some jank shit
                if((BitBoard.squares[i] & (rookPin | bishopPin)) != 0) {
                    if ((board[i] % 6) == Piece.QUEEN_W) {
                        if ((BitBoard.squares[i] & rookPin) != 0) {
                            mvsArr[i] &= (BitBoard.rookAttackMasksPin[i]&rookPin);
                        } else if ((BitBoard.squares[i] & bishopPin) != 0) {
                            mvsArr[i] &= (BitBoard.bishopAttackMasksPin[i]&bishopPin);
                        }
                    } else if ((board[i] % 6) == Piece.ROOK_W) {
                        if ((BitBoard.squares[i] & rookPin) != 0) {
                            mvsArr[i] &= rookPin;
                        } else if ((BitBoard.squares[i] & bishopPin) != 0) {
                            mvsArr[i] = 0L;
                        }
                    } else if ((board[i] % 6) == Piece.BISHOP_W) {
                        if ((BitBoard.squares[i] & rookPin) != 0) {
                            mvsArr[i] = 0L;
                        } else if ((BitBoard.squares[i] & bishopPin) != 0) {
                            mvsArr[i] &= bishopPin;
                        }
                    } else if (board[i] % 6 == Piece.PAWN_W) {
                        if ((BitBoard.squares[i] & rookPin) != 0) {
                            mvsArr[i] = 0L;
                        } else if ((BitBoard.squares[i] & bishopPin) != 0) {
                            mvsArr[i] &= bishopPin;
                        }
                    } else if (board[i] % 6 == Piece.KNIGHT_W) {
                        if ((BitBoard.squares[i] & (rookPin | bishopPin)) != 0) {
                            mvsArr[i] &= 0L;
                        }
                    }
                }
                while(mvsArr[i] != 0){
                    //bitscan through bitboard at given index and create individual destinations from those bit-indices
                    int j = BitBoard.bitScanForwardDbl(mvsArr[i]);
                    //long toFrom = BitBoard.squares[i]|BitBoard.squares[j];
                    Move m = new Move(i, j, 0b0000);
                    if(board[i]%6 == Piece.PAWN_W){
                        if(j/8 == 0 || j/8 == 7){
                            for(int k = 0; k < 4; k++) {
                                m = new Move(i, j, (k<<2)|0b0001);
                                generatedMoves.add(m);
                            }
                            mvsArr[i] &= mvsArr[i] - 1;
                            continue;
                        }
                    }
                    generatedMoves.add(m);
                    mvsArr[i] &= mvsArr[i] - 1;
                }
            }
        }
        //handle pawn pushes
        int pawnType = whiteTurn ? Piece.PAWN_W : Piece.PAWN_B;
        long pushPawns;
        long doublePushPawns;
        long empty = ~(whiteBB | blackBB);
        if(whiteTurn){
            long dblCheckMask = checkMask | (checkMask >>> 8);
            pushPawns = BitBoard.wPawnsAbleToPush(pieceBBs[Piece.PAWN_W],empty&checkMask);
            doublePushPawns = BitBoard.wPawnsAbleToDoublePush(pieceBBs[Piece.PAWN_W],empty&dblCheckMask);
        } else {
            long dblCheckMask = checkMask | (checkMask << 8);
            pushPawns = BitBoard.bPawnsAbleToPush(pieceBBs[Piece.PAWN_B],empty&checkMask);
            doublePushPawns = BitBoard.bPawnsAbleToDoublePush(pieceBBs[Piece.PAWN_B],empty&dblCheckMask);
        }
        int offset = whiteTurn ? 8 : -8;
        while (pushPawns != 0){
            int i = BitBoard.bitScanForwardDbl(pushPawns);
            long toFrom = BitBoard.squares[i]|BitBoard.squares[i+offset];
            if((BitBoard.squares[i] & rookPin) == 0 && (BitBoard.squares[i] & bishopPin) == 0) {
                //if going to back rank they can promote
                if((i+offset)/8 == ((whiteTurn) ? 7 : 0)){
                    for(int j = 0; j < 4; j++){
                        Move m = new Move(i, i + offset, (j<<2)|0b0001);
                        generatedMoves.add(m);
                    }
                } else {
                    Move m = new Move(i, i + offset, 0b0000);
                    generatedMoves.add(m);
                }
            } else if (Long.bitCount(toFrom&rookPin) == 2){
                if((i+offset)/8 == ((whiteTurn) ? 7 : 0)){
                    for(int j = 0; j < 4; j++){
                        Move m = new Move(i, i + offset, (j<<2)|0b0001);
                        generatedMoves.add(m);
                    }
                } else {
                    Move m = new Move(i, i + offset, 0b0000);
                    generatedMoves.add(m);
                }
            }
            pushPawns &= pushPawns - 1;
        }
        while (doublePushPawns != 0){
            int i = BitBoard.bitScanForwardDbl(doublePushPawns);
            long toFrom = (BitBoard.squares[i]|BitBoard.squares[i+offset*2]);
            if((BitBoard.squares[i] & rookPin) == 0 && (BitBoard.squares[i] & bishopPin) == 0) {
                Move m = new Move(i, i + offset*2, 0b0010);
                generatedMoves.add(m);
            } else if (Long.bitCount(toFrom&rookPin) == 2){
                Move m = new Move(i, i + offset*2, 0b0010);
                generatedMoves.add(m);
            }
            doublePushPawns &= doublePushPawns - 1;
        }
        //handle potential en passant captures
        if(enPassantsq != -1){
            int toSq = enPassantsq;
            if(enPassantsq%8 != 0){
                int wSq = whiteTurn ? enPassantsq-9 : enPassantsq+7;
                generateEnPassant(generatedMoves, checkMask, rookPin, bishopPin, pawnType, toSq, wSq);
            }
            if(enPassantsq%8 != 7){
                int eSq = whiteTurn ? enPassantsq-7 : enPassantsq+9;
                generateEnPassant(generatedMoves, checkMask, rookPin, bishopPin, pawnType, toSq, eSq);
            }
        }

        // just wanted to generalize this via ternaries rather than writing so many lines . . .
        boolean CASTLE_OO;
        boolean CASTLE_OOO;
        if(whiteTurn){
            CASTLE_OO = ((castleFlags&Position.OO_W) != 0);
            CASTLE_OOO = ((castleFlags&Position.OOO_W) != 0);
        } else {
            CASTLE_OO = ((castleFlags&Position.OO_B) != 0);
            CASTLE_OOO = ((castleFlags&Position.OOO_B) != 0);
        }
        //king not in check
        if(checkMask == (~0L) && (CASTLE_OO | CASTLE_OOO)){
            long bb_OO = (whiteTurn ? CASTLE_OO_W : CASTLE_OO_B);
            long bb_OOO = (whiteTurn ? CASTLE_OOO_W : CASTLE_OOO_B);
            if(CASTLE_OO && (bb_OO & (attMask|occupied)) == 0){
                int dat = whiteTurn ? 0b0011000100000110 : 0b0011111100111110;
                Move m = new Move(dat);
                generatedMoves.add(m);
            }
            if(CASTLE_OOO && (bb_OOO & (attMask|occupied)) == 0){
                int dat = whiteTurn ? 0b0011000100000010 : 0b0011111100111010;
                Move m = new Move(dat);
                generatedMoves.add(m);
            }
        }
        //System.out.println("Generated " + generatedMoves.size() + " moves.");
        if(generatedMoves.size() == 0){
            //king in check
            if(checkMask != ~0L){
                //System.out.println("Checkmate");
                if(whiteTurn){
                    gameState = State.MATE_BLACK;
                } else {
                    gameState = State.MATE_WHITE;
                }
            } else {
                //System.out.println("Stalemate");
                if(whiteTurn){
                    gameState = State.STALEMATE_BLACK;
                } else {
                    gameState = State.STALEMATE_WHITE;
                }
            }
        }
        return generatedMoves;
    }
    */

    private void generateEnPassant(ArrayList<Move> generatedMoves, long checkMask, long rookPin, long bishopPin, int pawnType, int toSq, int sq) {
        if(board[sq] == pawnType){
            long toFrom = BitBoard.squares[toSq]|BitBoard.squares[sq];
            int bc = (Long.bitCount(toFrom&bishopPin)%2);
            boolean bishopPinState = (bc == 0 || (BitBoard.squares[toSq]|bishopPin) != 0);
            //if not in double check (this should never happen after an opponent double pawn push anyway)
            if(Long.bitCount(checkMask) > 0) {
                checkMask |= BitBoard.squares[enPassantsq];
            }
            //BitBoard.printBoard(checkMask);
            if(((BitBoard.squares[toSq]&checkMask) != 0)&&bishopPinState && (BitBoard.squares[sq] & rookPin) == 0) {
                Move m = new Move(sq, toSq, 0b0110);
                generatedMoves.add(m);
            }
        }
    }

    public void makeMove(Move M){
        halfTurnClock++;
        int newEnPassantSq = -1;
        int to = M.moveData&0x3F;
        int from = (M.moveData>>>6)&0x3F;
        int flag = (M.moveData>>>12)&0xF;
        long toBB = BitBoard.squares[to];
        long fromBB =BitBoard.squares[from];
        int toPiece = board[to];
        int fromPiece = board[from];
        //normal move
        if(flag == 0b0000 || flag == 0b0001) {
            //alter surface level mailbox representation
            board[from] = Piece.NULL;
            board[to] = fromPiece;
            if(fromPiece%6 == Piece.PAWN_W){
                halfTurnClock = 0;
            }
            //set castling flags. doesn't matter if they get set repetitively
            if(fromPiece == Piece.KING_W){
                castleFlags &= ~Position.OO_W;
                castleFlags &= ~Position.OOO_W;
                kingSquareW = to;
            }
            else if(fromPiece == Piece.KING_B){
                castleFlags &= ~Position.OO_B;
                castleFlags &= ~Position.OOO_B;
                kingSquareB = to;
            }
            else if (fromPiece == Piece.ROOK_W){
                if(from == 0){
                    castleFlags &= ~Position.OOO_W;
                }
                else if (from == 7) {
                    castleFlags &= ~Position.OO_W;
                }
            }
            else if (fromPiece == Piece.ROOK_B){
                if(from == 56){
                    castleFlags &= ~Position.OOO_B;
                }
                else if (from == 63) {
                    castleFlags &= ~Position.OO_B;
                }
            }
            //alter bitboard for moving piece
            pieceBBs[fromPiece] ^= fromBB;
            pieceBBs[fromPiece] ^= toBB;
            if (whiteTurn) {
                //ditto for occupancy bitboard for given color
                whiteBB ^= fromBB;
                whiteBB ^= toBB;
                //if attacking a piece properly clear it from its occupancy bitboard and piece bitboard
                if (toPiece != Piece.NULL) {
                    halfTurnClock = 0;
                    blackBB ^= toBB;
                    pieceBBs[toPiece] ^= toBB;
                }
            } else {
                //same as above but with reversed colors
                blackBB ^= fromBB;
                blackBB ^= toBB;
                if (toPiece != Piece.NULL) {
                    halfTurnClock = 0;
                    whiteBB ^= toBB;
                    pieceBBs[toPiece] ^= toBB;
                }
            }
        }
        // Castling
        else if (flag == 0b0011){

            int rookFrom = -1;
            int rookTo = -1;
            //W_OO
            if(to == 6){
                rookFrom = 7;
                rookTo = 5;
                //W_OOO
            }
            else if (to == 2){
                rookFrom = 0;
                rookTo = 3;
                //B_OO
            }
            else if (to == 62){
                rookFrom = 63;
                rookTo = 61;
                //B_OOO
            }
            else if (to == 58){
                rookFrom = 56;
                rookTo = 59;
            }
            long kingShift = BitBoard.squares[from] | BitBoard.squares[to];
            long rookShift = BitBoard.squares[rookFrom] | BitBoard.squares[rookTo];



            board[from] = Piece.NULL;
            board[rookFrom] = Piece.NULL;

            if(whiteTurn){
                kingSquareW = to;
                board[to] = Piece.KING_W;
                board[rookTo] = Piece.ROOK_W;
                pieceBBs[Piece.KING_W] ^= kingShift;
                pieceBBs[Piece.ROOK_W] ^= rookShift;
                whiteBB ^= (kingShift|rookShift);
            } else {
                kingSquareB = to;
                board[to] = Piece.KING_B;
                board[rookTo] = Piece.ROOK_B;
                pieceBBs[Piece.KING_B] ^= kingShift;
                pieceBBs[Piece.ROOK_B] ^= rookShift;
                blackBB ^= (kingShift|rookShift);
            }
        }
        // En passant SET
        else if (flag == 0b0010){
            halfTurnClock = 0;
            board[from] = Piece.NULL;
            board[to] = fromPiece;
            pieceBBs[fromPiece] ^= fromBB;
            pieceBBs[fromPiece] ^= toBB;
            if (whiteTurn) {
                whiteBB ^= fromBB;
                whiteBB ^= toBB;
                newEnPassantSq = to - 8;
            } else {
                blackBB ^= fromBB;
                blackBB ^= toBB;
                newEnPassantSq = to + 8;
            }
        }
        // En passant CAPTURE
        else if (flag == 0b0110){
            halfTurnClock = 0;
            board[from] = Piece.NULL;
            board[to] = fromPiece;
            pieceBBs[fromPiece] ^= fromBB;
            pieceBBs[fromPiece] ^= toBB;
            if (whiteTurn) {
                whiteBB ^= fromBB;
                whiteBB ^= toBB;
                blackBB ^= toBB>>>8;
                pieceBBs[Piece.PAWN_B] ^= toBB>>>8;
                board[to - 8] = Piece.NULL;
            } else {
                blackBB ^= fromBB;
                blackBB ^= toBB;
                whiteBB ^= toBB<<8;
                pieceBBs[Piece.PAWN_W] ^= toBB<<8;
                board[to + 8] = Piece.NULL;
            }
        }
        //PROMOTION
        if ((flag&0b0011) == 0b0001){

            int promoteInto = (flag>>>2)&3;

            if(promoteInto == 0){
                if(whiteTurn) {
                    pieceBBs[Piece.QUEEN_W] ^= toBB;
                    pieceBBs[Piece.PAWN_W] ^= toBB;
                    board[to] = Piece.QUEEN_W;
                } else {
                    pieceBBs[Piece.QUEEN_B] ^= toBB;
                    pieceBBs[Piece.PAWN_B] ^= toBB;
                    board[to] = Piece.QUEEN_B;
                }
            } else if(promoteInto == 1){
                if(whiteTurn) {
                    pieceBBs[Piece.ROOK_W] ^= toBB;
                    pieceBBs[Piece.PAWN_W] ^= toBB;
                    board[to] = Piece.QUEEN_W;
                } else {
                    pieceBBs[Piece.ROOK_B] ^= toBB;
                    pieceBBs[Piece.PAWN_B] ^= toBB;
                    board[to] = Piece.QUEEN_B;
                }
            } else if(promoteInto == 2){
                if(whiteTurn) {
                    pieceBBs[Piece.BISHOP_W] ^= toBB;
                    pieceBBs[Piece.PAWN_W] ^= toBB;
                    board[to] = Piece.QUEEN_W;
                } else {
                    pieceBBs[Piece.BISHOP_B] ^= toBB;
                    pieceBBs[Piece.PAWN_B] ^= toBB;
                    board[to] = Piece.QUEEN_B;
                }
            } else {
                if(whiteTurn) {
                    pieceBBs[Piece.KNIGHT_W] ^= toBB;
                    pieceBBs[Piece.PAWN_W] ^= toBB;
                    board[to] = Piece.QUEEN_W;
                } else {
                    pieceBBs[Piece.KNIGHT_B] ^= toBB;
                    pieceBBs[Piece.PAWN_B] ^= toBB;
                    board[to] = Piece.QUEEN_B;
                }
            }
        }
        enPassantsq = newEnPassantSq;
        whiteTurn = !whiteTurn;
        if(whiteTurn){
            turn++;
        }
    }
}
