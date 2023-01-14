package mnkgame;
import java.util.*;

public class ATTPlayerDEFFNFC implements MNKPlayer{
    private MNKBoard B;
    private MNKGameState myWin;
    private MNKGameState yourWin;
    private int TIMEOUT;
    double TimeLimit;
    long TimeStart;
    boolean TimeFinish;     //vede quando un for è finito e dunque può cambiare la bestcell
    private boolean First;
    private Heuristic Euristica;
    private MNKCell BestIterativeCell;
    private MNKCell NewBestCell;
    private boolean BigSize;
    private int DepthCount;
    private ArrayNFC NFC;

    //DEBUG
    private double TimeInFill;
    private long time;
    private int NewBestValue;
    private boolean presodaTT;
    private int visitedNode;
    int cacheHit;
    private TransTable TT;

    public ATTPlayerDEFFNFC(){}

    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
        visitedNode=0;
        B = new MNKBoard(M, N, K);
        myWin = first ? MNKGameState.WINP1 : MNKGameState.WINP2;
        yourWin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;
        First = first;
        TIMEOUT = timeout_in_secs;
        BigSize = M > 40 || N > 40;
        TimeLimit = BigSize ? 100. - (0.23) : 100. - B.K * 0.24  ;
        TimeFinish=false;
        Euristica=new Heuristic();
        TT=new TransTable(M,N);

        NFC = new ArrayNFC(M,N);
        cacheHit++;
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

    private void IterativeDeepening(boolean MaxplayerA, int maxdepth) {
        visitedNode = 0;
        TimeFinish = false;       //vede quando un for è finito e dunque può cambiare la bestcell
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int bestvalue = MaxplayerA ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int value = 0;
        NewBestValue = 0;

        for (DepthCount = 1; DepthCount <= maxdepth; DepthCount++) {
            BestIterativeCell = null;
            if ((System.currentTimeMillis() - TimeStart) / 1000.0 > TIMEOUT * (TimeLimit / 100.0)) {
                //System.out.println("Depth raggiunta: "+ (DepthCount-1));
                System.out.println("best: " + bestvalue + '\n');
                return;
            }
            bestvalue = MaxplayerA ? Integer.MIN_VALUE : Integer.MAX_VALUE;

            for (MNKCell d : NFC.getArray()) {
                if (NFC.contains(d)) {
                    if ((System.currentTimeMillis() - TimeStart) / 1000.0 > TIMEOUT * (TimeLimit / 100.0)) {
                        //System.out.println("Depth raggiunta: "+ (DepthCount-1));
                        TimeFinish = true;
                        break;
                    }

                    B.markCell(d.i, d.j);
                    NFC.fillNFCplus(d, B);

                    if (!TimeFinish) {
                        if (MaxplayerA) {
                            value = Math.max(Integer.MIN_VALUE, AlphaBeta(DepthCount, alpha, beta, false));
                            if(Math.abs(value) != 1000000) System.out.println(value);
                            /*if(d.i == 13 && d.j == 13)
                                System.out.println("13 -13 value: " + value + "\t");*/
                            bestvalue = bestMove(bestvalue, value, true, d);
                        } else {
                            value = Math.min(Integer.MAX_VALUE, AlphaBeta(DepthCount, alpha, beta, true));
                            bestvalue = bestMove(bestvalue, value, false, d);
                        }
                    }

                    B.unmarkCell();
                    NFC.deleteNFCplus(d, B);

                    //cutoff manuali
                    if ((First && value == 1000000) || (!First && value == -1000000)) {
                        NewBestCell = BestIterativeCell;
                        System.out.println("best: " + bestvalue + '\n');
                        return;
                    }
                }
            }
            boolean NoUpdate = Math.abs(bestvalue) ==1000000;
            if (NoUpdate && DepthCount==1) return;
            if ((!TimeFinish || DepthCount == 1) && !NoUpdate) {
                NewBestValue = bestvalue;
                NewBestCell = BestIterativeCell;   //se non finisce di ispezionare tutta l'altezza riporta la bestcell trovata prima
            }
        }
        System.out.println("best: " + bestvalue + '\n');
    }


