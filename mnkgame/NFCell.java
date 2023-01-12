package mnkgame;

public class NFCell extends MNKCell implements Comparable<NFCell> {

    private int count;   // ci tiene traccia di quante volte è nell'nfc per non essere eliminato
    private int valuation;

    public NFCell(int i, int j, int count) {
        super(i, j);
        this.count=count;

    }

    public int getValuation() {
        return valuation;
    }

    public int getCount() {
        return count;
    }

    public void increaseCount(){
        count++;
    }

    public void decreaseCount(){
        count--;
    }
    public void setValuation(int valuation) {
        this.valuation = valuation;
    }

    @Override
    public int compareTo(NFCell o) {
        return (Integer.compare(this.getValuation(), o.getValuation()));
    }

}
