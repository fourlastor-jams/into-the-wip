package io.github.fourlastor.game.coordinates

import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.math.GridPoint3
import io.github.fourlastor.game.extensions.GridPoint3s.inverse
import io.github.fourlastor.game.extensions.GridPoint3s.scl
import kotlin.math.abs

/**
 * Hex coordinates, in offset (odd-q) and cube systems.
 * See [original blog post](https://www.redblobgames.com/grids/hexagons/#coordinates).
 * Cube is useful for pathfinding, offset is what we get from tiled, and it's useful for the on-screen coordinates.
 */

data class Hex @JvmOverloads constructor(
    val offset: GridPoint2,
    val cube: GridPoint3 = toCube(offset),
) {

    fun set(other: Hex) {
        offset.set(other.offset)
        cube.set(other.cube)
    }

    fun sameAxisAs(hex: Hex): Boolean {
        return hex.cube.x == cube.x || hex.cube.y == cube.y || hex.cube.z == cube.z
    }

    fun offset(direction: Direction, amount: Int): Hex {
        return Hex(toOffset(cube.cpy().add(direction.cube.cpy().scl(amount))))
    }

    enum class Direction(x: Int, y: Int, z: Int) {
        S(0, -1, 1),
        SE(1, -1, 0),
        NE(1, 0, -1),
        N(0, +1, -1),
        NW(-1, 1, 0),
        SW(-1, 0, 1);

        val cube: GridPoint3 = GridPoint3(x, y, z)

        fun opposite(): Direction = byValue(cube.cpy().inverse())

        companion object {
            fun byValue(cube: GridPoint3): Direction = values().first { it.cube == cube }

            fun byName(value: String): Direction = values().first { it.name.equals(value, ignoreCase = true) }

            /**
             * Starting at 0 degrees = East direction, counter-clockwise.
             */
            fun fromRotation(degrees: Int): Direction = Math.floorMod(degrees, 360).let {
                when {
                    it <= 60 -> NE
                    it <= 120 -> N
                    it <= 180 -> NW
                    it <= 240 -> SW
                    it <= 300 -> S
                    else -> SE
                }
            }
        }
    }

    companion object {
        fun toCube(offset: GridPoint2): GridPoint3 {
            val q = offset.x
            val r = (offset.y - (offset.x - abs((offset.x % 2).toDouble())) / 2).toInt()
            return GridPoint3(q, r, -q - r)
        }

        fun toOffset(cube: GridPoint3): GridPoint2 {
            val x = cube.x
            val y = (cube.y + (cube.x - abs((cube.x % 2).toDouble())) / 2).toInt()
            return GridPoint2(x, y)
        }
    }
}
