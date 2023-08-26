package io.github.fourlastor.game.demo.state.map;

import com.badlogic.gdx.Gdx;
import java.util.function.Predicate;
import space.earlygrey.simplegraphs.Graph;
import space.earlygrey.simplegraphs.Path;
import space.earlygrey.simplegraphs.UndirectedGraph;
import space.earlygrey.simplegraphs.algorithms.AStarSearch;
import space.earlygrey.simplegraphs.algorithms.SearchStep;

public class GraphMap {
    private final Graph<Tile> graph = new UndirectedGraph<>();

    public void add(Tile tile) {
        graph.addVertex(tile);
    }

    public void connect(Tile first, Tile second) {
        Gdx.app.log("GraphMap", "Connecting " + first.hex.cube + " to " + second.hex.cube);
        graph.addEdge(first, second);
    }

    public Path<Tile> path(Tile origin, Tile destination, Predicate<SearchStep<Tile>> filter) {
        AStarSearch<Tile> search = graph.algorithms()
                .newAstarSeach(origin, destination, (u, tile) -> u.hex.offset.dst(tile.hex.offset), step -> {
                    if (!filter.test(step)) {
                        step.ignore();
                    }
                });
        search.finish();
        return search.getPath();
    }
}
