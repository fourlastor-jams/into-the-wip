package io.github.fourlastor.game.demo.state

import io.github.fourlastor.game.coordinates.Hex
import io.github.fourlastor.game.demo.state.map.Tile
import io.github.fourlastor.game.demo.state.map.TileType
import io.github.fourlastor.game.demo.state.unit.Unit
import java.util.*
import java.util.function.BiPredicate
import java.util.function.Predicate
import space.earlygrey.simplegraphs.algorithms.SearchStep

interface Filter {
  companion object {
    fun ofType(type: TileType): BiPredicate<GameState, Tile> {
      return BiPredicate { _, tile -> tile.type == type }
    }

    fun <T> all(vararg filters: Predicate<T>): Predicate<T> =
        filters.reduce { acc, predicate -> acc.and(predicate) }

    fun <T> any(vararg filters: Predicate<T>): Predicate<T> =
        filters.reduce { acc, predicate -> acc.or(predicate) }

    fun <T, R> all(vararg filters: BiPredicate<T, R>): BiPredicate<T, R> =
        filters.reduce { acc, predicate -> acc.and(predicate) }

    fun <T, R> any(vararg filters: BiPredicate<T, R>): BiPredicate<T, R> =
        filters.reduce { acc, predicate -> acc.or(predicate) }

    fun canReach(origin: Tile, filter: Predicate<SearchStep<Tile>>): BiPredicate<GameState, Tile> {
      return BiPredicate { state, tile -> !state.graph.path(origin, tile, filter).isEmpty() }
    }

    /* Movement filters - directly on SearchStep<Tile>. */
    fun maxDistance(distance: Int): Predicate<SearchStep<Tile>> {
      return Predicate { step -> step.distance() <= distance }
    }

    fun atDistance(distance: Int): Predicate<SearchStep<Tile>> {
      return Predicate { step -> step.distance() == distance.toFloat() }
    }

    fun canTravel(unit: Unit): Predicate<SearchStep<Tile>> {
      return Predicate { step: SearchStep<Tile> -> unit.canTravel(step.vertex()) }
    }

    fun sameAxisAs(hex: Hex): Predicate<SearchStep<Tile>> {
      return Predicate { step -> step.vertex().hex.sameAxisAs(hex) }
    }

    fun hasUnit(): BiPredicate<GameState, Tile> {
      return BiPredicate { state: GameState, tile: Tile -> state.unitAt(tile.hex) != null }
    }
  }
}
