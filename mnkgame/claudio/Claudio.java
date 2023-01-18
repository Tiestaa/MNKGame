package mnkgame.claudio;

/*
Created by: Daniele Romanella
 */

import mnkgame.MNKCell;
import mnkgame.MNKCellState;
import mnkgame.MNKPlayer;

import java.util.LinkedList;
import java.util.Random;

public class Claudio implements MNKPlayer {

    private int altezza, larghezza, k, timeout;
    boolean isPlayer1;

    boolean firstMovePlayed;

    SituationHandler situationHandler;

    public Claudio() {
    }

    @Override
    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
        altezza = M;
        larghezza = N;
        k = K;
        isPlayer1 = first;
        timeout = timeout_in_secs;
        firstMovePlayed = !first;
        situationHandler = new SituationHandler(K, isPlayer1, M, N);
    }

    @Override
    public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {
        long startTime = System.currentTimeMillis();
        long millisForMove = timeout * 1000L;

        MNKCell move = FC[new Random().nextInt(FC.length)];
        MNKCell proposedMove;
        MNKCell cell1 = null;
        MNKCell cell2 = null;

        //se bisogna giocare la prima mossa chiaramente sceglieremo la cella centrale
        if (!firstMovePlayed) {
            firstMovePlayed = true;
            return new MNKCell(altezza / 2, larghezza / 2, MNKCellState.FREE);
        }

        if (MC.length >= 2) {
            cell1 = MC[MC.length - 1];
            cell2 = MC[MC.length - 2];
        } else if (MC.length == 1) {
            cell1 = MC[MC.length - 1];
        }
        situationHandler.saveMove(cell1, cell2);

        //Non ci sono combinazioni vincenti. Va bene una mossa a caso per terminare rapidamente la partita
        if (situationHandler.getNumberSatisfiableCombinations() == 0)
            return move;

        //Verifichiamo che ci siano situazioni in cui è possibile vincere
        proposedMove = situationHandler.proposedMoveForCriticalCombinations();
        if(proposedMove != null) return proposedMove;

        //Otteniamo una lista delle mosse che è interessante andare ad analizzare.
        LinkedList<MNKCell> interestingMoves = situationHandler.interestingMoves(altezza, larghezza, FC.length);

        proposedMove = situationHandler.getBestMove(interestingMoves, altezza, larghezza, (startTime + millisForMove) - System.currentTimeMillis());
        if (proposedMove == null) return move;
        else return proposedMove;

    }


    @Override
    public String playerName() {
        return "Claudio";
    }

}
