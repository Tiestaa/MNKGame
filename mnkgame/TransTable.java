package mnkgame;

import java.util.Map;
import java.util.HashMap;
import java.util.Random;

public class TransTable {
    private int N, M;

    private Map<Long, DataHash> tt;                            // Tabella Hash
    private long[][] XplayerVal, OplayerVal;                   // matrice principale con i valori casuali
    private long[][] Rt_XplayerVal, Rt_OplayerVal;             // matrice 
    private long Zobby, spec_Zobby, bot_Zobby, bot_Spec_Zobby;          //keys
    private long rtZobby, rtspec_Zobby, rtbot_Zobby, rtbot_Spec_Zobby;  //keys ruotate
    private long ZobriestKey;

    public TransTable(int M, int N) {
        this.M = M;
        this.N = N;
        this.tt = new HashMap<Long, DataHash>();
        XplayerVal = new long[M][N]; OplayerVal = new long[M][N];
        Rt_XplayerVal = new long[M][N]; Rt_OplayerVal = new long[M][N];
        Zobby = 0; spec_Zobby = 0; bot_Zobby = 0; bot_Spec_Zobby = 0;
        rtZobby = 0; rtspec_Zobby = 0; rtbot_Zobby = 0; rtbot_Spec_Zobby = 0;

        ZobriestKey =0;

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

    public void getKeys(MNKBoard B,MNKCell current_cell){
        generateKey(current_cell);
    }

    public void generateKey(MNKCell current_cell){

        int n = N - 1, m = M - 1;

        if (current_cell.state == MNKCellState.P1){            // X turn - xor
            Zobby ^= XplayerVal[current_cell.i][current_cell.j];
            spec_Zobby ^= XplayerVal[current_cell.i][n - current_cell.j];
            bot_Zobby ^= XplayerVal[m - current_cell.i][current_cell.j];
            bot_Spec_Zobby ^= XplayerVal[m - current_cell.i][n - current_cell.j];

            if(M==N){
                rtZobby ^= Rt_XplayerVal[current_cell.i][current_cell.j];
                rtspec_Zobby ^= Rt_XplayerVal[current_cell.i][n - current_cell.j];
                rtbot_Zobby ^= Rt_XplayerVal[m - current_cell.i][current_cell.j];
                rtbot_Spec_Zobby ^= Rt_XplayerVal[m - current_cell.i][n - current_cell.j];
            }
        }
            
        else{
            Zobby ^= OplayerVal[current_cell.i][current_cell.j];
            spec_Zobby ^= OplayerVal[current_cell.i][n - current_cell.j];
            bot_Zobby ^= OplayerVal[m - current_cell.i][current_cell.j];
            bot_Spec_Zobby ^= OplayerVal[m - current_cell.i][n - current_cell.j];

            if(M==N){
                rtZobby ^= Rt_OplayerVal[current_cell.i][current_cell.j];
                rtspec_Zobby ^= Rt_OplayerVal[current_cell.i][n - current_cell.j];
                rtbot_Zobby ^= Rt_OplayerVal[m - current_cell.i][current_cell.j];
                rtbot_Spec_Zobby ^= Rt_OplayerVal[m - current_cell.i][n - current_cell.j];
            }

        }
    }

    
    // funzione per il salvataggio di un valore all'interno della Transposition
    public boolean storeData(int alpha, int beta,int value, int depth){
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

        if (tt.containsKey(ZobriestKey)){
            DataHash OldData = tt.get(Zobby);
            tt.replace(ZobriestKey, OldData, NewData);
        }
        else tt.put(ZobriestKey, NewData);

        /*
        if (tt.replace(Zobby,NewData)==null)
            tt.put(Zobby, NewData);
            */
        return true;
    }

    public boolean findRightKey(long Z,long bZ, long bsZ, long sZ){
        if (tt.containsKey(Z)) {
            ZobriestKey =Z;
            return true;
        }
        if (tt.containsKey(bZ)) {
            ZobriestKey =Z;
            return true;
        }
        if (tt.containsKey(bsZ)) {
            ZobriestKey =Z;
            return true;
        }
        if (tt.containsKey(sZ)) {
            ZobriestKey =Z;
            return true;
        }
        return false;
    }


    public DataHash is_in_TT(){
        if (findRightKey(Zobby, spec_Zobby, bot_Zobby, bot_Spec_Zobby)) return tt.get(ZobriestKey);
        else if (M==N && findRightKey(rtZobby, rtspec_Zobby, rtbot_Zobby, rtbot_Spec_Zobby)) return tt.get(ZobriestKey);
        else return null;
    }



    public void clear (){
        tt.clear();
    }

    public long rtgetZobby(){
        return rtZobby;
    }
    public long rtgetBot_Zobby(){
        return rtbot_Zobby;
    }
    public long rtgetBot_Spec_Zobby(){
        return rtbot_Spec_Zobby;
    }

    public long rtgetSpec_Zobby(){
        return rtspec_Zobby;
    }

    public long getRightKey(){
        return ZobriestKey;
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
        MNKBoard Btest=new MNKBoard(3,3,3);
        TransTable TT=new TransTable(3,3);

        System.out.println("Zobby: "+ TT.getZobby()+"\tSpec_Zobby: "+ TT.getSpec_Zobby()+"\tBot_Zobby: "+ TT.getBot_Zobby()+"\tBot_Spec_Zobby: "+ TT.getBot_Spec_Zobby());
        System.out.println("rtZobby: "+ TT.rtgetZobby()+"\trtSpec_Zobby: "+ TT.rtgetSpec_Zobby()+"\trtBot_Zobby: "+ TT.rtgetBot_Zobby()+"\trtBot_Spec_Zobby: "+ TT.rtgetBot_Spec_Zobby()+"\n");


        Btest.markCell(0,0);
        TT.getKeys(Btest,new MNKCell(0,0));
        Btest.markCell(0,1);
        TT.StampGame(Btest.getMarkedCells(),Btest);
        TT.getKeys(Btest,new MNKCell(0,1));
        System.out.println("Zobby: "+ TT.getZobby()+"\tSpec_Zobby: "+ TT.getSpec_Zobby()+"\tBot_Zobby: "+ TT.getBot_Zobby()+"\tBot_Spec_Zobby: "+ TT.getBot_Spec_Zobby());
        System.out.println("rtZobby: "+ TT.rtgetZobby()+"\trtSpec_Zobby: "+ TT.rtgetSpec_Zobby()+"\trtBot_Zobby: "+ TT.rtgetBot_Zobby()+"\trtBot_Spec_Zobby: "+ TT.rtgetBot_Spec_Zobby());





        Btest.unmarkCell();
        Btest.unmarkCell();


        Btest.markCell(0,2);
        TT.getKeys(Btest,new MNKCell(0,2));
        Btest.markCell(1,2);
        TT.StampGame(Btest.getMarkedCells(),Btest);
        TT.getKeys(Btest,new MNKCell(1,2));
        System.out.println("Zobby: "+ TT.getZobby()+"\tSpec_Zobby: "+ TT.getSpec_Zobby()+"\tBot_Zobby: "+ TT.getBot_Zobby()+"\tBot_Spec_Zobby: "+ TT.getBot_Spec_Zobby());
        System.out.println("rtZobby: "+ TT.rtgetZobby()+"\trtSpec_Zobby: "+ TT.rtgetSpec_Zobby()+"\trtBot_Zobby: "+ TT.rtgetBot_Zobby()+"\trtBot_Spec_Zobby: "+ TT.rtgetBot_Spec_Zobby());
        return;

    }

}

