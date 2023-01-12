package mnkgame;

import java.util.ArrayList;
import java.util.HashSet;

public class Heuristic {

    private final HashSet<MNKCell> hor;
    private final HashSet<MNKCell> ver;
    private final HashSet<MNKCell> diag;
    private final HashSet<MNKCell> antidiag;

    private boolean currentPlayerCell;

    public Heuristic(){
        //utilizzo un hashset per tenere traccia delle celle già utilizzate per i controlli rispettivamente orizzontali, verticali, diagonali e antidiagonali
        hor = new HashSet<>();
        ver = new HashSet<>();
        diag = new HashSet<>();
        antidiag = new HashSet<>();
    }

    private int checkBis(MNKCell cell, MNKBoard B){
        //NON FUNZIONA
        MNKCellState opponentstate = (cell.state == MNKCellState.P1) ? MNKCellState.P2 : MNKCellState.P1;       //funziona
        int i=cell.i;
        int j=cell.j;
        int lineCount=1;
        int totalValuation=0;

        //Horizontal check
        int k=1;
        while(j-k >= 0 && k<B.K+1 && B.B[i][j-k] != opponentstate){     // backward check
            lineCount++;k++;
        }
        k=1;
        while(j+k <  B.N && k<B.K+1 &&B.B[i][j+k] != opponentstate){        // forward check
            lineCount++;k++;
        }

        if (lineCount >= B.K) totalValuation += 1;

        // Vertical check
        k=1;
        lineCount=1;
        while(i-k >= 0 && k<B.K+1 && B.B[i-k][j] != opponentstate){     // backward check
            lineCount++;k++;
        }

        k=1;
        while(i+k <  B.M && k<B.K+1 && B.B[i+k][j] != opponentstate){     // forward check
            lineCount++;k++;
        }

        if (lineCount >= B.K) totalValuation += 1;

        //diagonal check
        k=1;
        lineCount=1;
        while(i-k >= 0 && j-k >= 0 && k<B.K+1 && B.B[i-k][j-k]  != opponentstate){     // backward check
            lineCount++;k++;
        }

        k=1;
        while(i+k <  B.M && k<B.K+1 && j+k <  B.N && B.B[i+k][j+k] != opponentstate){   // forward check
            lineCount++;k++;
        }

        if (lineCount >= B.K) totalValuation += 1;

        //antidiagonale check
        k=1;
        lineCount=1;
        while(i-k >= 0 && j+k < B.N && k<B.K+1 && B.B[i-k][j+k] != opponentstate){     // backward check
            lineCount++;k++;
        }
        k=1;
        while(i+k <  B.M && j-k >= 0 && k<B.K+1 && B.B[i+k][j-k] != opponentstate){   // forward check
            lineCount++;k++;
        }

        if (lineCount >= B.K) totalValuation += 1;

        return totalValuation;
    }

