package io.github.fourlastor.game.coordinates

import com.badlogic.gdx.math.Vector2

class HexCoordinates(private val width: Int, tileHeight: Int, hexSideLength: Int) {
    private val height: Int
    private val horizontalSpacing: Float
    private val staggerSpacing: Float

    init {
        height = tileHeight - 1
        horizontalSpacing = (width - hexSideLength) / 2f + hexSideLength - 1
        staggerSpacing = height / 2f
    }

    fun toWorldAtOrigin(x: Int, y: Int, out: Vector2): Vector2 {
        val stagger = if (x % 2 == 0) 0f else staggerSpacing
        return out.set(x * horizontalSpacing, stagger + y * height)
    }

    fun toWorldAtCenter(hex: Hex): Vector2 {
        return toWorldAtCenter(hex.offset.x, hex.offset.y, Vector2())
    }

    fun toWorldAtCenter(hex: Hex, out: Vector2): Vector2 {
        return toWorldAtCenter(hex.offset.x, hex.offset.y, out)
    }

    fun toWorldAtCenter(x: Int, y: Int): Vector2 {
        return toWorldAtCenter(x, y, Vector2())
    }

    fun toWorldAtCenter(x: Int, y: Int, out: Vector2): Vector2 {
        return toWorldAtOrigin(x, y, out).add(width / 2f, height / 2f)
    }
}
