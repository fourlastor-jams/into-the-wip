package io.github.fourlastor.game.coordinates

import com.badlogic.gdx.math.GridPoint2

object Packer {
  fun pack(position: GridPoint2): Int {
    return position.x shl 16 or position.y
  }

  fun unpack(value: Int, target: GridPoint2): GridPoint2 {
    return target.set(value shr 16, value.toShort().toInt())
  }
}
