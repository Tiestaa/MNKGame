package mnkgame.claudio;

import mnkgame.MNKCell;
import mnkgame.MNKCellState;

import java.util.LinkedList;

public class EvaluatedSituation {

    private final MNKCell evaluatedMove;
    private boolean isCriticalForEnemy;
    private int maxLevelCombinationPlayer;
    private int maxLevelCombinationPlayerCounter;
    private int maxLevelCombinationEnemy;
    private int maxLevelCombinationEnemyCounter;
    //timedOut serve a capire se si è sforato il tempo limite per valutare la situazione (può capitare in caso le combinazioni da valutare sono troppe)
    private boolean timedOut;

    private int changedCombinations;

    private int enemyForcedCombinations;

    public EvaluatedSituation(MNKCell move, LinkedList<PopulatedCombination> combinations, long startTime, int maxEvalTime, int M, int N) {
        evaluatedMove = move;
        isCriticalForEnemy = false;
        maxLevelCombinationPlayer = -1;
        maxLevelCombinationEnemy = -1;
        timedOut = false;
        changedCombinations = 0;
        maxLevelCombinationPlayerCounter = 0;
        maxLevelCombinationEnemyCounter = 0;
        enemyForcedCombinations = -1;
        eval(combinations, startTime, maxEvalTime, M, N);
    }

    private void eval(LinkedList<PopulatedCombination> combinations, long startTime, int maxEvalTime, int M, int N) {

        PopulatedCombination criticalCombination = null;

        for (PopulatedCombination pc : combinations) {

            PopulatedCombination analyzingCombination = new PopulatedCombination(pc);

            if (pc.isCellContained(evaluatedMove)) {
                changedCombinations = changedCombinations + 1;
                analyzingCombination.saveMove(evaluatedMove);
                if (!analyzingCombination.isSatisfiable())
                    continue;
            }

            if (System.currentTimeMillis() >= startTime + maxEvalTime) {
                timedOut = true;
                break;
            }


            int combinationLevel = analyzingCombination.getLevel();

            if (analyzingCombination.isEnemyCombination() && maxLevelCombinationEnemy < combinationLevel) {
                maxLevelCombinationEnemy = combinationLevel;
                maxLevelCombinationEnemyCounter = 0;
            } else if (analyzingCombination.isPlayerCombination() && maxLevelCombinationPlayer < combinationLevel) {
                maxLevelCombinationPlayer = combinationLevel;
                maxLevelCombinationPlayerCounter = 0;
            }

            if (combinationLevel == maxLevelCombinationPlayer && analyzingCombination.isPlayerCombination()) {
                maxLevelCombinationPlayerCounter = maxLevelCombinationPlayerCounter + 1;
            }

            if (combinationLevel == maxLevelCombinationEnemy && analyzingCombination.isEnemyCombination()) {
                maxLevelCombinationEnemyCounter = maxLevelCombinationEnemyCounter + 1;
            }

            boolean isCriticalWin = analyzingCombination.isCriticalWin();
            if (!isCriticalForEnemy && isCriticalWin) {
                isCriticalForEnemy = true;
                criticalCombination = new PopulatedCombination(analyzingCombination);
            }


        }

        /*
        Nel caso in cui la situazione è critica e vi è una sola combinazione che la rende tale verifichiamo la situazione dopo che l'avversario ha giocato la mossa da noi desiderata
         */
        if (criticalCombination != null && maxLevelCombinationPlayerCounter == 1) {
            MNKCell forcedCell = criticalCombination.getCellFree();
                if(forcedCell != null) enemyForcedCombinations = getNumberCombinationAffected(combinations, forcedCell, M, N, criticalCombination.len);
        }

    }

    private int getNumberCombinationAffected(LinkedList<PopulatedCombination> combinations, MNKCell cell, int M, int N, int K) {
        int output = 0;

        for (PopulatedCombination pc : combinations) {
            if (pc.isCellContained(cell)) {
                output = output + 1;
            }
        }
        return output;

    }


    public MNKCell getEvaluatedMove() {
        return new MNKCell(evaluatedMove.i, evaluatedMove.j, MNKCellState.FREE);
    }

    public boolean isCriticalForEnemy() {
        return isCriticalForEnemy;
    }

    public int getMaxLevelCombinationPlayer() {
        return maxLevelCombinationPlayer;
    }

    public int getMaxLevelCombinationEnemy() {
        return maxLevelCombinationEnemy;
    }

    public boolean isTimedOut() {
        return timedOut;
    }

    public int getChangedCombinations() {
        return changedCombinations;
    }

    public int getMaxLevelCombinationPlayerCounter() {
        return maxLevelCombinationPlayerCounter;
    }

    public int getMaxLevelCombinationEnemyCounter() {
        return maxLevelCombinationEnemyCounter;
    }

    public int getEnemyForcedCombinations() {
        return enemyForcedCombinations;
    }
}