    private int AlphaBeta(int depth,int alpha, int beta,boolean MaxPlayerA) {
        int value = 0;
        boolean Final = false;
        int AlphaOrig = alpha;
        
        TT.getMainKey(B);
        DataHash TTData = TT.containsData(TT.getZobby(), TT.getBot_Zobby(), TT.getBot_Spec_Zobby(), TT.getSpec_Zobby());
        if (TTData == null && B.M == B.N) {
            TT.getRTKey(B);
            TTData = TT.containsData(TT.getZobby(), TT.getBot_Zobby(), TT.getBot_Spec_Zobby(), TT.getSpec_Zobby());
        }
        if (TTData != null) {
            if (TTData.getDepth() >= depth) {
                int valuation = TTData.getValuation();
                switch (TTData.getFlag()) {
                    case EXACT:
                        return valuation;
                    case LOWERBOUND:
                        alpha = Math.max(alpha, valuation);
                        break;
                    case UPPERBOUND:
                        beta = Math.min(beta, valuation);
                        break;
                }
                if (alpha >= beta) {
                    return TTData.getValuation();
                }
            }
        }
    

        if (B.gameState() != MNKGameState.OPEN || depth == 0 || (System.currentTimeMillis() - TimeStart) / 1000.0 > TIMEOUT * (TimeLimit / 100.0)){
            value = Euristica.evaluate(B);
            
            Final = true;
        }

        if (!Final) {
            if (MaxPlayerA) {
                value = Integer.MIN_VALUE;
                for (MNKCell child : NFC.getArray()) {
                    if (NFC.contains(child)) {
                        B.markCell(child.i, child.j);
                        NFC.fillNFCplus(child, B);

                        value = Math.max(value, AlphaBeta(depth - 1, alpha, beta, false));
                        alpha = Math.max(value, alpha);

                        B.unmarkCell();
                        NFC.deleteNFCplus(child, B);

                        if (beta <= alpha)
                            break;
                    }

                }
            } else {
                value = Integer.MAX_VALUE;
                for (MNKCell child : NFC.getArray()) {
                    if (NFC.contains(child)) {
                        B.markCell(child.i, child.j);
                        NFC.fillNFCplus(child, B);

                        value = Math.min(value, AlphaBeta(depth - 1, alpha, beta, true));
                        beta = Math.min(value, beta);

                        B.unmarkCell();
                        NFC.deleteNFCplus(child, B);

                        if (beta <= alpha)
                            break;
                    }
                }
            }
        }


        if (!TimeFinish) {
            TT.getMainKey(B);
            TT.storeData(TT.getZobby(), AlphaOrig, beta, value, depth);
        }
        return value;
    }
    private int bestMove(int oldValue,int newValue,boolean MaxPlayerA,MNKCell valuateCell){
        if(MaxPlayerA){
            if(newValue > oldValue){
                BestIterativeCell = new MNKCell(valuateCell.i,valuateCell.j);
                return newValue;
            }
        }
        else{
            if(newValue < oldValue){
                BestIterativeCell = new MNKCell(valuateCell.i,valuateCell.j);
                return newValue;
            }
        }
        if (oldValue==newValue){
            BestIterativeCell=new MNKCell(valuateCell.i,valuateCell.j);
        }
        return oldValue;
    }
    public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC){
        TimeStart =System.currentTimeMillis();
        TimeLimit = BigSize ? 100. - (0.25+((MC.length)*0.025)) : TimeLimit;
        TimeInFill=0;

        if(MC.length > 0) {
            MNKCell c = new MNKCell(MC[MC.length - 1].i, MC[MC.length - 1].j, First ? MNKCellState.P2 : MNKCellState.P1);
            B.markCell(c.i,c.j); //mark the last move
            NFC.fillNFCplus(c,B);
        }

        if (MC.length==0 && First){
            if (B.M==3 && B.N==3){
                MNKCell c = new MNKCell(2,0);
                B.markCell(c.i,c.j);
                NFC.fillNFCplus(c,B);
                return c;
            }
            MNKCell c=new MNKCell((int)Math.floor(B.M/2.), (int)Math.floor(B.N/2.), MNKCellState.P1);
            B.markCell(c.i,c.j);
            NFC.fillNFCplus(c,B);
            return c;
        }

        if (MC.length==1 && !First && !(B.M==3 && B.N==3)){
            MNKCell c=new MNKCell((int)Math.floor(B.M/2.), (int)Math.floor(B.N/2.));
            if (B.B[c.i][c.j]!=MNKCellState.FREE && B.B[c.i+1][c.j+1]==MNKCellState.FREE){
                c = new MNKCell(c.i+1,c.j+1);
            }
            B.markCell(c.i,c.j);
            NFC.fillNFCplus(c,B);
            return c;
        }

        if (MC.length==2 && First && !(B.M==3 && B.N==3)){
            MNKCell c=new MNKCell((int)Math.floor(B.M/2.), (int)Math.floor(B.N/2.));
            if (B.B[c.i-1][c.j-1]==MNKCellState.P2){
                c=new MNKCell(c.i+1,c.j-1);
            }
            else if (B.B[c.i-1][c.j+1]==MNKCellState.P2){
                c=new MNKCell(c.i+1,c.j+1);
            }
            else if (B.B[c.i+1][c.j+1]==MNKCellState.P2){
                c=new MNKCell(c.i-1,c.j+1);
            }
            else{
                c=new MNKCell(c.i-1,c.j-1);
            }
            B.markCell(c.i,c.j);
            NFC.fillNFCplus(c,B);
            return c;
        }

        //CONTROLLARE COSA SUCCEDE SE SI METTE NON IN ANGOLO
        if(FC.length == 1) {
            return FC[0];  // only one move left
        }

        boolean MaxPlayerA = B.currentPlayer() == 0;  // Am I the starting player ? (PlayerA)

        NewBestCell = FC[0];

        IterativeDeepening(MaxPlayerA,(B.M*B.N)-MC.length);

        if (BigSize && DepthCount<=2) {
            NFCell[] Arr = NFC.getValidArray();
            for (NFCell d : Arr) {
                B.markCell(d.i, d.j);
                d.setValuation(Euristica.evaluate(B));
                B.unmarkCell();
            }

            if (First) {
                Arrays.sort(Arr, Collections.reverseOrder());
            } else {
                Arrays.sort(Arr);
            }

            B.markCell(Arr[0].i,Arr[0].j);
            NFC.fillNFCplus(Arr[0],B);
            TT.clear();
            return Arr[0];
        }

        B.markCell(NewBestCell.i,NewBestCell.j);
        NFC.fillNFCplus(NewBestCell,B);

        TT.clear();
        return NewBestCell;
    }

    public String playerName(){
        return "NisshokuPlayer";
    }
}
