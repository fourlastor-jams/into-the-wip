package io.github.fourlastor.game.coordinates

import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.math.Vector2

object IsometricCoordinates {
  private const val TILE_WIDTH = 64
  private const val HALF_WIDTH = TILE_WIDTH / 2f
  private const val TILE_HEIGHT = 32
  private const val HALF_HEIGHT = TILE_HEIGHT / 2f
  private const val TILE_THICKNESS = 16
  private const val ORIGIN_BOTTOM = 2
  private const val ORIGIN_LEFT = 315
  fun toWorldAtOrigin(map: GridPoint2, left: Int, bottom: Int): Vector2 {
    return Vector2(left.toFloat(), bottom.toFloat())
        .add((map.x - map.y) * HALF_WIDTH, (map.x + map.y) * HALF_HEIGHT)
  }

  fun toWorldAtCenter(map: GridPoint2): Vector2 {
    return toWorldAtOrigin(map, ORIGIN_LEFT, ORIGIN_BOTTOM)
        .add(HALF_WIDTH, TILE_THICKNESS + HALF_HEIGHT)
  }
}
