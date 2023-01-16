package mnkgame;

import java.util.Map;
import java.util.HashMap;
import java.util.Random;

public class TransTable {
    private int N, M;

    private Map<Long, DataHash> tt;                            // Tabella Hash
    private long[][] XplayerVal, OplayerVal;                   // matrice principale con i valori casuali
    private long[][] Rt_XplayerVal, Rt_OplayerVal;             // matrice 
    private long Zobby, spec_Zobby, bot_Zobby, bot_Spec_Zobby; //keys
    private long rtZobby, rtspec_Zobby, rtbot_Zobby, rtbot_Spec_Zobby;

    public TransTable(int M, int N) {
        this.M = M;
        this.N = N;
        this.tt = new HashMap<Long, DataHash>();
        XplayerVal = new long[M][N]; OplayerVal = new long[M][N];
        Rt_XplayerVal = new long[M][N]; Rt_OplayerVal = new long[M][N];
        Zobby = 0; spec_Zobby = 0; bot_Zobby = 0; bot_Spec_Zobby = 0;
        rtZobby = 0; rtspec_Zobby = 0; rtbot_Zobby = 0; rtbot_Spec_Zobby = 0;

        // Riempimento della matrice principale
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                XplayerVal[i][j] = Math.abs(new Random().nextLong());
                OplayerVal[i][j] = Math.abs(new Random().nextLong());
            }
        }
        // Matrice ruotata
        if (M == N) {
            for (int i = 0; i < M; i++) {
                for (int j = 0; j < N; j++) {
                    Rt_XplayerVal[j][i] = XplayerVal[i][j];
                    Rt_OplayerVal[j][i] = OplayerVal[i][j];
                }
            }
            for (int i = 0; i < M; i++) {
                int start = 0, finish = M - 1;
                while (start < finish) {
                    long tmp = Rt_XplayerVal[i][start];
                    Rt_XplayerVal[i][start] = Rt_XplayerVal[i][finish];
                    Rt_XplayerVal[i][finish] = tmp;

                    tmp = Rt_OplayerVal[i][start];
                    Rt_OplayerVal[i][start] = Rt_OplayerVal[i][finish];
                    Rt_OplayerVal[i][finish] = tmp;
                    start++;
                    finish--;
                }
            }
        }
    }

    public void StampGame(MNKCell[] MC, MNKBoard B) {
        MNKCell c1, c2;
        boolean found = false;
        for (int i = 0; i < B.M; i++) {
            for (int j = 0; j < B.N; j++) {
                c1 = new MNKCell(i, j, MNKCellState.P1);
                c2 = new MNKCell(i, j, MNKCellState.P2);
                for (MNKCell M : MC) {
                    if (M.i == c1.i && M.j == c1.j && M.state == c1.state) {
                        System.out.print("X ");
                        found = true;
                        break;
                    } else if (M.i == c1.i && M.j == c1.j && M.state == c2.state) {
                        System.out.print("O ");
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.out.print("_ ");
                }
                found = false;
            }
            System.out.print("\n");
        }
        System.out.println("------");
    }

    public void getMainKey(MNKBoard B,MNKCell current_cell){
        generateKey(XplayerVal,OplayerVal,B,current_cell);
    }

    public void generateKey(long[][] Xlist, long[][] Olist, MNKBoard ZB,MNKCell current_cell){

        int n = ZB.N - 1, m = ZB.M - 1;

            if (current_cell.state == MNKCellState.P1){            // X turn - xor
                Zobby ^= Xlist[current_cell.i][current_cell.j];
                spec_Zobby ^= Xlist[current_cell.i][n - current_cell.j];
                bot_Zobby ^= Xlist[m - current_cell.i][current_cell.j];
                bot_Spec_Zobby ^= Xlist[m - current_cell.i][n - current_cell.j];

                rtZobby ^= Xlist[current_cell.i][current_cell.j];
                rtspec_Zobby ^= Xlist[current_cell.i][n - current_cell.j];
                rtbot_Zobby ^= Xlist[m - current_cell.i][current_cell.j];
                rtbot_Spec_Zobby ^= Xlist[m - current_cell.i][n - current_cell.j];
            }

            else{
                Zobby ^= Olist[current_cell.i][current_cell.j];
                spec_Zobby ^= Olist[current_cell.i][n - current_cell.j];
                bot_Zobby ^= Olist[m - current_cell.i][current_cell.j];
                bot_Spec_Zobby ^= Olist[m - current_cell.i][n - current_cell.j];

                rtZobby ^= Olist[current_cell.i][current_cell.j];
                rtspec_Zobby ^= Olist[current_cell.i][n - current_cell.j];
                rtbot_Zobby ^= Olist[m - current_cell.i][current_cell.j];
                rtbot_Spec_Zobby ^= Olist[m - current_cell.i][n - current_cell.j];
            }

        
    }

    

    // funzione per il salvataggio di un valore all'interno della Transposition
    public boolean storeData(long Zobby, int alpha, int beta,int value, int depth){
        /*
        DataHash ZobbyKey = tt.get(Zobby);
        if(ZobbyKey != null && ZobbyKey.getDepth() >= depth)
            return false;
         */
        DataHash NewData;
        if(value <= alpha)
            NewData = new DataHash(depth, value, Flag.LOWERBOUND);
        else if(value >= beta)
            NewData = new DataHash(depth, value, Flag.UPPERBOUND);
        else
            NewData = new DataHash(depth, value, Flag.EXACT);
        if (tt.replace(Zobby,NewData)==null)
            tt.put(Zobby, NewData);
        return true;
    }

    public DataHash containsData(long Zobby,long botzobby, long botspeczobby, long speczobby){
        DataHash ttData = tt.get(Zobby);
        if(ttData == null){
            ttData = tt.get(botzobby);
            if(ttData == null){
                ttData = tt.get(botspeczobby);
                if(ttData == null)
                    ttData = tt.get(speczobby);
            }
        }
        return ttData;
    }
    public void clear (){
        tt.clear();
    }

    public long rtgetZobby(){
        return Zobby;
    }
    public long rtgetBot_Zobby(){
        return bot_Zobby;
    }
    public long rtgetBot_Spec_Zobby(){
        return bot_Spec_Zobby;
    }

    public long rtgetSpec_Zobby(){
        return spec_Zobby;
    }
    public long getZobby(){
        return Zobby;
    }
    public long getBot_Zobby(){
        return bot_Zobby;
    }
    public long getBot_Spec_Zobby(){
        return bot_Spec_Zobby;
    }
    public long getSpec_Zobby(){
        return spec_Zobby;
    }

    public static void main(String[] args){
        TransTable TT = new TransTable(3, 3);
        MNKBoard B = new MNKBoard(3, 3, 3);
        MNKCell c = new MNKCell(1, 0);
        MNKCell c2 = new MNKCell(0, 0);
        System.out.println(TT.getZobby());
        B.markCell(c.i,c.j);
        TT.getMainKey(B,c);
        System.out.println(TT.getZobby());

        B.markCell(c2.i, c2.j);
        TT.getMainKey(B,c2);
        System.out.println(TT.getZobby());

        B.unmarkCell();
        TT.getMainKey(B,c2);
        System.out.println(TT.getZobby());

        B.unmarkCell();
        TT.getMainKey(B,c);
        System.out.println(TT.getZobby());
    }
}

