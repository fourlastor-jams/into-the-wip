package io.github.fourlastor.game.demo.state.unit.effect;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.ObjectIntMap;
import io.github.fourlastor.game.demo.state.unit.Unit;

public class EffectStacks {

    private final ObjectIntMap<Effect> stacks = new ObjectIntMap<>();

    public void addStack(Effect effect, int quantity) {
        int current = getStack(effect);
        stacks.put(effect, current + quantity);
    }

    public void tickStacks() {
        for (Effect effect : stacks.keys()) {
            int current = getStack(effect);
            if (current <= 1) {
                stacks.remove(effect, 0);
            } else {
                stacks.put(effect, current - 1);
            }
        }
    }

    private int getStack(Effect effect) {
        return stacks.get(effect, 0);
    }

    public Action onRoundStart(Unit unit) {
        SequenceAction sequence = Actions.sequence();
        for (Effect effect : stacks.keys()) {
            if (effect instanceof Effect.OnRoundStart) {
                sequence.addAction(((Effect.OnRoundStart) effect).onRoundStart(unit, getStack(effect)));
            }
        }
        return sequence;
    }
}
