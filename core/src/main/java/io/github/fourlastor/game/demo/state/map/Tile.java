package io.github.fourlastor.game.demo.state.map;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import io.github.fourlastor.game.coordinates.Hex;

public class Tile {
    public final Actor actor;
    public final Hex coordinates;

    public Tile(Actor actor, GridPoint2 position) {
        this.actor = actor;
        this.coordinates = new Hex(position);
    }

    public static class Link implements Connection<Tile> {

        private final Tile from;
        private final Tile to;

        public Link(Tile from, Tile to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public float getCost() {
            return 1;
        }

        @Override
        public Tile getFromNode() {
            return from;
        }

        @Override
        public Tile getToNode() {
            return to;
        }
    }
}