    private int check(MNKCell cell, MNKBoard B){

        MNKCellState opponentstate = (cell.state == MNKCellState.P1) ? MNKCellState.P2 : MNKCellState.P1;
        int i=cell.i;
        int j=cell.j;
        int totalValuation=0;
        int totalcell=1;        //celle vuote e di cell.state 
        int totalmycell=1;
        boolean canBeAligned=true; 

        // Horizontal check
        int k=1;
        while(j-k >= 0 && k<B.K+1 && B.B[i][j-k] != opponentstate){     // backward check
            if (B.B[i][j-k] == cell.state)
                if (canBeAligned) totalmycell++;
            else canBeAligned=false;
            totalcell++;k++;
        }

        canBeAligned=true;
        k=1;
        while(j+k <  B.N && k<B.K+1 &&B.B[i][j+k] != opponentstate){   // forward check
            if (B.B[i][j+k] == cell.state)
                if (canBeAligned) totalmycell++;
            else canBeAligned=false;
            totalcell++;k++;
        }

        totalValuation += valuecheck(totalcell,totalmycell,B.K);

        // Vertical check;
        canBeAligned=true;
        totalmycell=1;
        totalcell=1;
        k=1;
        while(i-k >= 0 && k<B.K+1 && B.B[i-k][j] != opponentstate){     // backward check
            if (B.B[i-k][j] == cell.state)
                if (canBeAligned) totalmycell++;
            else canBeAligned=false;
            totalcell++;k++;
        }
        canBeAligned=true;
        k=1;
        while(i+k <  B.M && k<B.K+1 && B.B[i+k][j] != opponentstate){     // forward check
            if (B.B[i+k][j] == cell.state)
                if (canBeAligned) totalmycell++;
            else canBeAligned=false;
            totalcell++;k++;
        }

        totalValuation += valuecheck(totalcell,totalmycell,B.K);

        // Diagonal check
        canBeAligned=true;
        totalmycell=1;
        totalcell=1;
        k=1;
        while(i-k >= 0 && j-k >= 0 && k<B.K+1 && B.B[i-k][j-k]  != opponentstate){     // backward check
            if (B.B[i-k][j-k] == cell.state)
                if (canBeAligned) totalmycell++;
            else canBeAligned=false;
            totalcell++;k++;
        }
        canBeAligned=true;
        k=1;
        while(i+k <  B.M && k<B.K+1 && j+k <  B.N && B.B[i+k][j+k] != opponentstate){   // forward check
            if (B.B[i+k][j+k] == cell.state)
                if (canBeAligned) totalmycell++;
            else canBeAligned=false;
            totalcell++;k++;
        }

        totalValuation += valuecheck(totalcell,totalmycell,B.K);

        // Anti-diagonal check
        canBeAligned=true;
        totalmycell=1;
        totalcell=1;
        k=1;
        while(i-k >= 0 && j+k < B.N && k<B.K+1 && B.B[i-k][j+k] != opponentstate){     // backward check
            if (B.B[i-k][j+k] == cell.state)
                if (canBeAligned) totalmycell++;
            else canBeAligned=false;
            totalcell++;k++;
        }
        canBeAligned=true;
        k=1;
        while(i+k <  B.M && j-k >= 0 && k<B.K+1 && B.B[i+k][j-k] != opponentstate){   // forward check
            if (B.B[i+k][j-k] == cell.state)
                if (canBeAligned) totalmycell++;
            else canBeAligned=false;
            totalcell++;k++;
        }

        totalValuation += valuecheck(totalcell,totalmycell,B.K);

        return totalValuation;
    }

    private int valuecheck(int totalcell, int totalMyCell, int K){
        int sum=0;
        if (totalcell > K){     // se è maggiore di k vuol dire che lì si può creare una minaccia, dunque aumento il valore della value
            if (totalcell>K*2) sum+=totalcell*6;
            else sum+=totalcell*4;
        }
        else if (totalcell==K) sum+=totalcell*2;      // se è uguale a K vuol dire che si può creare una minaccia ma facilmente bloccabile, dunque lo lascio così
        //in caso le celle siano minori di k vuol dire che sicuramente non ci potrà essere una minaccia
        sum+=totalMyCell;

        return sum;
    }

    private int isAntiDiagOpen(MNKCell start, MNKCell arrive, MNKBoard B) {
        int hole = 0;
        if (start.i - 1 >= 0 && start.j+1< B.N && B.B[start.i-1][start.j+1] == MNKCellState.FREE) {
            hole++;
        }
        if (arrive.i + 1 < B.M && arrive.j-1 >=0 && B.B[arrive.i+1][arrive.j-1] == MNKCellState.FREE) {
            hole++;
        }
        return hole;
    }
    private int isDiagOpen(MNKCell start, MNKCell arrive, MNKBoard B) {
        int hole = 0;
        if (start.i - 1 >= 0 && start.j-1>=0 && B.B[start.i-1][start.j-1] == MNKCellState.FREE) {
            hole++;
        }
        if (arrive.i + 1 < B.M && arrive.j+1 <B.N && B.B[arrive.i+1][arrive.j+1] == MNKCellState.FREE) {
            hole++;
        }
        return hole ;
    }
    private int isVerOpen(MNKCell start, MNKCell arrive, MNKBoard B) {
        int hole = 0;
        if (start.i - 1 >= 0 && B.B[start.i-1][start.j] == MNKCellState.FREE) {
            hole++;
        }
        if (arrive.i + 1 < B.M && B.B[arrive.i+1][arrive.j] == MNKCellState.FREE) {
            hole++;
        }
        return hole ;
    }
    private  int isHorOpen(MNKCell start, MNKCell arrive, MNKBoard B) {

        int hole = 0;
        if (start.j - 1 >= 0 && B.B[start.i][start.j-1] == MNKCellState.FREE) {
            hole++;
        }
        if (arrive.j + 1 < B.N && B.B[arrive.i][arrive.j + 1] == MNKCellState.FREE) {
            hole++;
        }
        return hole;
    }


