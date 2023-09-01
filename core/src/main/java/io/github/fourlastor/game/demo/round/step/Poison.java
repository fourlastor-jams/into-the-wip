package io.github.fourlastor.game.demo.round.step;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.unit.Unit;
import io.github.fourlastor.game.demo.state.unit.effect.PoisonEffect;

public class Poison extends SimpleStep {

    private final Unit source;
    private final Unit target;

    @AssistedInject
    public Poison(@Assisted("source") Unit source, @Assisted("target") Unit target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public void enter(GameState state, Runnable continuation) {
        target.addEffect(new PoisonEffect());
        continuation.run();
    }

    /**
     * Factory interface for creating instances of the AttackRanged class.
     */
    @AssistedFactory
    public interface Factory {
        Poison create(@Assisted("source") Unit source, @Assisted("target") Unit target);
    }
}
