package io.github.fourlastor.game.coordinates

import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.math.GridPoint3
import kotlin.math.abs

/**
 * Hex coordinates, in offset (odd-q) and cube systems. See
 * [original blog post](https://www.redblobgames.com/grids/hexagons/#coordinates). Cube is useful
 * for pathfinding, offset is what we get from tiled, and it's useful for the on-screen coordinates.
 */
data class Hex(
    val offset: GridPoint2,
    val cube: GridPoint3 = toCube(offset),
) {
  fun isOnSameAxisAs(other: Hex): Boolean =
      cube.x == other.cube.x || cube.y == other.cube.y || cube.z == other.cube.z

  fun set(other: Hex) {
    offset.set(other.offset)
    cube.set(other.cube)
  }

  fun sameAxisAs(hex: Hex): Boolean =
      hex.cube.x == cube.x || hex.cube.y == cube.y || hex.cube.z == cube.z

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
