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
        hor = new HashSet<>();
        ver = new HashSet<>();
        diag = new HashSet<>();
        antidiag = new HashSet<>();
    }

    private void addmode(char mode,MNKCell addCell){
        switch(mode){
            case('H'): hor.add(addCell); break;
            case('V'): ver.add(addCell); break;
            case('D'): diag.add(addCell); break;
            case('A'): antidiag.add(addCell); break;
        }
    }
    
    private int check(MNKCell cell, MNKBoard B){

        MNKCellState opponentstate = (cell.state == MNKCellState.P1) ? MNKCellState.P2 : MNKCellState.P1;       //funziona
        int i=cell.i;
        int j=cell.j;
        int cellCount=1;
        int totalValuation=0;
        int sum = 0;

        //Horizontal check
        int k=1;
        while(j-k >= 0 && k<B.K && B.B[i][j-k] != opponentstate){     // backward check
            if(B.B[i][j-k] == cell.state)totalValuation ++;
            cellCount++;k++;
        }
        
        if(j-k >= 0 && B.B[i][j-k] == opponentstate) totalValuation --;
        
        k=1;
        while(j+k <  B.N && k<B.K+1 && B.B[i][j+k] != opponentstate){        // forward check
            if(B.B[i][j+k] == cell.state) totalValuation ++;
            cellCount++;k++;
        }
        
        if(j+k <  B.N && B.B[i][j+k] == opponentstate) totalValuation --;
        
        if (cellCount >= B.K) totalValuation += 2;
        else totalValuation = totalValuation<0? totalValuation : 0;
        sum = totalValuation + sum;

        // Vertical check
        k=1;
        cellCount=1;
        while(i-k >= 0 && k<B.K+1 && B.B[i-k][j] != opponentstate){     // backward check
            if(B.B[i-k][j] == cell.state) totalValuation ++;
            cellCount++;k++;
        }
        
        if(i-k >= 0 && B.B[i-k][j] == opponentstate) totalValuation --;
        
        k=1;
        while(i+k <  B.M && k<B.K+1 && B.B[i+k][j] != opponentstate){     // forward check 
            if(B.B[i+k][j] == cell.state) totalValuation ++;
            cellCount++;k++;
        }
        
        if(i+k <  B.M && B.B[i+k][j] == opponentstate) totalValuation --;

        if (cellCount >= B.K) totalValuation += 2;
        else totalValuation = totalValuation<0? totalValuation : 0;
        sum = totalValuation + sum;

        //diagonal check
        k=1;
        cellCount=1;
        while(i-k >= 0 && j-k >= 0 && k<B.K+1 && B.B[i-k][j-k]  != opponentstate){     // backward check
            if(B.B[i-k][j-k] == cell.state) totalValuation ++;
            cellCount++;k++;
        }
        if(i-k >= 0 && j-k >= 0 && B.B[i-k][j-k] == opponentstate) totalValuation --;
        
        k=1;
        while(i+k <  B.M && k<B.K+1 && j+k <  B.N && B.B[i+k][j+k] != opponentstate){   // forward check
            if(B.B[i+k][j+k] == cell.state) totalValuation ++;
            cellCount++;k++;
        }
        
        if(i+k <  B.M && j+k <  B.N && B.B[i+k][j+k] == opponentstate) totalValuation --;
        
        if (cellCount >= B.K) totalValuation += 2;
        else totalValuation = totalValuation<0 ? totalValuation : 0;
        sum = totalValuation + sum;

        //antidiagonale check
        k=1;
        cellCount=1;
        while(i-k >= 0 && j+k < B.N && k<B.K+1 && B.B[i-k][j+k] != opponentstate){     // backward check
            if(B.B[i-k][j+k] == cell.state) totalValuation ++;
            cellCount++;k++;
        }
        
        if(i-k >= 0 && j+k < B.N &&  B.B[i-k][j+k] == opponentstate) totalValuation --;
        
        k=1;
        while(i+k <  B.M && j-k >= 0 && k<B.K+1 && B.B[i+k][j-k] != opponentstate){   // forward check
            if(B.B[i+k][j-k] == cell.state) totalValuation ++;
            cellCount++;k++;
        }

        if(i+k <  B.M && j-k >= 0 &&  B.B[i+k][j-k] == opponentstate) totalValuation --;
    
        if (cellCount >= B.K) totalValuation += 2;
        else totalValuation = totalValuation<0? totalValuation : 0;
        sum = totalValuation + sum;

        return sum;
    }

    private int isOpen(MNKCell start, MNKCell arrive, MNKBoard B,char mode){
        int hole = 0;
        boolean border1 = false, border2 = false;
        int x = 0,y = 0,w = 0,z = 0;
        switch (mode){
            case ('A'):
                border1 = start.j+1 < B.N; border2 = arrive.j-1 >= 0;
                x = start.i-1; y = start.j+1;
                w = arrive.i+1; z = arrive.j-1;
                break;
            
            case ('D'):
                border1 = start.j-1 >= 0; border2 = arrive.j+1 < B.N;
                x = start.i-1; y = start.j-1;
                w = arrive.i+1; z = arrive.j+1;
                break;
            
            case ('V'):
                border1 = border2 = true;
                x = start.i-1; y = start.j;
                w = arrive.i+1; z = arrive.j;
                break;
            
            case ('H'):
                border1 = border2 = true;
                x = start.i; y = start.j-1;
                w = arrive.i; z = arrive.j+1;
                break; 
        }
        
        if (start.i - 1 >= 0 && border1 && B.B[x][y] == MNKCellState.FREE)
            hole++;

        if (arrive.i + 1 < B.M && border2 && B.B[w][z] == MNKCellState.FREE)
            hole++;

        return hole;
    }

    private int evaluateThreat(MNKCell c, MNKBoard B, char mode) {
        int i = c.i;
        int j = c.j;
        int count = 1;            // celle "consecutive"
        MNKCellState opponentstate= c.state == MNKCellState.P1 ? MNKCellState.P2: MNKCellState.P1;
        int k = 1;
        boolean jump = false;     // mi tiene traccia di un salto di una cella vuota
        boolean flag = true;      // in caso di salto già avvenuto, il while si ferma
        boolean currentPlayerNode = (B.currentPlayer()==0 && c.state==MNKCellState.P1)||(B.currentPlayer() == 1 && c.state==MNKCellState.P2);

        /*start e arrivo per verificare successivamente che celle ci sono alle estremità,
          partono entrambe dalla stessa cella*/
        MNKCell start = c;
        MNKCell arrive = c;

        int x = 0,y = 0,ys = 0,xs = 0;
        boolean cond0 = false, cond1 = false, cond2 = false , cond3 = false;
        switch(mode){
            case('H'):
                x = i; y = j - k;  
                xs = i ; ys = j + k; 
                cond0 = j - k >= 0;
                cond1 = y - 1 >= 0 && B.B[x][y - 1] == c.state;
                cond2 = j + k < B.N ; 
                cond3 = ys + 1 < B.N && B.B[xs][ys + 1] == c.state;
                break;
            case('V'):
                x = i - k; y = j;  
                xs = i+k; ys = j; 
                cond0 = i-k >=0;
                cond1 = i-k-1 >=0 && B.B[i-k-1][j]==c.state;
                cond2 =  i+k < B.M; 
                cond3 = i+k+1 < B.M && B.B[i+k+1][j]==c.state;
                break;
            case('D'):
                x = i - k; y = j - k;  
                xs = i+k; ys = j + k;
                cond0 = i-k>=0 && j-k>=0;
                cond1 = i - k - 1 >= 0 && j - k - 1 >= 0 && B.B[i - k - 1][j - k - 1] == c.state;
                cond2 =  i+k < B.M && j+k < B.N; 
                cond3 = i+k+1 < B.M && j+k+1 < B.N && B.B[i+k+1][j+k+1] == c.state;
                break;
            case('A'):
                x = i - k; y = j + k;  
                xs = i+k; ys = j - k; 
                cond0 = i-k >= 0 && j+k < B.N;
                cond1 = i-k-1 >=0 && j+k+1 < B.N && B.B[i-k-1][j+k+1] == c.state;
                cond2 = i+k <B.M && j-k >= 0; 
                cond3 = i+k+1 < B.M && j-k-1>=0 && B.B[i+k+1][j-k-1] == c.state;
            
        }

        //backward
        while (flag && cond0 && k < B.K && B.B[x][y] != opponentstate) {
            if (B.B[x][y] == MNKCellState.FREE) {
                if (!jump) {
                    if (cond1)                                   // if(non ha saltato): vado a vedere la cella precedente, if (è occupata dallo stesso player): effettuo il salto
                        jump = true;
                    else 
                        flag = false;                            // if(trovo due celle consecutive vuote): si ferma il while con jump = false 
                } 
                else 
                    flag = false;                                // if(ha già saltato e trova un'altra cella vuota): si ferma
            } else {
                start = new MNKCell(x, y, c.state);              // start = nuova cella trovata
                count++;
                addmode(mode, start);                            // aggiungo la nuova cella in modo da non contare più volte la stessa minaccia
            }
            k++;
        }

        //forward (simmetrico al backward)
        flag = true; k = 1;
        while (flag && cond2 && k < B.K && B.B[xs][ys] != opponentstate) {
            if (B.B[xs][ys] == MNKCellState.FREE) {
                if (!jump) {
                    if (cond3) 
                        jump = true;
                    else 
                        flag = false;
                }
                else 
                    flag = false;
            } else {
                arrive = new MNKCell(xs, ys, c.state);           //modifico l'arrive
                count++;
                addmode(mode, arrive);
            }
            k++;
        }

        /* controllo di che tipo di minaccia si tratta */
        if (count == B.K-1){                                
            if (isOpen(start,arrive,B,mode) == 2) {     // k-1 halfopen: allineati, estremi liberi
                if (!jump) {                            // con salto
                    if (currentPlayerNode) return 250; 
                    else return 5020;
                } else {                                
                    if (currentPlayerNode) return 80;   // senza salto
                    else return 1500;
                }
            }
            else if (isOpen(start,arrive,B,mode)==1) {  // k-1 halfopen: allineati, un estremo libero
                if (currentPlayerNode) return 80;       
                else return 1500;
            }
            else {                                      // allineati, estremi non liberi, salto
                if (jump) {                 
                    if (currentPlayerNode) return 80;
                    else return 1500;
                }
            }
        }
        else if (count == B.K - 2) {
            if (isOpen(start, arrive,B,mode) == 2) {    // k-2 open
                if (!jump) {
                    if (currentPlayerNode) return 150;
                    else return 1200;
                }
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
                valuation = evaluateThreat(c,B,'H');
                if  (currentPlayerCell) CurrPlayer += valuation;
                else OppPlayer += valuation;
            }
            if (!ver.contains(c)) {
                valuation = evaluateThreat(c,B,'V');
                if  (currentPlayerCell) CurrPlayer += valuation;
                else OppPlayer += valuation;
            }
            if (!diag.contains(c)) {
                valuation = evaluateThreat(c,B, 'D');
                if  (currentPlayerCell) CurrPlayer += valuation;
                else OppPlayer += valuation;
            }
            if (!antidiag.contains(c)) {
                valuation = evaluateThreat(c,B,'A');
                if  (currentPlayerCell) CurrPlayer += valuation;
                else OppPlayer += valuation;
            }
        }

        hor.clear(); ver.clear(); diag.clear(); antidiag.clear();

        if (CurrPlayer==0 && OppPlayer==0){
            for (MNKCell c : MARKED){
                valuation= check(c,B);
                if (c.state == MNKCellState.P1) MaxPlayerValue = MaxPlayerValue +  valuation;
                else MinPlayerValue = MinPlayerValue - valuation;
            }
        }

        int toReturn = 0;
        if (CurrPlayer!=0 && OppPlayer!=0) toReturn = (CurrPlayer-OppPlayer);
        else toReturn = (MaxPlayerValue+MinPlayerValue);
        return toReturn;
    
    }
}
