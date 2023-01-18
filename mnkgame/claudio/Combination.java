package mnkgame.claudio;

import mnkgame.MNKCell;

public class Combination {

    protected int row;
    protected int col;
    protected Direction direction;

    protected int len;

    public Combination(int row, int col, Direction direction, int len) {
        this.row = row;
        this.col = col;
        this.direction = direction;
        this.len = len;
    }

    /**
     * Descrizione: Occorre creare una funzione che date le coordinate di una cella ritorna un booleano che indica la presenza di una cella all'interno della combinazione
     *
     * @param cell: Cella da controllare
     * @return boolean
     * @implNote Per le combinazioni oblique si verifica che si trovino su una diagonale quadrata dopo aver eliminato le coordinate della cella iniziale e si effettuano i controlli su questa.
     */
    public boolean isCellContained(MNKCell cell) {
        int cellRow = cell.i;
        int cellCol = cell.j;

        switch (direction) {
            case HORIZONTAL:
                if (cellRow == row && (col <= cellCol && col + len > cellCol)) {
                    return true;
                }
                break;
            case VERTICAL:
                if (cellCol == col && (row <= cellRow && row + len > cellRow)) {
                    return true;
                }
                break;
            case OBLIQUE_DESC:
                if (row <= cellRow) {
                    cellRow = cellRow - row;
                    cellCol = cellCol - col;
                    //Ora occorre verificare che le due coordinate sono uguali
                    if (cellCol == cellRow && cellCol >= 0) {
                        //Occorre quindi verificare che le coordinare sono minori di len.
                        if (cellCol < len) {
                            return true;
                        }
                    }
                }
                break;
            case OBLIQUE_ASC:
                if (row >= cellRow) {
                    cellRow = cellRow - row;
                    cellCol = cellCol - col;
                    cellRow = Math.abs(cellRow);
                    //Analogo a sopra
                    if (cellCol == cellRow && cellCol >= 0) {
                        if (cellRow < len) {
                            return true;
                        }
                    }
                }
                break;
        }
        return false;
    }

}
