package io.github.fourlastor.game.demo.state.map

import space.earlygrey.simplegraphs.Graph
import space.earlygrey.simplegraphs.Path
import space.earlygrey.simplegraphs.UndirectedGraph
import space.earlygrey.simplegraphs.algorithms.SearchStep
import java.util.function.Predicate

class GraphMap {
    private val graph: Graph<Tile> = UndirectedGraph()
    fun add(tile: Tile) {
        graph.addVertex(tile)
    }

    fun connect(first: Tile, second: Tile) {
        graph.addEdge(first, second)
    }

    fun path(origin: Tile, destination: Tile, filter: Predicate<SearchStep<Tile>>): Path<Tile> {
        val search = graph.algorithms()
            .newAstarSeach(
                origin,
                destination,
                { u, tile -> u.hex.offset.dst(tile.hex.offset) },
                {
                    if (!filter.test(it)) {
                        it.ignore()
                    }
                }
            )
        search.finish()
        return search.getPath()
    }
}
