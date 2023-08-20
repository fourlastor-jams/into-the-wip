package io.github.fourlastor.game.demo.round;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.unit.Unit;

public class Turn extends RoundState {

    private final Unit unit;
    private final StateRouter router;

    private boolean acted = false;

    @AssistedInject
    public Turn(@Assisted Unit unit, StateRouter router) {
        this.unit = unit;
        this.router = router;
    }

    @Override
    public void enter(GameState state) {
        if (!acted) {
            acted = true;
            router.startAbility(unit);
        } else {
            router.endOfTurn();
        }
    }

    @AssistedFactory
    public interface Factory {
        Turn create(Unit unit);
    }
}
