package mnkgame;

import java.util.Map;
import java.util.HashMap;
import java.util.Random;

public class TransTable {
    private int N, M;

    private Map<Long, DataHash> tt;
    private long[][] XplayerVal, OplayerVal;
    private long[][] Rt_XplayerVal, Rt_OplayerVal;
    private long Zobby, spec_Zobby, bot_Zobby, bot_Spec_Zobby;

    public TransTable(int M, int N) {
        this.M = M;
        this.N = N;
        this.tt = new HashMap<Long, DataHash>();
        XplayerVal = new long[M][N]; OplayerVal = new long[M][N];
        Rt_XplayerVal = new long[M][N]; Rt_OplayerVal = new long[M][N];

        // Main random board
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                XplayerVal[i][j] = Math.abs(new Random().nextLong());
                OplayerVal[i][j] = Math.abs(new Random().nextLong());
            }
        }
        //Rotated:
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

    public void getMainKey(MNKBoard B){
        generateKey(XplayerVal,OplayerVal,B);
    }

    public void getRTKey(MNKBoard B){
        generateKey(Rt_XplayerVal,Rt_OplayerVal,B);
    }

    public void generateKey(long[][] Xlist, long[][] Olist,MNKBoard ZB){
        Zobby = 0;
        spec_Zobby = 0;      //Specular board
        bot_Zobby = 0;       //Upside Down board
        bot_Spec_Zobby = 0;  //Specular and UpDown board

        int i, j;
        int n = ZB.N - 1, m = ZB.M - 1;


        for (MNKCell z: ZB.getMarkedCells()){
            i = z.i;
            j = z.j;

            if (z.state == MNKCellState.P1){
                Zobby ^= Xlist[i][j];
                spec_Zobby ^= Xlist[i][n - j];
                bot_Zobby ^= Xlist[m - i][j];
                bot_Spec_Zobby ^= Xlist[m - i][n - j];
            }
            else{
                Zobby ^= Olist[i][j];
                spec_Zobby ^= Olist[i][n - j];
                bot_Zobby ^= Olist[m - i][j];
                bot_Spec_Zobby ^= Olist[m - i][n - j];
            }
        }
    }

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

    public DataHash[] printTTValues(){
        return tt.values().toArray(new DataHash[0]);
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

}