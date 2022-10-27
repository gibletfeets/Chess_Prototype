package ChessEngine;

public class Move {
    public int moveData;
    //0bXX FF OOOOOO DDDDDD
    //D - Bits for destination square (0-63)
    //O - Bits for origin square (0-63)
    //F - Bits for flag for special move
    //00 - None, 01 - Promotion, 10 - En Passant, 11 - Castling
    //X - Bits for piece to promote into/Bits determining if performing En Passant or setting the En Passant square
    //Promotion: 00 - Queen, 01 - Rook, 10 - Bishop, 11 - Knight
    //En Passant: 00 - Set, 01 - Perform

    public Move(int from, int to, int flag){
        moveData = ((flag << 12)|(from << 6)|(to));
    }
    public Move(int _moveData){
        moveData = _moveData;
    }
    public String moveToStr(){
        String fileStr = "abcdefgh";
        String rankStr = "12345678";
        String str = "";
        int to = moveData & 0x3F;
        int from = (moveData & (0x3F << 6)) >>> 6;
        str += fileStr.charAt(from%8);
        str += rankStr.charAt(from/8);
        str += fileStr.charAt(to%8);
        str += rankStr.charAt(to/8);
        return str;
    }
    public void printMove(){
        int to = moveData&0x3F;
        int from = (moveData>>>6)&0x3F;
        System.out.println("    a  b  c  d  e  f  g  h");
        System.out.println("  --------------------------");
        for (int i = 7; i >= 0; i--) {
            System.out.print(Character.toString('1' + i) + " |");
            for (int j = 0; j < 8; j++) {
                if ((8*(i)+j) == from)
                    System.out.print(" F ");
                else if ((8*(i)+j) == to)
                    System.out.print(" T ");
                else
                    System.out.print(" . ");
            }
            System.out.print("| " + Character.toString('1' + i));
            System.out.print('\n');
        }
        System.out.println("  --------------------------");
        System.out.println("    a  b  c  d  e  f  g  h");

        System.out.print('\n');
    }
}
