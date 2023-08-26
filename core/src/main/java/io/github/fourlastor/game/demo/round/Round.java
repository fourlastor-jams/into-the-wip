package io.github.fourlastor.game.demo.round;

import com.github.tommyettinger.ds.ObjectList;
import io.github.fourlastor.game.demo.round.faction.Faction;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.unit.Unit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
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
            turns = Arrays.stream(Faction.values())
                    .flatMap(faction -> state.byFaction(faction).stream())
                    .map(unit -> new TurnOrder(unit, 0))
                    .collect(Collectors.toCollection(ObjectList::new));
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
        if (turnCounter >= turns.size()) {
            router.round();
            return;
        }
        startTurn();
    }

    private void startTurn() {
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
