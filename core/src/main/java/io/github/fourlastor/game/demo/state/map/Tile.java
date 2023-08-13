package io.github.fourlastor.game.demo.state.map;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import io.github.fourlastor.game.coordinates.Hex;
import io.github.fourlastor.game.demo.state.unit.Unit;

public class Tile extends Group {
    public final Image actor;
    public final Hex coordinates;

    // (sheerst) Note: I wasn't sure how to do this, this is temporary.
    public Unit unit = null;

    public Tile(Image image, GridPoint2 position) {
        addActor(image);
        this.actor = image;
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
