package io.github.fourlastor.game.coordinates;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.ObjectMap;
import java.util.Objects;

public class MapGraph implements IndexedGraph<Tile> {

    private final Heuristic<Tile> heuristic = new TileHeuristic();
    private final IntMap<Tile> tiles = new IntMap<>();
    private final Array<Tile> indexed = new Array<>();
    private final ObjectMap<Tile, Array<Connection<Tile>>> connections = new ObjectMap<>();
    private IndexedAStarPathFinder<Tile> finder = new IndexedAStarPathFinder<>(this);

    public void addTile(Tile tile) {
        tiles.put(tile.packedCoord(), tile);
        indexed.add(tile);
    }

    private final GridPoint2 cached = new GridPoint2();

    @Null
    public Tile get(int x, int y) {
        return tiles.get(Coordinate.pack(cached.set(x, y)));
    }

    @Null
    public Tile get(GridPoint2 position) {
        return tiles.get(Coordinate.pack(position));
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

    private void populateConnections(Tile a) {
        if (!connections.containsKey(a)) {
            connections.put(a, new Array<>());
        }
    }

    @Override
    public int getIndex(Tile node) {
        return indexed.indexOf(node, true);
    }

    @Override
    public int getNodeCount() {
        return tiles.size;
    }

    @Override
    public Array<Connection<Tile>> getConnections(Tile fromNode) {
        if (connections.containsKey(fromNode)) {
            return connections.get(fromNode);
        }

        return new Array<>(0);
    }

    private static class TileHeuristic implements Heuristic<Tile> {

        @Override
        public float estimate(Tile node, Tile endNode) {
            return node.position.dst(endNode.position);
        }
    }
}
