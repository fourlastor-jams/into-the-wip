package io.github.fourlastor.game.demo.round;

import com.github.tommyettinger.ds.ObjectList;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.unit.Unit;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;
import squidpony.squidmath.IRNG;

public class Round extends RoundState {

    private static final Comparator<TurnOrder> BY_INITIATIVE = Comparator.comparingInt(it -> it.initiative);

    private final StateRouter router;
    private final IRNG rng;
    private List<TurnOrder> turns = null;
    private int turnCounter = 0;

    @Inject
    public Round(StateRouter router, IRNG rng) {
        this.router = router;
        this.rng = rng;
    }

    @Override
    public void enter(GameState state) {
        if (turns == null) {
            turns = new ObjectList<>(state.units.size());
            for (Unit unit : state.units) {
                turns.add(new TurnOrder(unit, rng.nextInt(20)));
            }
            turns.sort(BY_INITIATIVE);
            startFirstTurn();
        } else {
            advanceToNextTurn();
        }
    }

    private void startFirstTurn() {
        turnCounter = 0;
        startTurn();
    }

    private void advanceToNextTurn() {
        turnCounter += 1;
        startTurn();
    }

    private void startTurn() {
        if (turnCounter >= turns.size()) {
            router.round();
            return;
        }
        Unit unit = turns.get(turnCounter).unit;
        router.turn(unit);
    }

    private static class TurnOrder {
        final Unit unit;
        final int initiative;

        private TurnOrder(Unit unit, int initiative) {
            this.unit = unit;
            this.initiative = initiative;
        }
    }
}
