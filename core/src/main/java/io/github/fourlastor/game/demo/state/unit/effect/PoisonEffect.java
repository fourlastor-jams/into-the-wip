package io.github.fourlastor.game.demo.state.unit.effect;

import io.github.fourlastor.game.demo.state.unit.Unit;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PoisonEffect implements Effect.OnRoundStart {

    @Inject
    public PoisonEffect() {}

    @Override
    public void onRoundStart(Unit unit, int stackCount) {
        unit.changeHp(-stackCount);
    }
}
