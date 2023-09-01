package io.github.fourlastor.game.demo.round.step;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.unit.Unit;
import io.github.fourlastor.game.demo.state.unit.effect.PoisonEffect;

public class Poison extends SimpleStep {

    private final Unit source;
    private final Unit target;
    private final PoisonEffect poisonEffect;

    @AssistedInject
    public Poison(@Assisted("source") Unit source, @Assisted("target") Unit target, PoisonEffect poisonEffect) {
        this.source = source;
        this.target = target;
        this.poisonEffect = poisonEffect;
    }

    @Override
    public void enter(GameState state, Runnable continuation) {
        target.group.image.addAction(Actions.sequence(
                Actions.color(Color.OLIVE, 0.5f),
                Actions.color(Color.GREEN, 0.2f),
                Actions.color(Color.WHITE, 0.3f),
                Actions.run(() -> target.addEffect(poisonEffect)),
                Actions.run(continuation::run)));
    }

    /**
     * Factory interface for creating instances of the AttackRanged class.
     */
    @AssistedFactory
    public interface Factory {
        Poison create(@Assisted("source") Unit source, @Assisted("target") Unit target);
    }
}
