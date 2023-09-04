package io.github.fourlastor.game.demo.state.map

import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.math.Vector2
import io.github.fourlastor.game.coordinates.Hex
import io.github.fourlastor.game.ui.TileOnMap

class Tile(var actor: TileOnMap, position: GridPoint2, var type: TileType) {
    val hex: Hex

    init {
        hex = Hex(position)
        if (type == TileType.WATER) {
            actor.setColor(1f, 1f, 1f, .9f)
        }
    }

    var actorPosition: Vector2
        get() = Vector2(actor.x, actor.y)
        set(targetPosition) {
            actor.setPosition(targetPosition.x, targetPosition.y)
        }
}
