package mnkgame;
//prova git
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ArrayNFC{
    private final NFCell[] NFC;

    public ArrayNFC(int M,int N){
        NFC = new NFCell[(int) Math.ceil(M*N*2.7)];
        for (int i = 0; i<(int) Math.ceil(M*N*2.7); i++)
            NFC[i] = null;
    }

    private int stringConversion(MNKCell d){
        if(d != null){
            String istring = Integer.toString(d.i);
            String jstring = Integer.toString(d.j);
            String ijstring = istring + jstring;
            return Integer.parseInt(ijstring);
        }
        return -1;
    }

    public boolean contains(MNKCell d){
        if(d != null){
            int ijstr = stringConversion(d);
            return (NFC[ijstr] != null);
        }
        return false;
    }

    private void add(MNKCell d, int count){
            NFC[stringConversion(d)] = new NFCell(d.i,d.j,count);
    }
    private void delete(MNKCell d){
        NFC[stringConversion(d)] = null;
    }

    public NFCell[] getArray(){
        return NFC;
    }

    public NFCell[] getValidArray(){
        ArrayList<NFCell> ARRAY=new ArrayList<>(NFC.length);
        for (NFCell c:NFC){
            if (contains(c) && c.getCount()!=0) ARRAY.add(c);
        }
        return ARRAY.toArray(new NFCell[0]);
    }


    /*
    public MNKCell[] getAscendingArray(){
        Arrays.sort(NFC);
        return NFC;
    }

    public MNKCell[] getDescendingArray(){
        Arrays.sort(NFC, Collections.reverseOrder());
        return NFC;
    }
     */
    private int numberNFC(MNKCell d,MNKBoard B){
        int count=0;
        if (d.i > 0) {
            if (d.j > 0) if (B.B[d.i-1][d.j-1]!=MNKCellState.FREE) count++;     //altosx
            if (d.j < B.N - 1) if (B.B[d.i-1][d.j+1]!=MNKCellState.FREE) count++;     //altodx

            if (B.B[d.i-1][d.j]!=MNKCellState.FREE) count++;           //alto
        }
        if (d.i < B.M - 1) {
            if (d.j > 0) if (B.B[d.i+1][d.j-1]!=MNKCellState.FREE) count++;     //bassosx
            if (d.j < B.N - 1) if (B.B[d.i+1][d.j+1]!=MNKCellState.FREE) count++;     //bassodx

            if (B.B[d.i+1][d.j]!=MNKCellState.FREE) count++;           //basso
        }
        if (d.j>0) if (B.B[d.i][d.j-1]!=MNKCellState.FREE) count++;       //sx
        if (d.j<B.N-1) if (B.B[d.i][d.j+1]!=MNKCellState.FREE) count++;       //dx
        return count;
    }

    public void fillNFCplus(MNKCell d,MNKBoard B){
        if (d.i > 0) {
            if (d.j > 0) {
                if (B.cellState(d.i - 1, d.j - 1) == MNKCellState.FREE) {       //altosx
                    MNKCell c= new MNKCell(d.i-1,d.j-1);
                    if (NFC[stringConversion(c)]==null) add(c,numberNFC(c,B));
                    else NFC[stringConversion(c)].increaseCount();
                }
            }
            if (d.j < B.N - 1) {
                if (B.cellState(d.i - 1, d.j + 1) == MNKCellState.FREE) {       //altodx
                    MNKCell c= new MNKCell(d.i-1,d.j+1);
                    if (NFC[stringConversion(c)]==null) add(c,numberNFC(c,B));
                    else NFC[stringConversion(c)].increaseCount();
                }
            }
            if (B.cellState(d.i - 1, d.j) == MNKCellState.FREE) {                   //alto
                MNKCell c = new MNKCell(d.i - 1, d.j);
                if (NFC[stringConversion(c)] == null) add(c, numberNFC(c, B));
                else NFC[stringConversion(c)].increaseCount();
            }
        }

        if (d.i < B.M - 1) {
            if (d.j > 0) {
                if (B.cellState(d.i + 1, d.j - 1) == MNKCellState.FREE) {       //bassosx
                    MNKCell c= new MNKCell(d.i+1,d.j-1);
                    if (NFC[stringConversion(c)]==null) add(c,numberNFC(c,B));
                    else NFC[stringConversion(c)].increaseCount();
                }
            }
            if (d.j < B.N - 1) {
                if (B.cellState(d.i + 1, d.j + 1) == MNKCellState.FREE) {       //bassodx
                    MNKCell c= new MNKCell(d.i+1,d.j+1);
                    if (NFC[stringConversion(c)]==null) add(c,numberNFC(c,B));
                    else NFC[stringConversion(c)].increaseCount();
                }
            }
            if (B.cellState(d.i + 1, d.j) == MNKCellState.FREE) {                //basso
                MNKCell c= new MNKCell(d.i+1,d.j);
                if (NFC[stringConversion(c)]==null) add(c,numberNFC(c,B));
                else NFC[stringConversion(c)].increaseCount();
            }
        }
        if (d.j>0){
            if (B.cellState(d.i, d.j - 1) == MNKCellState.FREE) {                  //sx
                MNKCell c= new MNKCell(d.i,d.j-1);
                if (NFC[stringConversion(c)]==null) add(c,numberNFC(c,B));
                else NFC[stringConversion(c)].increaseCount();
            }
        }
        if (d.j < B.N-1){
            if (B.cellState(d.i, d.j + 1) == MNKCellState.FREE) {               //dx
                MNKCell c= new MNKCell(d.i,d.j+1);
                if (NFC[stringConversion(c)]==null) add(c,numberNFC(c,B));
                else NFC[stringConversion(c)].increaseCount();
            }
        }
        if(numberNFC(d, B)!=0) delete(d);
    }

    public void deleteNFCplus(MNKCell d,MNKBoard B){
        //non modifico il count poichè il mark non lo varia
        if (d.i > 0) {
            if (d.j > 0) {
                if (B.cellState(d.i - 1, d.j - 1) == MNKCellState.FREE) {       //altosx
                    MNKCell tmp = new MNKCell(d.i - 1, d.j - 1);
                    if (contains(tmp)) {
                        NFC[stringConversion(tmp)].decreaseCount();
                        if (NFC[stringConversion(tmp)].getCount() == 0)
                            delete(tmp);
                    }
                }
            }
            if (d.j < B.N - 1) {
                if (B.cellState(d.i - 1, d.j + 1) == MNKCellState.FREE) {       //altodx
                    MNKCell tmp = new MNKCell(d.i - 1, d.j + 1);
                    if (contains(tmp)) {
                        NFC[stringConversion(tmp)].decreaseCount();
                        if (NFC[stringConversion(tmp)].getCount() == 0)
                            delete(tmp);
                    }
                }
            }
            if (B.cellState(d.i - 1, d.j) == MNKCellState.FREE) {           //alto
                MNKCell tmp=new MNKCell(d.i-1,d.j);
                if (contains(tmp)) {
                    NFC[stringConversion(tmp)].decreaseCount();
                    if (NFC[stringConversion(tmp)].getCount() == 0)
                        delete(tmp);
                }
            }
        }
        if (d.i < B.M - 1) {
            if (d.j > 0) {
                if (B.cellState(d.i + 1, d.j - 1) == MNKCellState.FREE) {   //bassosx
                    MNKCell tmp = new MNKCell(d.i + 1, d.j - 1);
                    if (contains(tmp)) {
                        NFC[stringConversion(tmp)].decreaseCount();
                        if (NFC[stringConversion(tmp)].getCount() == 0)
                            delete(tmp);
                    }
                }
            }
            if (d.j < B.N - 1) {
                if (B.cellState(d.i + 1, d.j + 1) == MNKCellState.FREE) {       //bassodx
                    MNKCell tmp = new MNKCell(d.i + 1, d.j + 1);
                    if (contains(tmp)) {
                        NFC[stringConversion(tmp)].decreaseCount();
                        if (NFC[stringConversion(tmp)].getCount() == 0)
                            delete(tmp);
                    }
                }
            }
            if (B.cellState(d.i + 1, d.j) == MNKCellState.FREE) {       //basso
                MNKCell tmp = new MNKCell(d.i + 1, d.j);
                if (contains(tmp)) {
                    NFC[stringConversion(tmp)].decreaseCount();
                    if (NFC[stringConversion(tmp)].getCount() == 0)
                        delete(tmp);
                }
            }
        }
        if (d.j<B.N-1) {
            if (B.cellState(d.i, d.j + 1) == MNKCellState.FREE) {       //dx
                MNKCell tmp = new MNKCell(d.i, d.j + 1);
                if (contains(tmp)) {
                    NFC[stringConversion(tmp)].decreaseCount();
                    if (NFC[stringConversion(tmp)].getCount() == 0)
                        delete(tmp);
                }
            }
        }
        if (d.j>0){
            if (B.cellState(d.i, d.j - 1) == MNKCellState.FREE) {       //sx
                MNKCell tmp = new MNKCell(d.i, d.j - 1);
                if (contains(tmp)) {
                    NFC[stringConversion(tmp)].decreaseCount();
                    if (NFC[stringConversion(tmp)].getCount() == 0)
                        delete(tmp);
                }
            }
        }
       if(numberNFC(d, B)!=0) add(d, numberNFC(d, B));
    }

    public void stampArray(NFCell[] NFC){
        System.out.println("celle in NFC:");
        for(NFCell d : NFC){
            if( contains(d) ) System.out.println(d.i+"-"+d.j+" value: "+ d.getValuation());
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

}