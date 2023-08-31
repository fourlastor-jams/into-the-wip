package io.github.fourlastor.game.demo.round;

import io.github.fourlastor.game.demo.state.unit.Unit;

public class UnitInRound {

    public final Unit unit;
    public boolean hasActed = false;

    public UnitInRound(Unit unit) {
        this.unit = unit;
    }
}
