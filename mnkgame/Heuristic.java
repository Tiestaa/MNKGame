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
    private int check(MNKCell cell, MNKBoard B){

        MNKCellState opponentstate= cell.state == MNKCellState.P1 ?MNKCellState.P2: MNKCellState.P1;
        int i=cell.i;
        int j=cell.j;
        int sum=0;
        int totalcell=1;
        int totalmycell=1;
        int aligned=1;
        boolean canbealigned=true;
        //per impilare e vincere il massimo che può mancare è un blocco, quindi nel caso

        // Horizontal check
        int k=1;
        while(j-k >= 0 && k<B.K && B.B[i][j-k] != opponentstate){     // backward check
            if(B.B[i][j-k]==cell.state) {
                if (canbealigned) aligned++;
                else totalmycell++;
            }
            else if (B.B[i][j-k]==MNKCellState.FREE) canbealigned=false;
            totalcell++;k++;
        }

        canbealigned=true;
        k=1;
        while(j+k <  B.N && k<B.K &&B.B[i][j+k] != opponentstate){   // forward check
            if(B.B[i][j+k]==cell.state) {
                if (canbealigned) aligned++;
                else totalmycell++;
            }
            else if (B.B[i][j+k]==MNKCellState.FREE) canbealigned=false;
            totalcell++;k++;
        }

        /*
        sum += totalcell;
        sum += totalmycell*3;
        if(totalcell >= B.K) {
            sum += (aligned*7);
            sum+=20;
        }
         */
        /*
        sum += totalmycell;
        if(totalcell == B.K) {
            sum += (aligned*4);
            sum+=(totalcell+4);
        }
        if (totalcell>B.K){
            sum+=aligned*8;
            sum+=(totalcell+10);
        }

        if (totalcell<B.K) sum+= totalcell;

         */

        sum+= valuecheck(totalcell,aligned,totalmycell,B.K);

        //else if (totalcell>B.K) sum+=9;

        // Vertical check;
        canbealigned=true;
        aligned=1;
        totalcell=1;
        totalmycell=1;
        k=1;
        while(i-k >= 0 && k<B.K&&B.B[i-k][j] != opponentstate){     // backward check
            if(B.B[i-k][j]==cell.state) {
                if (canbealigned) aligned++;
                else totalmycell++;
            }
            else if (B.B[i-k][j]==MNKCellState.FREE) canbealigned=false;
            totalcell++;k++;
        }

        canbealigned=true;
        k=1;
        while(i+k <  B.M && k<B.K && B.B[i+k][j] != opponentstate){     // forward check
            if(B.B[i+k][j]==cell.state) {
                if (canbealigned) aligned++;
                else totalmycell++;
            }
            else if (B.B[i+k][j]==MNKCellState.FREE) canbealigned=false;
            totalcell++;k++;
        }

        sum+= valuecheck(totalcell,aligned,totalmycell,B.K);
        //else if (totalcell>B.K) sum+=9;

        // Diagonal check
        canbealigned=true;
        aligned=1;
        totalcell=1;
        totalmycell=1;
        k=1;
        while(i-k >= 0 && j-k >= 0 && k<B.K && B.B[i-k][j-k]  != opponentstate){     // backward check
            if(B.B[i-k][j-k]==cell.state){
                if (canbealigned) aligned++;
                else totalmycell++;
            }
            else if (B.B[i-k][j-k]==MNKCellState.FREE) canbealigned=false;
            totalcell++;k++;
        }

        canbealigned=true;
        k=1;
        while(i+k <  B.M && k<B.K && j+k <  B.N && B.B[i+k][j+k] != opponentstate){   // forward check
            if(B.B[i+k][j+k]==cell.state) {
                if (canbealigned) aligned++;
                else totalmycell++;
            }
            else if (B.B[i+k][j+k]==MNKCellState.FREE) canbealigned=false;
            totalcell++;k++;
        }

        sum+= valuecheck(totalcell,aligned,totalmycell,B.K);

        // Anti-diagonal check
        canbealigned=true;
        aligned=1;
        totalcell=1;
        totalmycell=1;
        k=1;
        while(i-k >= 0 && j+k < B.N && k<B.K && B.B[i-k][j+k] != opponentstate){     // backward check
            if(B.B[i-k][j+k]==cell.state) {
                if (canbealigned) aligned++;
                else totalmycell++;
            }
            else if (B.B[i-k][j+k]==MNKCellState.FREE) canbealigned=false;
            totalcell++;k++;
        }

        canbealigned=true;
        k=1;
        while(i+k <  B.M && j-k >= 0 && k<B.K && B.B[i+k][j-k] != opponentstate){   // forward check
            if(B.B[i+k][j-k]==cell.state) {
                if (canbealigned) aligned++;
                else totalmycell++;
            }
            else if (B.B[i+k][j-k]==MNKCellState.FREE) canbealigned=false;
            totalcell++;k++;
        }
        /*
        if(totalcell == B.K) {
            if (currentPlayerCell) {
                sum += (aligned * 1.5);
                sum+=totalmycell*2;
            } else{
                sum += (aligned * 6);
                sum += totalmycell*5;
            }
        }
        if (totalcell>B.K){
            if (currentPlayerCell) {
                sum += aligned * 4;
                sum+=totalmycell*4;
            }
            else {
                sum+=aligned*13;
                sum+=totalmycell*8;
            }
        }
        sum+=totalcell/6;


         */

        sum+= valuecheck(totalcell,aligned,totalmycell,B.K);
        return sum;
    }

    private int valuecheck(int totalcell, int aligned, int totalmycell, int K){
        int sum=0;
        //1.
        /*

        if (totalcell > K){
            if (currentPlayerCell){
                sum+=totalmycell/2;
                sum+=totalcell;
            }
            else {
                sum+=totalmycell;
                sum+=totalcell*3;
            }
        }
        else if (totalcell == K) {
            if (currentPlayerCell) {
                sum += totalmycell / 50;
                sum += totalcell / 25;
            } else {
                sum += totalmycell / 25;
                sum += totalcell / 10;
            }
        }
        else {
            sum+=totalcell/100;
        }
         */

        //2.            MIGLIORE

        if (totalcell > K){

            sum+=totalcell*2;
        }
        else if (totalcell==K) sum+=totalcell;





        //3.
        /*
        if (totalcell > K){
            sum+=totalmycell;
            sum+=totalcell*2;
        }
        else if (totalcell==K) {
            sum += totalmycell/2;
            sum += totalcell;
        }


         */
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
    /*
    presa una cella c, valuta se esiste un threat a partire da quella cella. in base ai booleani sceglie quale threat controllare.
    Inoltre nella ricerca del threat la funzione prende un ArrayList in cui salva le celle già visitate durante la ricerca del threat.
    Questo per evitare di valutare due volte la stessa minaccia.
    Se player k-2>k-1
    se avversario k-1>k-2
     */

    //mio, quindi k-2>k-1, dunque faccio un controllo per fermarmi prima in caso
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
            if (B.B[i][j - k] == MNKCellState.FREE) {
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
      /*
                if (currentPlayerNode && j - k - 1 >= 0 && B.B[i][j - k - 1] == MNKCellState.FREE && count == B.K - 2 && isHorOpen(start, arrive,B) == 2 && !jump){
                    if (j - k - 2 >= 0 && B.B[i][j - k - 2] == c.state) hor.add(new MNKCell(i,j-k-2,c.state));
                    return 100;
                }


       */

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
                /*
                if (currentPlayerNode && j + k + 1 < B.N && B.B[i][j + k + 1] == MNKCellState.FREE && count == B.K - 2 && isHorOpen(start, arrive,B) == 2 && !jump){
                    if (j + k + 2 <B.N && B.B[i][j + k + 2] == c.state) hor.add(new MNKCell(i, j + k + 2, c.state));
                    return 100;
                }

                 */

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

    //SECONDA PROVA, MULT=10
    //TERZA PROVA, VALUE DELLA SECONDA, MULT=1, CURR E OPP (NON MAX E MIN)
    //QUARTA PROVA, VALUE DELLA PRIMA, MULT=1, CURR E OPP (NON MAX E MIN)
    /*  VALUE SECONDA:
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
            if (B.B[i][j - k] == MNKCellState.FREE) {
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

                if (currentPlayerNode && j - k - 1 >= 0 && B.B[i][j - k - 1] == MNKCellState.FREE && count == B.K - 2 && isHorOpen(start, arrive,B) == 2 && !jump){
                    if (j - k - 2 >= 0 && B.B[i][j - k - 2] == c.state) hor.add(new MNKCell(i,j-k-2,c.state));
                    return 100;
                }


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

                if (currentPlayerNode && j + k + 1 < B.N && B.B[i][j + k + 1] == MNKCellState.FREE && count == B.K - 2 && isHorOpen(start, arrive,B) == 2 && !jump){
                    if (j + k + 2 <B.N && B.B[i][j + k + 2] == c.state) hor.add(new MNKCell(i, j + k + 2, c.state));
                    return 100;
                }


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
                    else return 2000;
                }
            }
            else if (isHorOpen(start,arrive,B)==1) {
                //k-1 halfopen,allineati con un estremo libero senza/con un salto
                if (currentPlayerNode) return 80;
                else return 2000;
            }
            else {
                if (jump) {
                    //k-1 allineati con estremi non liberi ma con un salto
                    if (currentPlayerNode) return 80;
                    else return 2000;
                }
            }
        }

        else if (count == B.K - 2) {

            if (isHorOpen(start, arrive,B) == 2) {
                //k-2 open
                if (!jump) {
                    if (currentPlayerNode) return 100;
                    else return 1300;
                }
            }
        }
        return 0;
    }

     */
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
                /*
                if (currentPlayerNode&&i - k - 1 >= 0 && B.B[i - k - 1][j] != c.state && count == B.K - 2 && isVerOpen(start, arrive,B) == 2 && !jump){
                    if (i - k - 2 >= 0 && B.B[i - k - 2][j] == c.state) ver.add(new MNKCell(i - k - 2, j, c.state));
                    return 100;
                }

                 */


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
                /*
                if (currentPlayerNode&&i + k + 1 <B.M && B.B[i + k + 1][j] != c.state&&count == B.K - 2 && isVerOpen(start, arrive,B) == 2 && !jump){
                    if (i + k + 2 < B.M && B.B[i + k + 2][j] == c.state) ver.add(new MNKCell(i + k + 2, j, c.state));
                    return 100;
                }

                 */


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
                /*
                if (currentPlayerNode&&i - k - 1 >= 0 && j-k-1>=0 && B.B[i - k - 1][j - k - 1] != c.state&&count == B.K - 2 && isDiagOpen(start, arrive,B) == 2 && !jump) {
                    if(i - k - 2 >= 0 && j-k-2>=0 && B.B[i-k-2][j-k-2] == c.state) diag.add(new MNKCell(i-k-2, j-k-2 , c.state));
                    return 100;
                }

                 */
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
                /*
                if (currentPlayerNode&&i + k + 1 <B.M && j+k+1<B.N && B.B[i+k+1][j+k+1] != c.state&&count == B.K - 2 && isDiagOpen(start, arrive,B) == 2 && !jump) {
                    if (i + k + 2 < B.M && j + k + 2 < B.N && B.B[i + k + 2][j + k + 2] == c.state) diag.add(new MNKCell(i + k + 2, j + k + 2, c.state));
                    return 100;
                }

                 */
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
                /*
                if (currentPlayerNode&&i-k-1>= 0 && j+k+1<B.N && B.B[i-k-1][j+k+1] != c.state&&count == B.K - 2 && isAntiDiagOpen(start, arrive,B) == 2 && !jump){
                    if (i-k-2 >= 0 && j+k+2<B.N && B.B[i-k-2][j+k+2] == c.state) antidiag.add(new MNKCell(i-k-2, j+k+2 , c.state));
                    return 100;
                }

                 */
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
                /*
                if (currentPlayerNode && i+k+1 <B.M && j-k-1>=0 && B.B[i+k+1][j - k - 1] != c.state && count == B.K - 2 && isAntiDiagOpen(start, arrive,B) == 2 && !jump){
                    if (i+k+2 <B.M && j-k-2>=0 && B.B[i+k +2][j-k-2] == c.state) antidiag.add(new MNKCell(i+k +2, j-k-2 , c.state));
                    return 100;
                }

                 */
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
        int MaxPlayerValue=0;
        int MinPlayerValue=0;
        int valuation=0;
        int MaxPlayerCheckValue=0;
        int MinPlayerCheckValue=0;
        int CurrPlayer=0;
        int OppPlayer=0;

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
        //creo un arraylist che mi tiene traccia di ogni cella visitata in ogni direzione

        for (MNKCell c : MARKED) {
            currentPlayerCell=(c.state == MNKCellState.P1 && B.currentPlayer()==0) ||(c.state==MNKCellState.P2 && B.currentPlayer()==1);
            /*
           vado a cercare il threat soltanto se la cella non è stata visitata. Gli ADD sono nella valutazione del threat
            Non aggiungo la cella c negli arraylist poichè è superfluo, in quanto una volta visitato con il for, non verrà
            visitate più.
             */


            valuation=check(c,B); //currentPlayerCell ? check(c,B):check(c,B)*2;
            //System.out.println(valuation);

            if (c.state==MNKCellState.P1){
                MaxPlayerCheckValue+=valuation;
            }
            else{
                MinPlayerCheckValue-=valuation;
            }


            /*

            if (currentPlayerCell)CurrPlayer+=valuation;
            else OppPlayer+=valuation;


             */
            int mult=1;
            if (!hor.contains(c)) {
                valuation = evaluateHorThreat(c, B) * mult;


                if  (currentPlayerCell) CurrPlayer += valuation;
                else OppPlayer += valuation;

                /*
                if (c.state==MNKCellState.P1){
                    MaxPlayerValue+=valuation;
                }
                else MinPlayerValue -=valuation;
                 */

                /*
                if (currentPlayerCell) {
                    if (c.state == MNKCellState.P1) {
                        MaxPlayerValue += valuation;
                    } else MinPlayerValue -= valuation;
                } else {
                    if (c.state == MNKCellState.P1) {
                        MaxPlayerValue += valuation;
                    } else MinPlayerValue -= valuation;
                }
                 */
            }
            if (!ver.contains(c)) {
                valuation = evaluateVerThreat(c, B) * mult;
                if  (currentPlayerCell) CurrPlayer += valuation;
                else OppPlayer += valuation;
            }
            if (!diag.contains(c)) {
                valuation = evaluateDiagThreat(c, B)*mult;
                if  (currentPlayerCell) CurrPlayer += valuation;
                else OppPlayer += valuation;
            }
            if (!antidiag.contains(c)) {
                valuation = evaluateAntiDiagThreat(c, B)*mult;
                if  (currentPlayerCell) CurrPlayer += valuation;
                else OppPlayer += valuation;
            }
        }

        hor.clear(); ver.clear(); diag.clear(); antidiag.clear();

        return (CurrPlayer==0 && OppPlayer==0) ? MaxPlayerCheckValue+MinPlayerCheckValue : CurrPlayer-OppPlayer;
    }
}