    //PRIMA PROVA
    private int evaluateHorThreat(MNKCell c, MNKBoard B) {
        int i = c.i;
        int j = c.j;
        int count = 1;  //numero di celle "consecutive"
        MNKCellState opponentstate= c.state == MNKCellState.P1 ? MNKCellState.P2: MNKCellState.P1;
        int k = 1;
        boolean jump = false;     //mi tiene traccia di un salto di una cella vuota
        boolean flag = true;      //in caso di salto già avvenuto, il while si ferma
        boolean currentPlayerNode = (B.currentPlayer()==0 && c.state==MNKCellState.P1)||(B.currentPlayer() == 1 && c.state==MNKCellState.P2);

        //prendo le celle di start e arrivo per verificare successivamente che celle ci sono alle estremità,
        //partono entrambe dalla stessa cella
        MNKCell start = c;
        MNKCell arrive = c;

        //backward
        while (flag && j - k >= 0 && k < B.K && B.B[i][j - k] != opponentstate) {
            if (B.B[i][ j - k] == MNKCellState.FREE) {
                if (!jump) {
                    //se non ha saltato vado a vedere la cella ancora precedente, se è occupata dallo stesso player allora effettuo il salto
                    if (j - k - 1 >= 0 && B.B[i][j - k - 1] == c.state) {
                        jump = true;
                    } else flag = false;        //se trovo due celle consecutive vuote non ha senso parlare di salto, quindi si ferma il while con jump=false
                } else {
                    flag = false;       //se ha già saltato e trova un'altra cella vuota si ferma
                }
            } else {
                start = new MNKCell(i, j - k, c.state); //scalo lo start alla nuova cella trovata
                count++;
                hor.add(start);         //aggiungo la nuova cella in modo da non contare più volte la stessa minaccia

            }
            k++;
        }

        //simmetrico al backward
        //forward
        flag = true;
        k = 1;
        while (flag && j + k < B.N && k < B.K && B.B[i][j + k] != opponentstate) {
            if (B.B[i][j + k] == MNKCellState.FREE) {
                if (!jump) {
                    if (j + k + 1 < B.N && B.B[i][j + k + 1] == c.state) {
                        jump = true;
                    } else flag = false;
                } else {
                    flag = false;
                }
            } else {
                arrive = new MNKCell(i, j + k, c.state);        //modifico l'arrive
                count++;
                hor.add(arrive);
            }
            k++;
        }

        //controllo di che tipo di minaccia si tratta
        if (count==B.K-1){
            if (isHorOpen(start,arrive,B)==2) {
                if (!jump) {
                    if (currentPlayerNode) return 250;
                    else return 5020;
                } else {
                    //k-1 halfopen,allineati con estremi liberi ma con un salto
                    if (currentPlayerNode) return 80;
                    else return 1500;
                }
            }
            else if (isHorOpen(start,arrive,B)==1) {
                //k-1 halfopen,allineati con un estremo libero senza/con un salto
                if (currentPlayerNode) return 80;
                else return 1500;
            }
            else {
                if (jump) {
                    //k-1 allineati con estremi non liberi ma con un salto
                    if (currentPlayerNode) return 80;
                    else return 1500;
                }
            }
        }

        else if (count == B.K - 2) {

            if (isHorOpen(start, arrive,B) == 2) {
                //k-2 open
                if (!jump) {
                    if (currentPlayerNode) return 150;
                    else return 1200;
                }
            }
        }
        return 0;
    }

