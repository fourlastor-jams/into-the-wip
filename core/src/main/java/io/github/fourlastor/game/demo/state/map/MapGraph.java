package io.github.fourlastor.game.demo.state.map;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Null;
import com.github.tommyettinger.ds.IntObjectMap;
import com.github.tommyettinger.ds.ObjectList;
import com.github.tommyettinger.ds.ObjectObjectMap;
import com.github.tommyettinger.ds.ObjectSet;
import io.github.fourlastor.game.coordinates.Hex;
import io.github.fourlastor.game.coordinates.Packer;
import io.github.fourlastor.game.demo.state.unit.Unit;
import io.github.fourlastor.game.demo.state.unit.UnitType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MapGraph implements IndexedGraph<Tile> {

    private final Heuristic<Tile> heuristic = new TileHeuristic();
    private final IntObjectMap<Tile> tiles;
    private final List<Tile> indexed;
    private final Map<Tile, List<Connection<Tile>>> connections;

    public MapGraph() {
        this(new IntObjectMap<>(), new ObjectList<>(), new ObjectObjectMap<>());
    }

    private MapGraph(IntObjectMap<Tile> tiles, List<Tile> indexed, Map<Tile, List<Connection<Tile>>> connections) {
        this.tiles = tiles;
        this.indexed = indexed;
        this.connections = connections;
    }

    public void addTile(Tile tile) {
        tiles.put(offsetId(tile), tile);
        indexed.add(tile);
    }

    public void removeTile(GridPoint2 position) {
        Tile tile = get(position);
        if (tile == null) {
            return;
        }
        tiles.remove(offsetId(tile));
        indexed.remove(tile);
        connections.remove(tile);
        connections.values().forEach(connection -> connection.removeIf((it) -> it.getToNode() == tile));
    }

    private int offsetId(Tile tile) {
        return Packer.pack(tile.hex.offset);
    }

    private final GridPoint2 cached = new GridPoint2();

    /** Returns an instance of this graph specific for the unit, removing tiles the unit cannot pass and so forth. */
    public MapGraph forUnit(Unit unit) {
        List<Tile> newIndexed = new ArrayList<>();

        // Perform a breadth-first search to determine which tiles are reachable
        Set<Tile> reachableTiles = new HashSet<>();
        LinkedList<Tile> queue = new LinkedList<Tile>();
        Tile startTile = indexed.stream()
                .filter((it) -> unit.hex.equals(it.hex))
                .findFirst()
                .get();

        queue.add(startTile);

        while (!queue.isEmpty()) {
            Tile currentTile = queue.poll();
            if (!reachableTiles.contains(currentTile)) {
                reachableTiles.add(currentTile);

                // Add neighboring tiles to the queue if they are passable and not solid.
                // Special rules apply to Tectonne.
                for (Connection<Tile> connection : connections.get(currentTile)) {
                    Tile neighborTile = connection.getToNode();
                    if (unit.type == UnitType.TECTONNE && neighborTile.type == TileType.SOLID) {
                        reachableTiles.add(neighborTile);
                        continue;
                    }

                    if (!unit.canTravel(neighborTile)) {
                        continue;
                    }
                    queue.add(neighborTile);
                }
            }
        }

        // Filter the indexed tiles based on reachability
        for (Tile tile : indexed) {
            if (reachableTiles.contains(tile)) {
                newIndexed.add(tile);
            }
        }

        // Create new maps for tiles and connections
        IntObjectMap<Tile> newTiles = new IntObjectMap<>();
        ObjectObjectMap<Tile, List<Connection<Tile>>> newConnections = new ObjectObjectMap<>();

        for (Tile tile : reachableTiles) {
            newTiles.put(tiles.findKey(tile, 0), tile);

            List<Connection<Tile>> newTileConnections = new ArrayList<>();
            for (Connection<Tile> connection : connections.get(tile)) {
                if (reachableTiles.contains(connection.getToNode())) {
                    newTileConnections.add(connection);
                }
            }
            newConnections.put(tile, newTileConnections);
        }
        return new MapGraph(newTiles, newIndexed, newConnections);
    }

    @Null
    public Tile get(int x, int y) {
        return tiles.get(Packer.pack(cached.set(x, y)));
    }

    @Null
    public Tile get(GridPoint2 position) {
        return tiles.get(Packer.pack(position));
    }

    @Null
    public Tile get(GridPoint3 cubePosition) {
        return tiles.get(Packer.pack(Hex.toOffset(cubePosition)));
    }

    public void connect(Tile from, Tile to) {
        populateConnections(from);
        Objects.requireNonNull(connections.get(from)).add(new Tile.Link(from, to));
    }

    public GraphPath<Tile> calculatePath(Tile from, Tile to) {
        DefaultGraphPath<Tile> path = new DefaultGraphPath<>();
        new IndexedAStarPathFinder<>(this).searchNodePath(from, to, heuristic, path);
        return path;
    }

    public GraphPath<Tile> calculatePath(Hex hex, Tile to) {
        return calculatePath(get(hex.offset), to);
    }

    private void populateConnections(Tile a) {
        if (!connections.containsKey(a)) {
            connections.put(a, new ObjectList<>());
        }
    }

    @Override
    public int getIndex(Tile node) {
        return indexed.indexOf(node);
    }

    @Override
    public int getNodeCount() {
        return tiles.size();
    }

    @Override
    public Array<Connection<Tile>> getConnections(Tile fromNode) {
        if (connections.containsKey(fromNode)) {
            List<Connection<Tile>> tileConnections = connections.get(fromNode);
            return new Array<Connection<Tile>>(tileConnections.toArray(new Connection[0]));
        }

        return new Array<>(0);
    }

    public Set<Tile> tilesAtDistance(Tile origin, int distance) {
        LinkedList<TileAtDepth> queue = new LinkedList<>();
        ObjectSet<Tile> visited = new ObjectSet<>();
        queue.add(new TileAtDepth(origin, 0));
        while (!queue.isEmpty()) {
            TileAtDepth atDepth = queue.pop();
            visited.add(atDepth.tile);
            if (atDepth.depth < distance) {
                for (Connection<Tile> connection : getConnections(atDepth.tile)) {
                    if (visited.contains(connection.getToNode())) {
                        continue;
                    }
                    queue.add(new TileAtDepth(connection.getToNode(), atDepth.depth + 1));
                }
            }
        }
        return visited;
    }

    private static class TileHeuristic implements Heuristic<Tile> {

        @Override
        public float estimate(Tile node, Tile endNode) {
            return node.hex.offset.dst(endNode.hex.offset);
        }
    }

    private static class TileAtDepth {
        final Tile tile;
        final int depth;

        private TileAtDepth(Tile tile, int depth) {
            this.tile = tile;
            this.depth = depth;
        }
    }
}
