package io.github.fourlastor.game.demo.state

import io.github.fourlastor.game.coordinates.Hex
import io.github.fourlastor.game.demo.state.map.Tile
import io.github.fourlastor.game.demo.state.map.TileType
import io.github.fourlastor.game.demo.state.unit.Mon
import space.earlygrey.simplegraphs.algorithms.SearchStep
import java.util.function.BiPredicate
import java.util.function.Predicate

object Filter {
    @JvmStatic
    fun ofType(type: TileType): BiPredicate<GameState, Tile> {
        return BiPredicate { _, tile -> tile.type == type }
    }

    fun <T> all(vararg filters: Predicate<T>): Predicate<T> = filters.reduce { acc, predicate -> acc.and(predicate) }

    fun <T> any(vararg filters: Predicate<T>): Predicate<T> = filters.reduce { acc, predicate -> acc.or(predicate) }

    @JvmStatic
    fun <T, R> all(vararg filters: BiPredicate<T, R>): BiPredicate<T, R> =
        filters.reduce { acc, predicate -> acc.and(predicate) }

    fun <T, R> any(vararg filters: BiPredicate<T, R>): BiPredicate<T, R> =
        filters.reduce { acc, predicate -> acc.or(predicate) }

    @JvmStatic
    fun canReach(origin: Tile, filter: Predicate<SearchStep<Tile>>): BiPredicate<GameState, Tile> {
        return BiPredicate { state, tile -> !state.graph.path(origin, tile, filter).isEmpty() }
    }

    /* Movement filters - directly on SearchStep<Tile>. */
    @JvmStatic
    fun maxDistance(distance: Int): Predicate<SearchStep<Tile>> {
        return Predicate { it.distance() <= distance }
    }

    fun atDistance(distance: Int): Predicate<SearchStep<Tile>> {
        return Predicate { it.distance() == distance.toFloat() }
    }

    @JvmStatic
    fun canTravel(mon: Mon): Predicate<SearchStep<Tile>> {
        return Predicate { mon.canTravel(it.vertex()) }
    }

    @JvmStatic
    fun sameAxisAs(hex: Hex): Predicate<SearchStep<Tile>> = Predicate { it.vertex().hex.sameAxisAs(hex) }

    @JvmStatic
    fun hasUnit(): BiPredicate<GameState, Tile> =
        BiPredicate { state, tile -> state.unitAt(tile.hex) != null }
}
