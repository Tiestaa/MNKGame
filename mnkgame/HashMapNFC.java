package mnkgame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class HashMapNFC{
    private final HashMap<Integer,NFCell> NFC;

    public HashMapNFC(int M,int N){
        String totaln = Integer.toString(M-1);
        String totalm = Integer.toString(N-1);
        String totalmn = totaln + totalm;
        NFC = new HashMap<Integer,NFCell>( (int)(Integer.parseInt(totalmn)*1.25));        //inserire dimensinoe minima attraverso totalmn
    }

    private int stringConversion(MNKCell d){
        String istring = Integer.toString(d.i);
        String jstring = Integer.toString(d.j);
        String K = istring + jstring;
        return Integer.parseInt(K);
    }

    public boolean contains(MNKCell d){
        return NFC.containsValue(d);
    }

    private void add(MNKCell d, int count){
        NFC.put(stringConversion(d),new NFCell(d.i,d.j,count));
    }
    private void delete(NFCell d){
        NFC.remove(stringConversion(d),d);
    }

    public NFCell[] getArray(){
        return (NFC.values()).toArray(new NFCell[0]); // returns an array of values
    }

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
                    MNKCell tmp=new MNKCell(d.i-1,d.j-1);
                    NFCell c = NFC.get(stringConversion(tmp));
                    if (c!=null) c.increaseCount();
                    else add(tmp,numberNFC(tmp,B)); 
                }
            }
            if (d.j < B.N - 1) {
                if (B.cellState(d.i - 1, d.j + 1) == MNKCellState.FREE) {       //altodx
                    MNKCell tmp=new MNKCell(d.i-1,d.j+1);
                    NFCell c = NFC.get(stringConversion(tmp));
                    if (c!=null) c.increaseCount();
                    else add(tmp,numberNFC(tmp,B)); 
                }
            }
            if (B.cellState(d.i - 1, d.j) == MNKCellState.FREE) {                   //alto
                MNKCell tmp=new MNKCell(d.i-1,d.j);
                NFCell c = NFC.get(stringConversion(tmp));
                if (c!=null) c.increaseCount();
                else add(tmp,numberNFC(tmp,B)); 
            }
        }

        if (d.i < B.M - 1) {
            if (d.j > 0) {
                if (B.cellState(d.i + 1, d.j - 1) == MNKCellState.FREE) {       //bassosx
                    MNKCell tmp=new MNKCell(d.i+1,d.j-1);
                    NFCell c = NFC.get(stringConversion(tmp));
                    if (c!=null) c.increaseCount();
                    else add(tmp,numberNFC(tmp,B)); 
                }
            }
            if (d.j < B.N - 1) {
                if (B.cellState(d.i + 1, d.j + 1) == MNKCellState.FREE) {       //bassodx
                    MNKCell tmp=new MNKCell(d.i+1,d.j+1);
                    NFCell c = NFC.get(stringConversion(tmp));
                    if (c!=null) c.increaseCount();
                    else add(tmp,numberNFC(tmp,B)); 
                }
            }
            if (B.cellState(d.i + 1, d.j) == MNKCellState.FREE) {                //basso
                MNKCell tmp=new MNKCell(d.i+1,d.j);
                NFCell c = NFC.get(stringConversion(tmp));
                if (c!=null) c.increaseCount();
                else add(tmp,numberNFC(tmp,B)); 
            }
        }
        if (d.j>0){
            if (B.cellState(d.i, d.j - 1) == MNKCellState.FREE) {                  //sx
                MNKCell tmp=new MNKCell(d.i,d.j-1);
                NFCell c = NFC.get(stringConversion(tmp));
                if (c!=null) c.increaseCount();
                else add(tmp,numberNFC(tmp,B)); 
            }
        }
        if (d.j < B.N-1){
            if (B.cellState(d.i, d.j + 1) == MNKCellState.FREE) {               //dx
                MNKCell tmp=new MNKCell(d.i,d.j+1);
                NFCell c = NFC.get(stringConversion(tmp));
                if (c!=null) c.increaseCount();
                else add(tmp,numberNFC(tmp,B)); 
            }
        }
        if(numberNFC(d, B)!=0) delete(new NFCell(d.i, d.j, 0));
    }

    public void deleteNFCplus(MNKCell d,MNKBoard B){
        //non modifico il count poichè il mark non lo varia
        if (d.i > 0) {
            if (d.j > 0) {
                if (B.cellState(d.i - 1, d.j - 1) == MNKCellState.FREE) {       //altosx
                    NFCell c = NFC.get(stringConversion(new MNKCell(d.i - 1, d.j - 1)));
                    if (c!=null){
                        c.decreaseCount();
                        if (c.getCount()==0) delete(c);
                    }
                }
            }
            if (d.j < B.N - 1) {
                if (B.cellState(d.i - 1, d.j + 1) == MNKCellState.FREE) {       //altodx
                    NFCell c = NFC.get(stringConversion(new MNKCell(d.i - 1, d.j + 1)));
                    if (c!=null){
                        c.decreaseCount();
                        if (c.getCount()==0) delete(c);
                    }
                }
            }
            if (B.cellState(d.i - 1, d.j) == MNKCellState.FREE) {           //alto
                NFCell c = NFC.get(stringConversion(new MNKCell(d.i - 1, d.j)));
                if (c!=null){
                    c.decreaseCount();
                    if (c.getCount()==0) delete(c);
                }
            }
        }
        if (d.i < B.M - 1) {
            if (d.j > 0) {
                if (B.cellState(d.i + 1, d.j - 1) == MNKCellState.FREE) {   //bassosx
                    NFCell c = NFC.get(stringConversion(new MNKCell(d.i + 1, d.j-1)));
                    if (c!=null){
                        c.decreaseCount();
                        if (c.getCount()==0) delete(c);
                    }
                }
            }
            if (d.j < B.N - 1) {
                if (B.cellState(d.i + 1, d.j + 1) == MNKCellState.FREE) {       //bassodx
                    NFCell c = NFC.get(stringConversion(new MNKCell(d.i + 1, d.j+1)));
                    if (c!=null){
                        c.decreaseCount();
                        if (c.getCount()==0) delete(c);
                    }
                }
            }
            if (B.cellState(d.i + 1, d.j) == MNKCellState.FREE) {       //basso
                NFCell c = NFC.get(stringConversion(new MNKCell(d.i + 1, d.j)));
                if (c!=null){
                    c.decreaseCount();
                    if (c.getCount()==0) delete(c);
                }
            }
        }
        if (d.j<B.N-1) {
            if (B.cellState(d.i, d.j + 1) == MNKCellState.FREE) {       //dx
                NFCell c = NFC.get(stringConversion(new MNKCell(d.i, d.j+1)));
                if (c!=null){
                    c.decreaseCount();
                    if (c.getCount()==0) delete(c);
                }
            }
        }
        if (d.j>0){
            if (B.cellState(d.i, d.j - 1) == MNKCellState.FREE) {       //sx
                NFCell c = NFC.get(stringConversion(new MNKCell(d.i, d.j-1)));
                if (c!=null){
                    c.decreaseCount();
                    if (c.getCount()==0) delete(c);
                }
            }
        }
       if(numberNFC(d, B)!=0) add(d, numberNFC(d, B));
    }
}

