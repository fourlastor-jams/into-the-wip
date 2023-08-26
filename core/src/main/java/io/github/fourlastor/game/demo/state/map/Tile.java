package io.github.fourlastor.game.demo.state.map;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import io.github.fourlastor.game.coordinates.Hex;
import io.github.fourlastor.game.ui.TileOnMap;

public class Tile {
    public TileOnMap actor;
    public final Hex hex;
    public TileType type;

    public Tile(TileOnMap image, GridPoint2 position, TileType type) {
        this.actor = image;
        this.hex = new Hex(position);
        this.type = type;
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

    public Vector2 getActorPosition() {
        return new Vector2(actor.getX(), actor.getY());
    }

    public void setActorPosition(Vector2 targetPosition) {
        actor.setPosition(targetPosition.x, targetPosition.y);
    }
}
