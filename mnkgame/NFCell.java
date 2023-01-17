package mnkgame;

public class NFCell extends MNKCell{

    private int count;      // tiene traccia di quante volte è nell'nfc per non essere eliminato

    public NFCell(int i, int j, int count) {
        super(i, j);
        this.count=count;

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


}
