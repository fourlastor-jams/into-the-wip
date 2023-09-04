package io.github.fourlastor.game.demo.state

import io.github.fourlastor.game.coordinates.Hex
import io.github.fourlastor.game.demo.round.faction.Faction
import io.github.fourlastor.game.demo.state.map.GraphMap
import io.github.fourlastor.game.demo.state.map.Tile
import io.github.fourlastor.game.demo.state.unit.Mon
import io.github.fourlastor.game.ui.UiLayer
import java.util.function.BiPredicate
import java.util.function.Predicate

class GameState(val mons: List<Mon>, val tiles: List<Tile>, val ui: UiLayer) {
    val graph: GraphMap = GraphMap()

    init {
        for (tile in tiles) {
            graph.add(tile)
        }
        for (tile in tiles) {
            connectTiles(tile, adjacent(tile, 0, 1, -1))
            connectTiles(tile, adjacent(tile, 0, -1, 1))
            connectTiles(tile, adjacent(tile, 1, 0, -1))
            connectTiles(tile, adjacent(tile, -1, 0, 1))
            connectTiles(tile, adjacent(tile, 1, -1, 0))
            connectTiles(tile, adjacent(tile, -1, 1, 0))
        }
    }

    fun search(filter: BiPredicate<GameState, Tile>): List<Tile> =
        tiles.filter { filter.test(this, it) }

    fun alignAllHpBars() {
        for (unit in mons) {
            unit.alignHpBar()
        }
    }

    fun byFaction(faction: Faction): List<Mon> = mons.filter { it.faction == faction }

    fun tileAt(hex: Hex): Tile = tiles.first { it.hex == hex }

    fun unitAt(hex: Hex): Mon? = unitAt { it.hex == hex }

    fun unitAt(filter: Predicate<Mon>): Mon? = mons.firstOrNull { filter.test(it) }

    private fun connectTiles(tile: Tile, adjacent: Tile?) {
        if (adjacent == null) {
            return
        }
        graph.connect(tile, adjacent)
    }

    private fun adjacent(tile: Tile, x: Int, y: Int, z: Int): Tile? {
        val position = tile.hex.cube.cpy().add(x, y, z)
        // inside an iterator for loop already
        // and it would cause issues in GWT
        for (i in tiles.indices) {
            val it = tiles[i]
            if (it.hex.cube == position) {
                return it
            }
        }
        return null
    }
}
