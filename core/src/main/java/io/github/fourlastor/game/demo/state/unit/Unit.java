package io.github.fourlastor.game.demo.state.unit;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Unit {

    public final Actor actor;
    public final GridPoint2 position;

    public Unit(Actor actor, GridPoint2 position) {
        this.actor = actor;
        this.position = position;
    }
}
