package mnkgame.claudio;

import mnkgame.MNKCell;
import mnkgame.MNKCellState;


import java.util.LinkedList;

public class SituationHandler {

    private final LinkedList<PopulatedCombination> combinations;

    private final boolean isFirstPlayer;

    private LinkedList<PopulatedCombination> toRemove;

    public SituationHandler(int len, boolean isFirstPlayer, int altezza, int larghezza) {
        this.combinations = this.getWinningCombinations(altezza, larghezza, len, isFirstPlayer);
        this.isFirstPlayer = isFirstPlayer;
        toRemove = null;
    }

    /**
     * Descrizione:
     * Questa funzione ritorna la lista di tutte le possibili combinazioni vincenti nella matrice M, N, K
     * Ad esempio nel gioco MNK con M = N = K = 3 (Il classico Tris) La funzione ritorna una lista di 8 elementi.
     * Perchè è possibile vincere in 8 modi diversi.
     * Perchè è possibile vincere riempiendo le 3 colonne, le 3 righe o le 2 diagonali
     * Per cui 3 + 3 + 2 = 8.
     * Questa funzione astrae il calcolo precedentemente spiegato e lo adatta anche nel caso K != N != M
     *
     * @return LinkedList<Combination>
     * @implNote Le righe e le colonne vengono individuate con un processo che analizza la matrice da sinistra verso destra con un'operazione di somma.
     * --- Per le diagonali "Decrescenti" questo metodo funziona, per individuare quelle "Crescenti" occcorre fare un'ulteriore operazione.
     * <p>
     * --- Le coordinate i, j puntano al primo elemento.
     */
    private LinkedList<PopulatedCombination> getWinningCombinations(int altezza, int larghezza, int k, boolean isFirstPlayer) {
        LinkedList<PopulatedCombination> output = new LinkedList<>();

        for (int row = 0; row < altezza; row++) {
            for (int col = 0; col < larghezza; col++) {

                if (col + k <= larghezza) {
                    output.add(new PopulatedCombination(new Combination(row, col, Direction.HORIZONTAL, k), k, isFirstPlayer));
                }

                if (row + k <= altezza) {
                    output.add(new PopulatedCombination(new Combination(row, col, Direction.VERTICAL, k), k, isFirstPlayer));
                }

                if (col + k <= larghezza && row + k <= altezza) {
                    output.add(new PopulatedCombination(new Combination(row, col, Direction.OBLIQUE_DESC, k), k, isFirstPlayer));
                }

                if (col + k <= larghezza && (row + 1) - k >= 0) {
                    output.add(new PopulatedCombination(new Combination(row, col, Direction.OBLIQUE_ASC, k), k, isFirstPlayer));
                }

            }
        }

        return output;
    }

    public void saveMove(MNKCell cell1, MNKCell cell2) {
        toRemove = new LinkedList<>();
        for (PopulatedCombination c : combinations) {
            if (cell1 != null) {
                c.saveMove(cell1);
            }

            if (cell2 != null) {
                c.saveMove(cell2);
            }

            removeIfNotSatisfiable(c);
        }
        deleteNotSatisfiableCombinations();
    }

    private void removeIfNotSatisfiable(PopulatedCombination populatedCombination) {

        if (!populatedCombination.isSatisfiable() && toRemove != null) {
            toRemove.add(populatedCombination);
        }
    }

    private void deleteNotSatisfiableCombinations() {
        if (toRemove != null) {
            for (PopulatedCombination pc : toRemove) {
                combinations.remove(pc);
            }
        }
    }

    public MNKCell proposedMoveForCriticalCombinations() {
        MNKCell output = null;

        for (PopulatedCombination c : combinations) {
            if (c.isCriticalWin()) {
                return c.getCellFree();
            }

            if (c.isCriticalLose() && output == null) {
                output = c.getCellFree();
            }

        }

        return output;
    }

