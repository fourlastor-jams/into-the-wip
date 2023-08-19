package io.github.fourlastor.game.demo.state.unit;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import io.github.fourlastor.game.coordinates.HexCoordinates;
import io.github.fourlastor.game.demo.state.map.Tile;

public class Unit {

    public final Actor actor;
    public final GridPoint2 position;
    public final HexCoordinates coordinates;
    public final UnitType type;

    public Unit(Actor actor, GridPoint2 position, HexCoordinates coordinates, UnitType type) {
        this.actor = actor;
        this.position = position;
        this.coordinates = coordinates;
        this.type = type;
    }

    public boolean canTravel(Tile tile) {
        if (type.canFly) {
            return true;
        } else {
            return tile.type.allowWalking;
        }
    }
}