    private int evaluateVerThreat(MNKCell c, MNKBoard B){
        int i=c.i;
        int j=c.j;
        int count=1;
        MNKCellState opponentstate= c.state == MNKCellState.P1 ?MNKCellState.P2: MNKCellState.P1;
        int k=1;
        boolean jump=false;
        boolean flag=true;
        MNKCell start=c;
        MNKCell arrive=c;
        boolean currentPlayerNode= (B.currentPlayer()==0 && c.state==MNKCellState.P1)||(B.currentPlayer() == 1 && c.state==MNKCellState.P2);

        //backward
        while ((((flag && i-k >=0) && k< B.K) && B.B[i-k][j]!=opponentstate)) {
            if(B.B[i-k][j]==MNKCellState.FREE) {
                if (!jump) {
                    if (i-k-1 >=0 && B.B[i-k-1][j]==c.state){
                        jump=true;
                    }
                    else flag=false;
                } else {
                    flag=false;
                }
            }
            else{
                start = new MNKCell(i-k,j, c.state);
                count++;
                ver.add(start);

            }
            k++;
        }

        //forward
        flag=true;
        k=1;
        while ((((flag && i+k < B.M) && k< B.K) && B.B[i+k][j]!=opponentstate)){
            if(B.B[i+k][j]==MNKCellState.FREE) {
                if (!jump) {
                    if (i+k+1 < B.M && B.B[i+k+1][j]==c.state){
                        jump=true;
                    }
                    else flag=false;
                } else {
                    flag=false;
                }
            }
            else{
                arrive = new MNKCell(i+k, j, c.state);
                count++;
                ver.add(arrive);

            }
            k++;
        }

        if (count==B.K-1){
            if (isVerOpen(start,arrive,B)==2) {
                if (!jump) {
                    if (currentPlayerNode) return 250;
                    else return 5020;
                }
                else {
                    if (currentPlayerNode)return 80;
                    else return 1500;
                }
            }
            else if (isVerOpen(start,arrive,B)==1)
                if (currentPlayerNode)return 80;
                else return 1500;
            else {
                if (jump) {
                    if (currentPlayerNode)return 80;
                    else return 1500;
                }
            }
        }
        else if (count == B.K - 2) {
            if (isVerOpen(start, arrive,B) == 2) {
                if (!jump) {
                    if (currentPlayerNode) return 150;
                    else return 1200;
                }
            }
        }
        return 0;
    }
    private int evaluateDiagThreat(MNKCell c, MNKBoard B){
        int i=c.i;
        int j=c.j;
        int count=1;
        MNKCellState opponentstate= c.state == MNKCellState.P1 ?MNKCellState.P2: MNKCellState.P1;
        int k=1;
        boolean jump=false;
        boolean flag=true;
        MNKCell start=c;
        MNKCell arrive=c;
        boolean currentPlayerNode= (B.currentPlayer()==0 && c.state==MNKCellState.P1)||(B.currentPlayer() == 1 && c.state==MNKCellState.P2);

        //backward
        while (flag && i-k>=0 && j-k>=0 && k<B.K && B.B[i-k][j-k]!=opponentstate) {
            if (B.B[i - k][j - k] == MNKCellState.FREE) {
                if (!jump) {
                    if (i - k - 1 >= 0 && j - k - 1 >= 0 && B.B[i - k - 1][j - k - 1] == c.state) {
                        jump = true;
                    } else flag = false;
                } else {
                    flag = false;
                }
            } else {
                start = new MNKCell(i-k, j-k, c.state);
                count++;
                diag.add(start);
            }
            k++;
        }

        //forward
        flag=true;
        k=1;
        while (flag && i+k < B.M && j+k < B.N &&B.B[i+k][j+k] != opponentstate){
            if(B.B[i+k][j+k]==MNKCellState.FREE) {
                if (!jump) {
                    if (i+k+1 < B.M && j+k+1 < B.N && B.B[i+k+1][j+k+1] == c.state){
                        jump=true;
                    }
                    else flag=false;
                } else {
                    flag=false;
                }
            }
            else{
                arrive = new MNKCell(i+k, j+k, c.state);
                count++;
                diag.add(arrive);
            }
            k++;
        }

        if (count==B.K-1){
            if (isDiagOpen(start,arrive,B)==2) {
                if (!jump) {
                    if (currentPlayerNode)return 250;
                    else return 5020;
                }
                else {
                    if (currentPlayerNode)return 80;
                    else return 1500;
                }
            }
            else if (isDiagOpen(start,arrive,B)==1) {
                if (currentPlayerNode)return 80;
                else return 1500;
            }
            else {
                if (jump) {
                    if (currentPlayerNode)return 80;
                    else return 1500;
                }
            }
        }
        else if (count == B.K - 2) {
            if (isDiagOpen(start, arrive,B) == 2) {
                if (!jump) {
                    if (currentPlayerNode)return 150;
                    else return 1200;
                };
            }
        }
        return 0;
    }
    private int evaluateAntiDiagThreat(MNKCell c, MNKBoard B){
        int i=c.i;
        int j=c.j;
        int count=1;
        MNKCellState opponentstate= c.state == MNKCellState.P1 ?MNKCellState.P2: MNKCellState.P1;
        int k=1;
        boolean jump=false;
        boolean flag=true;
        MNKCell start=c;
        MNKCell arrive=c;
        boolean currentPlayerNode= (B.currentPlayer()==0 && c.state==MNKCellState.P1)||(B.currentPlayer() == 1 && c.state==MNKCellState.P2);

        //backward
        while (flag && i-k >= 0 && j+k < B.N  && B.B[i-k][j+k]!=opponentstate) {
            if(B.B[i-k][j+k]==MNKCellState.FREE) {
                if (!jump) {
                    if (i-k-1 >=0 && j+k+1 < B.N && B.B[i-k-1][j+k+1] == c.state){
                        jump=true;
                    }
                    else flag=false;
                } else {
                    flag=false;
                }
            }
            else{
                start = new MNKCell(i-k, j+k, c.state);
                count++;
                antidiag.add(start);
            }
            k++;
        }

        flag=true;
        //forward
        k=1;
        while (flag && i+k <B.M && j-k >= 0 && B.B[i+k][j-k]!=opponentstate){
            if(B.B[i+k][j-k]==MNKCellState.FREE) {
                if (!jump) {
                    if (i+k+1 < B.M && j-k-1>=0 && B.B[i+k+1][j-k-1] == c.state){
                        jump=true;
                    }
                    else flag=false;
                } else {
                    flag=false;
                }
            }
            else{
                arrive = new MNKCell(i+k, j-k, c.state);
                count++;
                antidiag.add(arrive);
            }
            k++;
        }



        if (count==B.K-1){
            if (isAntiDiagOpen(start,arrive,B)==2) {
                if (!jump) {
                    if (currentPlayerNode)return 250;
                    else return 5020;
                }
                else {
                    if (currentPlayerNode)return 80;
                    else return 1500;
                }
            }
            else if (isAntiDiagOpen(start,arrive,B)==1) {
                if (currentPlayerNode)return 100;
                else return 1500;
            }
            else {
                if (jump) {
                    if (currentPlayerNode)return 80;
                    else return 1500;
                }
            }
        }
        else if (count == B.K - 2) {
            if (isAntiDiagOpen(start, arrive,B) == 2) {
                if (!jump) {
                    if (currentPlayerNode)return 150;
                    else return 1200;
                };
            }
        }
        return 0;
    }

