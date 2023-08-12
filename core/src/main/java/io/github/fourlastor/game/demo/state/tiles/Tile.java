package io.github.fourlastor.game.demo.state.tiles;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Tile {
    public final Actor actor;
    public final GridPoint2 position;

    public Tile(Actor actor, GridPoint2 position) {
        this.actor = actor;
        this.position = position;
    }
}
