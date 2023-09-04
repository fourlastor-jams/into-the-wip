package io.github.fourlastor.game.extensions

import com.badlogic.gdx.math.Vector2

object Vector2s {

    /**
     * Calculate the angle in degrees between two 2D vectors.
     * .
     * @param target The target vector.
     * @return The angle in degrees between this vector and the target.
     */
    fun Vector2.calculateAngle(target: Vector2): Float = target.cpy().sub(this).angleDeg()
}
