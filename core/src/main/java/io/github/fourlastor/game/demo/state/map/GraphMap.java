package io.github.fourlastor.game.demo.state.map;

import com.github.tommyettinger.ds.ObjectList;
import java.util.List;
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
        graph.addEdge(first, second);
    }

    public Path<Tile> path(Tile origin, Tile destination, Filter filter) {
        AStarSearch<Tile> search = graph.algorithms()
                .newAstarSeach(origin, destination, (u, tile) -> u.hex.offset.dst(tile.hex.offset), step -> {
                    if (!filter.test(step)) {
                        step.ignore();
                    }
                });
        search.finish();
        return search.getPath();
    }

    public List<Tile> search(Tile origin, Filter filter) {
        List<Tile> result = new ObjectList<>();
        graph.algorithms().breadthFirstSearch(origin, step -> {
            // skipping the first step equals to canceling the whole search
            if (step.depth() == 0) {
                return;
            }
            if (!filter.test(step)) {
                step.ignore();
            } else {
                result.add(step.vertex());
            }
        });
        return result;
    }

    public interface Filter extends Predicate<SearchStep<Tile>> {
        static Filter all(Filter... filters) {
            return step -> {
                boolean result = true;
                for (Filter filter : filters) {
                    result &= filter.test(step);
                }
                return result;
            };
        }

        static Filter any(Filter... filters) {
            return step -> {
                boolean result = false;
                for (Filter filter : filters) {
                    result |= filter.test(step);
                }
                return result;
            };
        }
    }
}
