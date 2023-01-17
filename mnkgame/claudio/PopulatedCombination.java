package mnkgame.claudio;

import mnkgame.MNKCell;
import mnkgame.MNKCellState;

import java.util.LinkedList;

public class PopulatedCombination extends Combination {

    private final MNKCellState[] moves;
    private final boolean isFirstPlayer;

    private boolean p1;
    private boolean p2;
    private int cellsRemaining;

    public PopulatedCombination(Combination combination, int len, boolean isFirstPlayer) {
        super(combination.row, combination.col, combination.direction, len);
        moves = new MNKCellState[len];
        for (int q = 0; q < len; q++) {
            moves[q] = MNKCellState.FREE;
        }
        cellsRemaining = len;
        this.isFirstPlayer = isFirstPlayer;
    }

    public PopulatedCombination(PopulatedCombination populatedCombination) {
        super(populatedCombination.row, populatedCombination.col, populatedCombination.direction, populatedCombination.len);
        moves = new MNKCellState[populatedCombination.len];
        System.arraycopy(populatedCombination.moves, 0, moves, 0, populatedCombination.moves.length);
        cellsRemaining = populatedCombination.cellsRemaining;
        this.isFirstPlayer = populatedCombination.isFirstPlayer;
        this.p1 = populatedCombination.p1;
        this.p2 = populatedCombination.p2;
    }


    public void saveMove(MNKCell cell) {
        if (isCellContained(cell)) {
            int index;
            switch (direction) {
                case HORIZONTAL:
                    index = cell.j - this.col;
                    moves[index] = cell.state;
                    break;
                case VERTICAL:
                    index = cell.i - this.row;
                    moves[index] = cell.state;
                    break;
                case OBLIQUE_ASC:
                case OBLIQUE_DESC:
                    index = cell.i - this.row;
                    index = Math.abs(index);
                    moves[index] = cell.state;
                    break;
            }
            this.updateInfo(cell);
        }
    }

    private void updateInfo(MNKCell cell) {
        if (cell.state == MNKCellState.P1) {
            p1 = true;
            cellsRemaining--;
        } else if (cell.state == MNKCellState.P2) {
            p2 = true;
            cellsRemaining--;
        }

    }

    /**
     * Questo metodo ritorna la prima cella libera nella combinazione.
     * UTILE PER GESTIRE LE SITUAZIONI FINALI DI GIOCO.
     * IN PARTICOLARE QUANDO isCriticalWin || isCriticalLose
     *
     * @return MNKCell
     */
    public MNKCell getCellFree() {
        for (int k = 0; k < moves.length; k++) {
            MNKCellState s = moves[k];
            if (s == MNKCellState.FREE) {
                switch (direction) {
                    case HORIZONTAL:
                        return new MNKCell(row, col + k, MNKCellState.FREE);
                    case VERTICAL:
                        return new MNKCell(row + k, col, MNKCellState.FREE);
                    case OBLIQUE_ASC:
                        return new MNKCell(row - k, col + k, MNKCellState.FREE);
                    case OBLIQUE_DESC:
                        return new MNKCell(row + k, col + k, MNKCellState.FREE);
                }
            }
        }
        return null;
    }


    public LinkedList<MNKCell> getAllCellsFree() {

        LinkedList<MNKCell> output = new LinkedList<>();

        for (int k = 0; k < moves.length; k++) {
            MNKCellState s = moves[k];
            if (s == MNKCellState.FREE) {
                switch (direction) {
                    case HORIZONTAL:
                        output.add(new MNKCell(row, col + k, MNKCellState.FREE));
                        break;
                    case VERTICAL:
                        output.add(new MNKCell(row + k, col, MNKCellState.FREE));
                        break;
                    case OBLIQUE_ASC:
                        output.add(new MNKCell(row - k, col + k, MNKCellState.FREE));
                        break;
                    case OBLIQUE_DESC:
                        output.add(new MNKCell(row + k, col + k, MNKCellState.FREE));
                        break;
                }
            }
        }
        return output;
    }

    public boolean isCriticalWin() {
        if (!isSatisfiable())
            return false;
        if (cellsRemaining > 1)
            return false;
        if (isFirstPlayer && p1)
            return true;
        if (!isFirstPlayer && p2)
            return true;
        return false;
    }

    public boolean isCriticalLose() {
        if (!isSatisfiable())
            return false;
        if (cellsRemaining > 1)
            return false;
        if (isFirstPlayer && p2)
            return true;
        if (!isFirstPlayer && p1)
            return true;
        return false;
    }

    public boolean isSatisfiable() {
        return !p1 || !p2;
    }

    public Combination getCombination() {
        return new Combination(this.row, this.col, this.direction, len);
    }

    /**
     * Questo metodo ritorna true se la combinazione è "del nemico" ovvero se non sono presenti simboli del nostro giocatore e se non è bianca.
     * False altrimenti
     *
     * @return boolean
     */
    public boolean isEnemyCombination() {
        if (isVoid()) return false;
        if (isFirstPlayer && p2) return true;
        return !isFirstPlayer && p1;
    }

    /**
     * Analoga a isEnemyCombination ma verifica che la combinazione sia "del nostro giocatore"
     * @return boolean
     */
    public boolean isPlayerCombination() {
        if (isVoid()) return false;
        if (isFirstPlayer && p1) return true;
        return !isFirstPlayer && p2;
    }

    /**
     * Ritorna true se la combinazione non ha simboli dei giocatori, false altrimenti
     *
     * @return boolean
     */
    public boolean isVoid() {
        return !p1 && !p2;
    }

    /**
     * Ritorna il livello della combinazione (Inteso come numero di simboli contigui di uno stesso giocatore)
     * SE LA COMBINAZIONE E' VUOTA O INSODDISFACIBILE RITORNA 0
     *
     * @return int
     */
    public int getLevel() {
        if (!isSatisfiable()) return 0;
        if (isVoid()) return 0;
        return len - cellsRemaining;
    }


}
