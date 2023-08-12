package io.github.fourlastor.game.demo.state;

import com.github.tommyettinger.ds.ObjectList;
import io.github.fourlastor.game.demo.state.tiles.Tile;
import io.github.fourlastor.game.demo.state.unit.Unit;

public class GameState {

    public final ObjectList<Unit> units;
    public final ObjectList<Tile> tiles;

    public GameState(ObjectList<Unit> units, ObjectList<Tile> tiles) {
        this.units = units;
        this.tiles = tiles;
    }
}
