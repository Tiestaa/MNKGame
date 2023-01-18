package mnkgame;
import java.util.*;

public class ATTPlayerDEFFNFC implements MNKPlayer{
    private MNKBoard B;
    private MNKGameState myWin;
    private MNKGameState yourWin;
    private int TIMEOUT;
    double TimeLimit;
    long TimeStart;
    boolean TimeFinish; 
    private boolean First;
    private Heuristic Euristica;
    private MNKCell BestIterativeCell;
    private MNKCell NewBestCell;
    private int DepthCount;
    private HashMapNFC NFC;
    private long bucketKey;
    private boolean Final;
    private TransTable TT;

    public ATTPlayerDEFFNFC(){}

    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
        B = new MNKBoard(M, N, K);
        myWin = first ? MNKGameState.WINP1 : MNKGameState.WINP2;
        yourWin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;
        First = first;
        TIMEOUT = timeout_in_secs;
        TimeLimit = 100. - 0.4;
        TimeFinish=false;
        Euristica=new Heuristic();
        TT=new TransTable(M,N);
        bucketKey = 0;
        NFC = new HashMapNFC(M,N);
        Final=false;
    }

    private void IterativeDeepening(boolean MaxplayerA, int maxdepth) {
        int alpha = Integer.MIN_VALUE;       //minvalue
        int beta = Integer.MAX_VALUE;     //maxvalue
        int bestvalue = MaxplayerA ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int value = 0;

        for (DepthCount = 1; DepthCount <= maxdepth; DepthCount++) {
            BestIterativeCell = null;
            if ((System.currentTimeMillis() - TimeStart) / 1000.0 > TIMEOUT * (TimeLimit / 100.0)) {
                TimeFinish=true;
                break;
            }
            bestvalue = MaxplayerA ? Integer.MIN_VALUE : Integer.MAX_VALUE;

            for (MNKCell d : NFC.getArray()) {


                if ((System.currentTimeMillis() - TimeStart) / 1000.0 > TIMEOUT * (TimeLimit / 100.0)) {
                    TimeFinish = true;
                    break;
                }

                B.markCell(d.i, d.j);
                TT.getKeys(B,d);
                NFC.fillNFCplus(d, B);

                if (!TimeFinish) {
                    if (MaxplayerA) {
                        value = Math.max(Integer.MIN_VALUE, AlphaBeta(DepthCount, alpha, beta, false,d));
                        bestvalue = bestMove(bestvalue, value, true, d);
                    } else {
                        value = Math.min(Integer.MAX_VALUE, AlphaBeta(DepthCount, alpha, beta, true,d));
                        bestvalue = bestMove(bestvalue, value, false, d);
                    }
                }
                
                B.unmarkCell();
                TT.getKeys(B,d);
                NFC.deleteNFCplus(d, B);

                if (TimeFinish) break;

                //cutoff manuali
                
                if ((First && bestvalue == 10000000) || (!First && bestvalue == -10000000)) {
                    NewBestCell = BestIterativeCell;
                    return;
                }
                
                //System.out.println("cella : "+ d.i +"-"+d.j+"\t\tvalue: "+ value+"\t\tbestvalue: "+ bestvalue+"\t\taltezza: "+ DepthCount);
            }
            
            boolean NoUpdate = Math.abs(bestvalue) == 10000000;
            //if (NoUpdate && DepthCount==1) return;      //se ad altezza 1 non ci sono soluzioni in cui si evita la sconfitta si ritorna subito
            if ((!TimeFinish || DepthCount == 1) && !NoUpdate ){       //MODIFICATO
                //System.out.println("Cambio la cella "+ NewBestCell.i+"-"+NewBestCell.j+" con la cella "+ BestIterativeCell.i+"-"+BestIterativeCell.j);
                NewBestCell = BestIterativeCell;   //se non finisce di ispezionare tutta l'altezza riporta la bestcell trovata prima

            }
        
        }
    }


    private int AlphaBeta(int depth,int alpha, int beta,boolean MaxPlayerA,MNKCell currentCell) {

        int value = MaxPlayerA ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        if ((System.currentTimeMillis() - TimeStart) / 1000.0 > TIMEOUT * (TimeLimit / 100.0)){    
            TimeFinish=true;
            return value;
        }
         
        Final = false;
        int AlphaOrig = alpha;
        DataHash TTData = TT.is_in_TT();
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
        
        
        if ((B.gameState() != MNKGameState.OPEN || depth == 0) && !TimeFinish ){
            value = Euristica.evaluate(B);
            Final = true;
        }

        if (!Final && !TimeFinish) {
            if (MaxPlayerA) {
                value = Integer.MIN_VALUE;
                for (MNKCell child : NFC.getArray()) {
                    if (TimeFinish) break;
                    B.markCell(child.i, child.j);
                    TT.getKeys(B,child);
                    NFC.fillNFCplus(child, B);

                    value = Math.max(value, AlphaBeta(depth - 1, alpha, beta, false, child));
                    alpha = Math.max(value, alpha);

                    B.unmarkCell();
                    TT.getKeys(B, child);
                
                    NFC.deleteNFCplus(child, B);

                    if (beta <= alpha)
                        break;
        
                    if (TimeFinish) break;
                }
            } else {
                value = Integer.MAX_VALUE;
                for (MNKCell child : NFC.getArray()) {
                    if (TimeFinish) break;
                    B.markCell(child.i, child.j);
                    TT.getKeys(B,child);
                    NFC.fillNFCplus(child, B);

                    value = Math.min(value, AlphaBeta(depth - 1, alpha, beta, true,child));
                    beta = Math.min(value, beta);

                    B.unmarkCell();
                    TT.getKeys(B, child);
                    NFC.deleteNFCplus(child, B);

                    if (beta <= alpha)
                        break;
        
                    if (TimeFinish) break;
                }
            }
        }

        if (!TimeFinish) {
            TT.getKeys(B,currentCell);
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
        TimeFinish=false;

        if(MC.length > 0) {
            TT.clear();     
            MNKCell c = new MNKCell(MC[MC.length - 1].i, MC[MC.length - 1].j, First ? MNKCellState.P2 : MNKCellState.P1);
            B.markCell(c.i,c.j); //mark the last move
            TT.getKeys(B,c);
            NFC.fillNFCplus(c,B);
        }

        if (MC.length==0 && First){
            if (B.M==3 && B.N==3){
                MNKCell c = new MNKCell(2,0);
                B.markCell(c.i,c.j);
                TT.getKeys(B,c);
                NFC.fillNFCplus(c,B);
                return c;
            }
            MNKCell c=new MNKCell((int)Math.floor(B.M/2.), (int)Math.floor(B.N/2.), MNKCellState.P1);
            B.markCell(c.i,c.j);
            TT.getKeys(B,c);
            NFC.fillNFCplus(c,B);
            return c;
        }
            
        if (MC.length==1 && !First && !(B.M==3 && B.N==3)){
            MNKCell c=new MNKCell((int)Math.floor(B.M/2.), (int)Math.floor(B.N/2.));
            if (B.B[c.i][c.j]!=MNKCellState.FREE && B.B[c.i+1][c.j+1]==MNKCellState.FREE){
                c = new MNKCell(c.i-1,c.j-1);    
            }
            B.markCell(c.i,c.j);
            TT.getKeys(B,c);
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
            TT.getKeys(B,c);
            NFC.fillNFCplus(c,B);
            return c;
        }
        
        if(FC.length == 1) {
            return FC[0];  // only one move left
        }

        boolean MaxPlayerA = B.currentPlayer() == 0;  // Am I the starting player ? (PlayerA)

        NewBestCell = FC[0];
        
        System.out.println("tempo prima di IT: "+ ((System.currentTimeMillis() - TimeStart)/1000.));
        IterativeDeepening(MaxPlayerA,(B.M*B.N)-MC.length);
        System.out.println("tempo dopo di IT: "+ ((System.currentTimeMillis() - TimeStart)/1000.)+"\n");

        B.markCell(NewBestCell.i,NewBestCell.j);
        TT.getKeys(B,NewBestCell);
        NFC.fillNFCplus(NewBestCell,B);
        return NewBestCell;
    }

    public String playerName(){
        return "NisshokuPlayer";
    }
}
