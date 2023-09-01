package io.github.fourlastor.game.demo.state.unit.effect;

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

    public void onRoundStart(Unit unit) {
        for (Effect effect : stacks.keys()) {
            if (effect instanceof Effect.OnRoundStart) {
                ((Effect.OnRoundStart) effect).onRoundStart(unit, getStack(effect));
            }
        }
    }
}
