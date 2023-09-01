package io.github.fourlastor.game.demo.state.unit.effect;

import com.badlogic.gdx.scenes.scene2d.Action;
import io.github.fourlastor.game.demo.state.unit.Unit;

public interface Effect {

    interface OnRoundStart extends Effect {
        Action onRoundStart(Unit unit, int stackAmount);
    }
}
