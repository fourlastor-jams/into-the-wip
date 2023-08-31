package io.github.fourlastor.game.demo.round;

import io.github.fourlastor.game.demo.state.unit.Unit;

public class UnitInTurn {

    public final Unit unit;
    public boolean hasActed = false;

    public UnitInTurn(Unit unit) {
        this.unit = unit;
    }
}
