package io.github.fourlastor.game.coordinates;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.math.GridPoint2;

public class Tile {
    public final GridPoint2 position;

    public Tile(GridPoint2 position) {
        this.position = position;
    }

    public int packedCoord() {
        return Coordinate.pack(position);
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
