package ChessEngine;
import java.util.ArrayList;
import java.util.HashMap;
public class BitBoard {
    public static final HashMap<Long,Long> checkMasks;
    public static final long[] wPawnAttacks;
    public static final long[] bPawnAttacks;
    public static final long[] kingAttacks;
    public static final long[] knightAttacks;
    public static final long[] ranks = {
            0x00000000000000FFL,
            0x000000000000FF00L,
            0x0000000000FF0000L,
            0x00000000FF000000L,
            0x000000FF00000000L,
            0x0000FF0000000000L,
            0x00FF000000000000L,
            0xFF00000000000000L
    };
    public static final long[] files = {
            0x0101010101010101L,
            0x0202020202020202L,
            0x0404040404040404L,
            0x0808080808080808L,
            0x1010101010101010L,
            0x2020202020202020L,
            0x4040404040404040L,
            0x8080808080808080L
    };
    public static final long[] squares = {
            0x0000000000000001L, 0x0000000000000002L, 0x0000000000000004L, 0x0000000000000008L, 0x0000000000000010L, 0x0000000000000020L, 0x0000000000000040L, 0x0000000000000080L,
            0x0000000000000100L, 0x0000000000000200L, 0x0000000000000400L, 0x0000000000000800L, 0x0000000000001000L, 0x0000000000002000L, 0x0000000000004000L, 0x0000000000008000L,
            0x0000000000010000L, 0x0000000000020000L, 0x0000000000040000L, 0x0000000000080000L, 0x0000000000100000L, 0x0000000000200000L, 0x0000000000400000L, 0x0000000000800000L,
            0x0000000001000000L, 0x0000000002000000L, 0x0000000004000000L, 0x0000000008000000L, 0x0000000010000000L, 0x0000000020000000L, 0x0000000040000000L, 0x0000000080000000L,
            0x0000000100000000L, 0x0000000200000000L, 0x0000000400000000L, 0x0000000800000000L, 0x0000001000000000L, 0x0000002000000000L, 0x0000004000000000L, 0x0000008000000000L,
            0x0000010000000000L, 0x0000020000000000L, 0x0000040000000000L, 0x0000080000000000L, 0x0000100000000000L, 0x0000200000000000L, 0x0000400000000000L, 0x0000800000000000L,
            0x0001000000000000L, 0x0002000000000000L, 0x0004000000000000L, 0x0008000000000000L, 0x0010000000000000L, 0x0020000000000000L, 0x0040000000000000L, 0x0080000000000000L,
            0x0100000000000000L, 0x0200000000000000L, 0x0400000000000000L, 0x0800000000000000L, 0x1000000000000000L, 0x2000000000000000L, 0x4000000000000000L, 0x8000000000000000L
    };
    //lookup table to be populated, is keyed as [square][magicKey]
    private static final long[][] rookTables;

