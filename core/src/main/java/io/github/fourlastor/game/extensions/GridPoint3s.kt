package io.github.fourlastor.game.extensions

import com.badlogic.gdx.math.GridPoint3

object GridPoint3s {

    // New stuff.
    fun GridPoint3.inverse() = apply {
        x = -x
        y = -y
        z = -z
    }

    fun GridPoint3.scl(amount: Int) = apply {
        x *= amount
        y *= amount
        z *= amount
    }
}
