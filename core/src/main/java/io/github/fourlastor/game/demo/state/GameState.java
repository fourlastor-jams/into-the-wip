package io.github.fourlastor.game.demo.state;

import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.utils.Null;
import com.github.tommyettinger.ds.ObjectList;
import io.github.fourlastor.game.coordinates.Hex;
import io.github.fourlastor.game.demo.state.map.MapGraph;
import io.github.fourlastor.game.demo.state.map.Tile;
import io.github.fourlastor.game.demo.state.unit.Unit;

public class GameState {

    public final ObjectList<Unit> units;
    public final ObjectList<Tile> tiles;
    public final MapGraph graph;

    public GameState(ObjectList<Unit> units, ObjectList<Tile> tiles) {
        this.units = units;
        this.tiles = tiles;
        graph = new MapGraph();
        for (Tile tile : tiles) {
            graph.addTile(tile);
        }
        for (Tile tile : tiles) {
            connectTiles(tile, adjacent(tile, 0, 1, -1));
            connectTiles(tile, adjacent(tile, 0, -1, 1));
            connectTiles(tile, adjacent(tile, 1, 0, -1));
            connectTiles(tile, adjacent(tile, -1, 0, 1));
            connectTiles(tile, adjacent(tile, 1, -1, 0));
            connectTiles(tile, adjacent(tile, -1, 1, 0));
        }
    }

    public void alignAllHpBars() {
        for (Unit unit : units) {
            unit.alignHpBars();
        }
    }

    public Unit unitAt(Hex hex) {
        return units.stream()
                .filter(it -> it.position.equals(hex.offset))
                .findFirst()
                .orElse(null);
    }

    private void connectTiles(Tile tile, @Null Tile adjacent) {
        if (adjacent == null) {
            return;
        }
        graph.connect(tile, adjacent);
    }

    private Tile adjacent(Tile tile, int x, int y, int z) {
        GridPoint3 position = tile.coordinates.cube.cpy().add(x, y, z);
        return graph.get(position);
    }
}