    public int evaluate(MNKBoard B){
        int valuation=0;
        int CurrPlayer=0;
        int OppPlayer=0;
        int MaxPlayerValue=0;
        int MinPlayerValue=0;

        if (B.gameState()==MNKGameState.WINP1) {
            return 1000000;
        }

        else if (B.gameState()==MNKGameState.WINP2) {
            return -1000000;
        }
        else if (B.gameState()==MNKGameState.DRAW) {
            return 0;
        }

        MNKCell[] MARKED = B.getMarkedCells();

        for (MNKCell c : MARKED) {
            currentPlayerCell=(c.state == MNKCellState.P1 && B.currentPlayer()==0) ||(c.state==MNKCellState.P2 && B.currentPlayer()==1);

            if (!hor.contains(c)) {
                valuation = evaluateHorThreat(c, B);
                if  (currentPlayerCell) CurrPlayer += valuation;
                else OppPlayer += valuation;
            }
            if (!ver.contains(c)) {
                valuation = evaluateVerThreat(c, B);
                if  (currentPlayerCell) CurrPlayer += valuation;
                else OppPlayer += valuation;
            }
            if (!diag.contains(c)) {
                valuation = evaluateDiagThreat(c, B);
                if  (currentPlayerCell) CurrPlayer += valuation;
                else OppPlayer += valuation;
            }
            if (!antidiag.contains(c)) {
                valuation = evaluateAntiDiagThreat(c, B);
                if  (currentPlayerCell) CurrPlayer += valuation;
                else OppPlayer += valuation;
            }
        }

        hor.clear(); ver.clear(); diag.clear(); antidiag.clear();

        if (CurrPlayer==0 && OppPlayer==0){
            for (MNKCell c : MARKED){
                valuation=checkBis(c,B);
                if (c.state == MNKCellState.P1) MaxPlayerValue += valuation;
                else MinPlayerValue -= valuation;
            }
        }

        return (CurrPlayer==0 && OppPlayer==0) ? CurrPlayer-OppPlayer : MaxPlayerValue+MinPlayerValue;
    }
}
