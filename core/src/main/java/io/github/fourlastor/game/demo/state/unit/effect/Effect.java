package io.github.fourlastor.game.demo.state.unit.effect;

import io.github.fourlastor.game.demo.state.unit.Unit;

public interface Effect {

    interface OnRoundStart extends Effect {
        void onRoundStart(Unit unit, int stackAmount);
    }
}
