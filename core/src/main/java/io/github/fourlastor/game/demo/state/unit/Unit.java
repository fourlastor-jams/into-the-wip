package io.github.fourlastor.game.demo.state.unit;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import io.github.fourlastor.game.coordinates.HexCoordinates;

public class Unit {

    public final Actor actor;
    public final GridPoint2 position;
    public final HexCoordinates coordinates;

    public Unit(Actor actor, GridPoint2 position, HexCoordinates coordinates) {
        this.actor = actor;
        this.position = position;
        this.coordinates = coordinates;
    }
}
