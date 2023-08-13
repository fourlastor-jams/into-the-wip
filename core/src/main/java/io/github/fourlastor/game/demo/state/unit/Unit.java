package io.github.fourlastor.game.demo.state.unit;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import io.github.fourlastor.game.coordinates.HexCoordinates;

public class Unit {

    public final Actor actor;
    public final GridPoint2 position;
    public final HexCoordinates coordinates;
    public int maxHp = 20;
    public int currentHp;

    public Unit(Actor actor, GridPoint2 position, HexCoordinates coordinates) {
        this.actor = actor;
        this.position = position;
        this.coordinates = coordinates;

        this.currentHp = this.maxHp;
    }

    public Vector2 getActorPosition() {
        return new Vector2(this.actor.getX(), this.actor.getY());
    }

    public void setActorPosition(Vector2 targetPosition) {
        this.actor.setPosition((int) targetPosition.x, (int) targetPosition.y);
    }

    public void setActorPosition(GridPoint2 targetPosition) {
        this.actor.setPosition(targetPosition.x, targetPosition.y);
    }
}
