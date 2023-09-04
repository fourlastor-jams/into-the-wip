package io.github.fourlastor.game.demo.state;

import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.utils.Null;
import com.github.tommyettinger.ds.ObjectList;
import io.github.fourlastor.game.coordinates.Hex;
import io.github.fourlastor.game.demo.round.faction.Faction;
import io.github.fourlastor.game.demo.state.map.GraphMap;
import io.github.fourlastor.game.demo.state.map.Tile;
import io.github.fourlastor.game.demo.state.unit.Unit;
import io.github.fourlastor.game.ui.UiLayer;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class GameState {

    public final ObjectList<Unit> units;
    public final ObjectList<Tile> tiles;
    public final GraphMap graph;
    public final UiLayer ui;

    public GameState(ObjectList<Unit> units, ObjectList<Tile> tiles, UiLayer ui) {
        this.units = units;
        this.tiles = tiles;
        this.ui = ui;
        this.graph = new GraphMap();
        for (Tile tile : tiles) {
            graph.add(tile);
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

    public List<Tile> search(BiPredicate<GameState, Tile> filter) {
        return tiles.stream().filter(tile -> filter.test(this, tile)).collect(Collectors.toCollection(ObjectList::new));
    }

    public void alignAllHpBars() {
        for (Unit unit : units) {
            unit.alignHpBar();
        }
    }

    public List<Unit> byFaction(Faction faction) {
        return units.stream().filter(it -> it.faction == faction).collect(Collectors.toCollection(ObjectList::new));
    }

    public Tile tileAt(Hex hex) {
        return tiles.stream()
                .filter(it -> it.hex.equals(hex))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Hex does not contain a tile"));
    }

    @Null
    public Unit unitAt(Hex hex) {
        return units.stream().filter(it -> it.hex.equals(hex)).findFirst().orElse(null);
    }

    private void connectTiles(Tile tile, @Null Tile adjacent) {
        if (adjacent == null) {
            return;
        }
        graph.connect(tile, adjacent);
    }

    private Tile adjacent(Tile tile, int x, int y, int z) {
        GridPoint3 position = tile.hex.cube.cpy().add(x, y, z);
        // inside an iterator for loop already
        // and it would cause issues in GWT
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < tiles.size(); i++) {
            Tile it = tiles.get(i);
            if (it.hex.cube.equals(position)) {
                return it;
            }
        }
        return null;
    }
}
