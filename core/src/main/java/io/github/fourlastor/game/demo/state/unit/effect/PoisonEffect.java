package io.github.fourlastor.game.demo.state.unit.effect;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import io.github.fourlastor.game.demo.state.unit.Unit;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PoisonEffect implements Effect.OnRoundStart {

    @Inject
    public PoisonEffect() {}

    @Override
    public Action onRoundStart(Unit unit, int stackCount) {
        SequenceAction sequence = Actions.sequence(
                Actions.color(Color.OLIVE, 0.5f),
                Actions.color(Color.GREEN, 0.2f),
                Actions.color(Color.WHITE, 0.3f),
                Actions.run(() -> unit.changeHp(-stackCount)));
        sequence.setActor(unit.group.image);
        return sequence;
    }
}