    public MNKCell getBestMove(LinkedList<MNKCell> interestingMoves, int M, int N, long timeLeft) {
        EvaluatedSituation bestSituation = null;

        MNKCellState state = MNKCellState.P2;
        if (isFirstPlayer) state = MNKCellState.P1;

        if (interestingMoves.size() == 0) return null;

        long timeForSituation = timeLeft / interestingMoves.size();
        timeForSituation = (int) (timeForSituation * 0.95);

        for (MNKCell cell : interestingMoves) {
            MNKCell tmpCell = new MNKCell(cell.i, cell.j, state);
            EvaluatedSituation situation = new EvaluatedSituation(tmpCell, this.combinations, System.currentTimeMillis(), (int) timeForSituation, M, N);

            if (bestSituation == null) {
                bestSituation = situation;
                continue;
            }

            if (bestSituation.getMaxLevelCombinationPlayer() <= bestSituation.getMaxLevelCombinationEnemy()
                    && situation.getMaxLevelCombinationPlayer() > situation.getMaxLevelCombinationEnemy()) {
                bestSituation = situation;
                continue;
            }

            if (situation.isCriticalForEnemy() && !bestSituation.isCriticalForEnemy()) {
                bestSituation = situation;
                continue;
            }

            if (situation.isCriticalForEnemy()
                    && bestSituation.isCriticalForEnemy()
                    && (situation.getMaxLevelCombinationPlayerCounter() > bestSituation.getMaxLevelCombinationPlayerCounter())) {
                bestSituation = situation;
                continue;
            }

            if (situation.isCriticalForEnemy()
                    && bestSituation.isCriticalForEnemy()
                    && (situation.getEnemyForcedCombinations() < bestSituation.getEnemyForcedCombinations())) {
                bestSituation = situation;
                continue;
            }


            if (situation.getMaxLevelCombinationEnemy() < bestSituation.getMaxLevelCombinationEnemy()
                    && !bestSituation.isCriticalForEnemy()) {
                bestSituation = situation;
                continue;
            }

            if (situation.getMaxLevelCombinationPlayer() > bestSituation.getMaxLevelCombinationPlayer()) {
                bestSituation = situation;
                continue;
            }

            if (!bestSituation.isCriticalForEnemy()) {
                if ((situation.getMaxLevelCombinationPlayer() == bestSituation.getMaxLevelCombinationPlayer() && situation.getMaxLevelCombinationEnemy() == bestSituation.getMaxLevelCombinationEnemy())
                        && (situation.getMaxLevelCombinationPlayerCounter() >= bestSituation.getMaxLevelCombinationPlayerCounter() && situation.getMaxLevelCombinationEnemyCounter() <= bestSituation.getMaxLevelCombinationEnemyCounter())
                        && situation.getChangedCombinations() >= bestSituation.getChangedCombinations()) {
                    bestSituation = situation;
                    continue;
                }


                if (situation.getMaxLevelCombinationPlayer() == bestSituation.getMaxLevelCombinationPlayer() &&
                        situation.getMaxLevelCombinationEnemy() == bestSituation.getMaxLevelCombinationEnemy() &&
                        situation.getMaxLevelCombinationPlayerCounter() == bestSituation.getMaxLevelCombinationPlayerCounter() &&
                        situation.getMaxLevelCombinationEnemyCounter() == bestSituation.getMaxLevelCombinationEnemyCounter() &&
                        situation.getChangedCombinations() > bestSituation.getChangedCombinations()) {
                    bestSituation = situation;
                    continue;
                }
            }

            /*
            if (bestSituation.isTimedOut() && !situation.isTimedOut())
                bestSituation = situation;

             */
        }

        return bestSituation.getEvaluatedMove();

    }


    public LinkedList<MNKCell> interestingMoves(int M, int N, int maximumFreeCells) {
        LinkedList<MNKCell> output = new LinkedList<>();
        boolean[][] markedCell = new boolean[M][N];


        for (PopulatedCombination combination : combinations) {
            if (combination.isVoid()) continue;

            LinkedList<MNKCell> cellsFree = combination.getAllCellsFree();

            for (MNKCell cell : cellsFree) {
                if (!markedCell[cell.i][cell.j]) {
                    markedCell[cell.i][cell.j] = true;
                    output.add(cell);
                }
            }

            if (maximumFreeCells == output.size()) break;

        }

        return output;

    }

    public int getNumberSatisfiableCombinations() {
        return combinations.size();
    }
}
