package io.github.fourlastor.game.demo.state.unit.effect;

import io.github.fourlastor.game.demo.state.unit.Unit;

public class PoisonEffect implements Effect.OnRoundStart {
    @Override
    public void onRoundStart(Unit unit, int stackCount) {
        unit.changeHp(-stackCount);
    }
}
