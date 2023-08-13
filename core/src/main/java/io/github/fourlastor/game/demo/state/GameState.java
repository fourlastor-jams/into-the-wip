package io.github.fourlastor.game.demo.state;

import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.utils.Null;
import com.github.tommyettinger.ds.ObjectList;
import io.github.fourlastor.game.demo.state.map.MapGraph;
import io.github.fourlastor.game.demo.state.map.Tile;
import io.github.fourlastor.game.demo.state.unit.Unit;

public class GameState {

    public final ObjectList<Unit> units;
    public final ObjectList<Tile> tiles;
    private final MapGraph graph;

    public GameState(ObjectList<Unit> units, ObjectList<Tile> tiles) {
        this.units = units;
        this.tiles = tiles;
        graph = new MapGraph();
        for (Tile tile : tiles) {
            graph.addTile(tile);
        }
        for (Tile tile : tiles) {
            connectTiles(tile, adjacent(tile, -1, 0, 0));
            connectTiles(tile, adjacent(tile, 1, 0, 0));
            connectTiles(tile, adjacent(tile, 0, -1, 0));
            connectTiles(tile, adjacent(tile, 0, 1, 0));
            connectTiles(tile, adjacent(tile, 0, 0, -1));
            connectTiles(tile, adjacent(tile, 0, 0, 1));
        }
    }

    private void connectTiles(Tile tile, @Null Tile adjacent) {
        if (adjacent == null) {
            return;
        }
        graph.connect(tile, adjacent);
    }

    private final GridPoint3 adjacentTmp = new GridPoint3();

    private Tile adjacent(Tile tile, int x, int y, int z) {
        GridPoint3 position = adjacentTmp.set(tile.coordinates.cube).add(x, y, z);
        return graph.get(position);
    }
}