    //relevant amount of occupancy bits for magic key generation
    private static final int[] rookBits = {
            12, 11, 11, 11, 11, 11, 11, 12,
            11, 10, 10, 11, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 11, 10, 10, 10, 11,
            10,  9,  9,  9,  9,  9, 10, 10,
            11, 10, 10, 10, 10, 11, 10, 11 };
    //magic bitboards, used for generating magic keys by multiplying with relevant occupancy bitmask
    //and then bit shifting the top n bits out, with n being the rookBits for the corresponding square
    private static final long[] rookMagics = {
            0x19a80065ff2bffffL, 0x3fd80075ffebffffL, 0x4010000df6f6fffeL, 0x0050001faffaffffL, 0x0050028004ffffb0L, 0x7f600280089ffff1L, 0x7f5000b0029ffffcL, 0x5b58004848a7fffaL,
            0x002a90005547ffffL, 0x000050007f13ffffL, 0x007fa0006013ffffL, 0x006a9005656fffffL, 0x007f600f600affffL, 0x007ec007e6bfffe2L, 0x007ec003eebffffbL, 0x0071d002382fffdaL,
            0x009f803000e7fffaL, 0x00680030008bffffL, 0x00606060004f3ffcL, 0x001a00600bff9ffdL, 0x000d006005ff9fffL, 0x0001806003005fffL, 0x00000300040bfffaL, 0x000192500065ffeaL,
            0x00fff112d0006800L, 0x007ff037d000c004L, 0x003fd062001a3ff8L, 0x00087000600e1ffcL, 0x000fff0100100804L, 0x0007ff0100080402L, 0x0003ffe0c0060003L, 0x0001ffd53000d300L,
            0x00fffd3000600061L, 0x007fff7f95900040L, 0x003fff8c00600060L, 0x001ffe2587a01860L, 0x000fff3fbf40180cL, 0x0007ffc73f400c06L, 0x0003ff86d2c01405L, 0x0001fffeaa700100L,
            0x00fffdfdd8005000L, 0x007fff80ebffb000L, 0x003fffdf603f6000L, 0x001fffe050405000L, 0x000fff400700c00cL, 0x0007ff6007bf600aL, 0x0003ffeebffec005L, 0x0001fffdf3feb001L,
            0x00ffff39ff484a00L, 0x007fff3fff486300L, 0x003fff99ffac2e00L, 0x001fff31ff2a6a00L, 0x000fff19ff15b600L, 0x0007fff5fff28600L, 0x0003fffddffbfee0L, 0x0001fff5f63c96a0L,
            0x00ffff5dff65cfb6L, 0x007fffbaffd1c5aeL, 0x003fff71ff6cbceaL, 0x001fffd9ffd4756eL, 0x000ffff5fff338e6L, 0x0007fffdfffe24f6L, 0x0003ffef27eebe74L, 0x0001ffff23ff605eL
    };
    public static final long[] rookAttackMasks = {
            0x000101010101017EL, 0x000202020202027CL, 0x000404040404047AL, 0x0008080808080876L, 0x001010101010106EL, 0x002020202020205EL, 0x004040404040403EL, 0x008080808080807EL,
            0x0001010101017E00L, 0x0002020202027C00L, 0x0004040404047A00L, 0x0008080808087600L, 0x0010101010106E00L, 0x0020202020205E00L, 0x0040404040403E00L, 0x0080808080807E00L,
            0x00010101017E0100L, 0x00020202027C0200L, 0x00040404047A0400L, 0x0008080808760800L, 0x00101010106E1000L, 0x00202020205E2000L, 0x00404040403E4000L, 0x00808080807E8000L,
            0x000101017E010100L, 0x000202027C020200L, 0x000404047A040400L, 0x0008080876080800L, 0x001010106E101000L, 0x002020205E202000L, 0x004040403E404000L, 0x008080807E808000L,
            0x0001017E01010100L, 0x0002027C02020200L, 0x0004047A04040400L, 0x0008087608080800L, 0x0010106E10101000L, 0x0020205E20202000L, 0x0040403E40404000L, 0x0080807E80808000L,
            0x00017E0101010100L, 0x00027C0202020200L, 0x00047A0404040400L, 0x0008760808080800L, 0x00106E1010101000L, 0x00205E2020202000L, 0x00403E4040404000L, 0x00807E8080808000L,
            0x007E010101010100L, 0x007C020202020200L, 0x007A040404040400L, 0x0076080808080800L, 0x006E101010101000L, 0x005E202020202000L, 0x003E404040404000L, 0x007E808080808000L,
            0x7E01010101010100L, 0x7C02020202020200L, 0x7A04040404040400L, 0x7608080808080800L, 0x6E10101010101000L, 0x5E20202020202000L, 0x3E40404040404000L, 0x7E80808080808000L,
    };
    public static final long[] rookAttackMasksPin = {
            0x01010101010101ffL, 0x02020202020202ffL, 0x04040404040404ffL, 0x08080808080808ffL, 0x10101010101010ffL, 0x20202020202020ffL, 0x40404040404040ffL, 0x80808080808080ffL,
            0x010101010101ff01L, 0x020202020202ff02L, 0x040404040404ff04L, 0x080808080808ff08L, 0x101010101010ff10L, 0x202020202020ff20L, 0x404040404040ff40L, 0x808080808080ff80L,
            0x0101010101ff0101L, 0x0202020202ff0202L, 0x0404040404ff0404L, 0x0808080808ff0808L, 0x1010101010ff1010L, 0x2020202020ff2020L, 0x4040404040ff4040L, 0x8080808080ff8080L,
            0x01010101ff010101L, 0x02020202ff020202L, 0x04040404ff040404L, 0x08080808ff080808L, 0x10101010ff101010L, 0x20202020ff202020L, 0x40404040ff404040L, 0x80808080ff808080L,
            0x010101ff01010101L, 0x020202ff02020202L, 0x040404ff04040404L, 0x080808ff08080808L, 0x101010ff10101010L, 0x202020ff20202020L, 0x404040ff40404040L, 0x808080ff80808080L,
            0x0101ff0101010101L, 0x0202ff0202020202L, 0x0404ff0404040404L, 0x0808ff0808080808L, 0x1010ff1010101010L, 0x2020ff2020202020L, 0x4040ff4040404040L, 0x8080ff8080808080L,
            0x01ff010101010101L, 0x02ff020202020202L, 0x04ff040404040404L, 0x08ff080808080808L, 0x10ff101010101010L, 0x20ff202020202020L, 0x40ff404040404040L, 0x80ff808080808080L,
            0xff01010101010101L, 0xff02020202020202L, 0xff04040404040404L, 0xff08080808080808L, 0xff10101010101010L, 0xff20202020202020L, 0xff40404040404040L, 0xff80808080808080L
    };
    //this is all performs the same roles as the previous rook masks/tables
    private static final long[][] bishopTables;
    private static final int[] bishopBits = {
            5, 4, 5, 5, 5, 5, 4, 5,
            4, 4, 5, 5, 5, 5, 4, 4,
            4, 4, 7, 7, 7, 7, 4, 4,
            5, 5, 7, 9, 9, 7, 5, 5,
            5, 5, 7, 9, 9, 7, 5, 5,
            4, 4, 7, 7, 7, 7, 4, 4,
            4, 4, 5, 5, 5, 5, 4, 4,
            5, 4, 5, 5, 5, 5, 4, 5 };
    private static final long[] bishopMagics = {
            0x0006eff5367ff600L, 0x00345835ba77ff2bL, 0x00145f68a3f5dab6L, 0x003a1863fb56f21dL, 0x0012eb6bfe9d93cdL, 0x000d82827f3420d6L, 0x00074bcd9c7fec97L, 0x000034fe99f9ffffL,
            0x0000746f8d6717f6L, 0x00003acb32e1a3f7L, 0x0000185daf1ffb8aL, 0x00003a1867f17067L, 0x0000038ee0ccf92eL, 0x000002a2b7ff926eL, 0x000006c9aa93ff14L, 0x00000399b5e5bf87L,
            0x00400f342c951ffcL, 0x0020230579ed8ff0L, 0x007b008a0077dbfdL, 0x001d00010c13fd46L, 0x00040022031c1ffbL, 0x000fa00fd1cbff79L, 0x000400a4bc9affdfL, 0x000200085e9cffdaL,
            0x002a14560a3dbfbdL, 0x000a0a157b9eafd1L, 0x00060600fd002ffaL, 0x004006000c009010L, 0x001a002042008040L, 0x001a00600fd1ffc0L, 0x000d0ace50bf3f8dL, 0x000183a48434efd1L,
            0x001fbd7670982a0dL, 0x000fe24301d81a0fL, 0x0007fbf82f040041L, 0x000040c800008200L, 0x007fe17018086006L, 0x003b7ddf0ffe1effL, 0x001f92f861df4a0aL, 0x000fd713ad98a289L,
            0x000fd6aa751e400cL, 0x0007f2a63ae9600cL, 0x0003ff7dfe0e3f00L, 0x000003fd2704ce04L, 0x00007fc421601d40L, 0x007fff5f70900120L, 0x003fa66283556403L, 0x001fe31969aec201L,
            0x0007fdfc18ac14bbL, 0x0003fb96fb568a47L, 0x000003f72ea4954dL, 0x00000003f8dc0383L, 0x0000007f3a814490L, 0x00007dc5c9cf62a6L, 0x007f23d3342897acL, 0x003fee36eee1565cL,
            0x0003ff3e99fcccc7L, 0x000003ecfcfac5feL, 0x00000003f97f7453L, 0x0000000003f8dc03L, 0x000000007efa8146L, 0x0000007ed3e2ef60L, 0x00007f47243adcd6L, 0x007fb65afabfb3b5L
    };
    public static final long[] bishopAttackMasks = {
            0x0040201008040200L, 0x0000402010080400L, 0x0000004020100A00L, 0x0000000040221400L, 0x0000000002442800L, 0x0000000204085000L, 0x0000020408102000L, 0x0002040810204000L,
            0x0020100804020000L, 0x0040201008040000L, 0x00004020100A0000L, 0x0000004022140000L, 0x0000000244280000L, 0x0000020408500000L, 0x0002040810200000L, 0x0004081020400000L,
            0x0010080402000200L, 0x0020100804000400L, 0x004020100A000A00L, 0x0000402214001400L, 0x0000024428002800L, 0x0002040850005000L, 0x0004081020002000L, 0x0008102040004000L,
            0x0008040200020400L, 0x0010080400040800L, 0x0020100A000A1000L, 0x0040221400142200L, 0x0002442800284400L, 0x0004085000500800L, 0x0008102000201000L, 0x0010204000402000L,
            0x0004020002040800L, 0x0008040004081000L, 0x00100A000A102000L, 0x0022140014224000L, 0x0044280028440200L, 0x0008500050080400L, 0x0010200020100800L, 0x0020400040201000L,
            0x0002000204081000L, 0x0004000408102000L, 0x000A000A10204000L, 0x0014001422400000L, 0x0028002844020000L, 0x0050005008040200L, 0x0020002010080400L, 0x0040004020100800L,
            0x0000020408102000L, 0x0000040810204000L, 0x00000A1020400000L, 0x0000142240000000L, 0x0000284402000000L, 0x0000500804020000L, 0x0000201008040200L, 0x0000402010080400L,
            0x0002040810204000L, 0x0004081020400000L, 0x000A102040000000L, 0x0014224000000000L, 0x0028440200000000L, 0x0050080402000000L, 0x0020100804020000L, 0x0040201008040200L
    };
    public static final long[] bishopAttackMasksPin = {
            0x8040201008040201L, 0x0080402010080502L, 0x0000804020110a04L, 0x0000008041221408L, 0x0000000182442810L, 0x0000010204885020L, 0x000102040810a040L, 0x0102040810204080L,
            0x4020100804020102L, 0x8040201008050205L, 0x00804020110a040aL, 0x0000804122140814L, 0x0000018244281028L, 0x0001020488502050L, 0x0102040810a040a0L, 0x0204081020408040L,
            0x2010080402010204L, 0x4020100805020508L, 0x804020110a040a11L, 0x0080412214081422L, 0x0001824428102844L, 0x0102048850205088L, 0x02040810a040a010L, 0x0408102040804020L,
            0x1008040201020408L, 0x2010080502050810L, 0x4020110a040a1120L, 0x8041221408142241L, 0x0182442810284482L, 0x0204885020508804L, 0x040810a040a01008L, 0x0810204080402010L,
            0x0804020102040810L, 0x1008050205081020L, 0x20110a040a112040L, 0x4122140814224180L, 0x8244281028448201L, 0x0488502050880402L, 0x0810a040a0100804L, 0x1020408040201008L,
            0x0402010204081020L, 0x0805020508102040L, 0x110a040a11204080L, 0x2214081422418000L, 0x4428102844820100L, 0x8850205088040201L, 0x10a040a010080402L, 0x2040804020100804L,
            0x0201020408102040L, 0x0502050810204080L, 0x0a040a1120408000L, 0x1408142241800000L, 0x2810284482010000L, 0x5020508804020100L, 0xa040a01008040201L, 0x4080402010080402L,
            0x0102040810204080L, 0x0205081020408000L, 0x040a112040800000L, 0x0814224180000000L, 0x1028448201000000L, 0x2050880402010000L, 0x40a0100804020100L, 0x8040201008040201L
    };
    private static long createMask(int i, long mask){
        long outputMask = 0L;
        int c = 0;
        while(true){
            //isolate the least significant bit
            long nextMask = mask & (mask -1);
            //deactivate the least significant bit and store the result into bit
            long bit = mask ^ nextMask;
            //shift hackery to finalize converting the attack mask into an appropriate bitmask
            if((i & (1L << c)) != 0)
                outputMask |= bit;
            mask = nextMask;
            if(mask == 0)
                break;
            c++;
        }
        return outputMask;
    }
    private static long checkDirsRook(int sq, long occupied){
        long outputMask = 0L;
        long bit = squares[sq];

        // up & down
        do {
            bit = bit << 8;
            outputMask |= bit;
        } while (bit > 0 && (bit & occupied) == 0);
        bit = squares[sq];
        do {
            bit = bit >>> 8;
            outputMask |= bit;
        } while (bit > 0 && (bit & occupied) == 0);
        bit = squares[sq];

        //mask to make sure bits are on the appropriate row post-shift
        final long row = ranks[sq/8];
        //left & right
        do {
            bit = bit << 1;
            if((bit & row) != 0)
                outputMask |= bit;
            else
                break;
        } while ((bit & occupied) == 0);
        bit = squares[sq];
        //right
        do {
            bit = bit >>> 1;
            if((bit & row) != 0)
                outputMask |= bit;
            else
                break;
        } while ((bit & occupied) == 0);
        return outputMask;
    }
    private static long checkDirsBishop(int sq, long occupied){
        //functionally very similar to the rook function, except its also now continually trying to make sure that horizontal
        //wrapping doesn't occur in all 4 directions it's checking, since all 4 directions have a horizontal component.
        long outputMask = 0L;
        final long row = ranks[sq/8];
        long bit = squares[sq];
        long bit2 = bit;
        do {
            bit <<= 7;
            bit2 >>>= 1;
            if ((bit2 & row) != 0)
                outputMask |= bit;
            else
                break;
        }
        while (bit != 0 && (bit & occupied) == 0);
        bit = squares[sq];
        bit2 = bit;
        do {
            bit <<= 9;
            bit2 <<= 1;
            if ((bit2 & row) != 0)
                outputMask |= bit;
            else
                break;
        }
        while (bit != 0 && (bit & occupied) == 0);

        bit = squares[sq];
        bit2 = bit;
        do {
            bit >>>= 7;
            bit2 <<= 1;
            if ((bit2 & row) != 0)
                outputMask |= bit;
            else
                break;
        }
        while (bit != 0 && (bit & occupied) == 0);

        bit = squares[sq];
        bit2 = bit;
        do {
            bit >>>= 9;
            bit2 >>= 1;
            if ((bit2 & row) != 0)
                outputMask |= bit;
            else
                break;
        }
        while (bit > 0 && (bit & occupied) == 0);
        return outputMask;
    }
    public static long rookAttacks(int sq, long occupied) {
        return rookTables[sq][(int)(((occupied & rookAttackMasks[sq]) * rookMagics[sq]) >>> (64 - rookBits[sq]))];
    }
    public static long bishopAttacks(int sq, long occupied) {
        return bishopTables[sq][(int)(((occupied & bishopAttackMasks[sq]) * bishopMagics[sq]) >>> (64 - bishopBits[sq]))];
    }
    public static long queenAttacks(int sq, long occupied) {
        return rookAttacks(sq,occupied) | bishopAttacks(sq,occupied);
    }
    public static long xrayRookAttacks(long occ, long blockers, int sq){
        long attacks = rookAttacks(sq,occ);
        blockers &= attacks;
        return attacks ^ rookAttacks(sq,occ^blockers);
    }
    public static long xrayBishopAttacks(long occ, long blockers, int sq){
        long attacks = bishopAttacks(sq,occ);
        blockers &= attacks;
        return attacks ^ bishopAttacks(sq,occ^blockers);
    }
    public static long wPawnsAbleToPush(long wPawns, long emptySquares){
        return (emptySquares>>>8) & wPawns;
    }
    public static long wPawnsAbleToDoublePush(long wPawns, long emptySquares){
        long emptyRank3 = ((emptySquares&ranks[3])>>>8) & emptySquares;
        return wPawnsAbleToPush(wPawns,emptyRank3);
    }
    public static long bPawnsAbleToPush(long bPawns, long emptySquares){
        return (emptySquares<<8) & bPawns;
    }
    public static long bPawnsAbleToDoublePush(long bPawns, long emptySquares){
        long emptyRank5 = ((emptySquares&ranks[4])<<8) & emptySquares;
        return bPawnsAbleToPush(bPawns,emptyRank5);
    }
    public static void initCheckMasks(){
        for(int i = 0; i < 64; i++) {
            for (int j = i + 1; j < 64; j++) {
                int x0 = i % 8;
                int y0 = 7 - (i / 8);
                int x1 = j % 8;
                int y1 = 7 - (j / 8);
                long bb_IN = (squares[i]) | (squares[j]);
                long bb_OUT = 0L;
                if (y0 == y1) {
                    for (int k = x0; k <= x1; k++) {
                        bb_OUT |= squares[(k + 8 * (7 - y0))];
                    }
                    checkMasks.put(bb_IN, bb_OUT);
                } else if (x0 == x1) {
                    for (int k = y1; k <= y0; k++) {
                        bb_OUT |= squares[(x0 + 8 * (7 - k))];
                    }
                    checkMasks.put(bb_IN, bb_OUT);
                } else if (Math.abs(x0 - x1) == Math.abs(y0 - y1)) {
                    int dx = (x0 - x1) > 0 ? -1 : 1;
                    int dy = (y0 - y1) > 0 ? -1 : 1;
                    for (int k = 0; k <= Math.abs(x0 - x1); k++) {
                        bb_OUT |= squares[(x0 + k * dx + 8 * (7 - (y0 + k * dy)))];
                    }
                    checkMasks.put(bb_IN, bb_OUT);
                }
            }
        }
    }
    static{
        //initializing tables
        checkMasks = new HashMap<>();
        initCheckMasks();
        wPawnAttacks = new long[64];
        bPawnAttacks = new long[64];
        kingAttacks = new long[64];
        knightAttacks = new long[64];
        rookTables = new long[64][];
        bishopTables = new long[64][];
        int rookC = 0;
        int bishopC = 0;
        for (int sq = 0; sq < 64; sq++){
            long p = squares[sq];
            long att;
            // -- pawn attack table computation --
            att = ((p<<9) & ~files[0]) | ((p<<7) & ~files[7]);
            wPawnAttacks[sq] = att;
            att = ((p>>>7) & ~files[0]) | ((p>>>9) & ~files[7]);
            bPawnAttacks[sq] = att;
            // -- king attack table computation --
            // up and down
            att = (p << 8) | (p >>> 8);
            // right attacks
            att |= ((p << 1) | (p << 9) | (p >>> 7)) & ~files[0];
            // left attacks
            att |= ((p >>> 1) | (p >>> 9) | (p << 7)) & ~files[7];
            kingAttacks[sq] = att;

            // -- knight attack table computation --
            // right attacks
            att = (((p << 17) | (p >>> 15)) & ~files[0]) | (((p >>> 6) | (p << 10)) & ~(files[0] | files[1]));
            // left attacks
            att |= (((p >>> 17) | (p << 15)) & ~files[7]) | (((p << 6) | (p >>> 10)) & ~(files[6] | files[7]));
            knightAttacks[sq] = att;

            //vars that get used in both rook and bishop attack table computation
            int tableSize;
            long[] table;
            int numMasks;
            // -- rook attack table computation --
            //generate table with a size appropriate for how many rookBits correspond to the square
            tableSize = 1 << rookBits[sq];
            table = new long[tableSize];
            //initialize table values to a default value
            for(int i = 0; i < tableSize; i++) table[i] = -1;
            //get the number of relevant occupancy bits from the rook attack mask for the corresponding square,
            //which directly corresponds to the amount of unique rook attack masks to be computed based on
            //the pieces possibly blocking the rook's movement
            numMasks = 1 << Long.bitCount(rookAttackMasks[sq]);
            for(int i = 0; i < numMasks; i++){
                long m = createMask(i, rookAttackMasks[sq]);
                int entry = (int)((m * rookMagics[sq]) >>> (64-rookBits[sq]));
                long attacks = checkDirsRook(sq,m);
                if(table[entry] == -1) {
                    rookC++;
                    table[entry] = attacks;
                }
                else if(table[entry] != attacks) {
                    //this occurs if the table was already populated at the generated magic index and it's a conflicting table
                    //this should not happen.
                    System.out.println("Square: " + sq + ", Entry: " + entry + ", Cycle: " + i);
                    printBoard(m);
                    printBoard(attacks);
                    printBoard(table[entry]);
                    throw new RuntimeException();
                }
            }
            rookTables[sq] = table;

            // -- bishop attack computation --
            tableSize = 1 << bishopBits[sq];
            table = new long[tableSize];
            for (int i = 0; i < tableSize; i++) table[i] = -1;
            numMasks = 1 << Long.bitCount(bishopAttackMasks[sq]);
            for (int i = 0; i < numMasks; i++) {
                long m = createMask(i, bishopAttackMasks[sq]);
                int entry = (int)((m * bishopMagics[sq]) >>> (64 - bishopBits[sq]));
                long attacks = checkDirsBishop(sq, m);
                if (table[entry] == -1) {
                    bishopC++;
                    table[entry] = attacks;
                } else if (table[entry] != attacks) {
                    //this also should not happen. both cases are still kept in case i decide to change my magic bitboards.
                    throw new RuntimeException();
                }
            }
            bishopTables[sq] = table;
        }
        System.out.println("Generated " + rookC + " Rook tables");
        System.out.println("Generated " + bishopC + " Bishop tables");
    }

    public static void printBoard(long bb) {
        System.out.println("    a  b  c  d  e  f  g  h");
        System.out.println("  --------------------------");
        for (int i = 7; i >= 0; i--) {
            System.out.print(Character.toString('1' + i) + " |");
            for (int j = 0; j < 8; j++) {
                if ((bb >>> ((i * 8 + j)) & 1) == 1)
                    System.out.print(" 1 ");
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
    /**
     * @author Gerd Isenberg
     * @return index 0..63 of LS1B -1023 if passing zero
     * @param b a 64-bit word to bitscan
     */
    //Sourced from https://www.chessprogramming.org/Java-Bitscan
    static public int bitScanForwardDbl(long b)
    {
        double x = (double)(b & - b);
        int exp = (int) (Double.doubleToLongBits(x) >>> 52);
        return (exp & 2047) - 1023;
    }
}
