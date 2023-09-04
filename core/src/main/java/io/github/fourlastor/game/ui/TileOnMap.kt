package io.github.fourlastor.game.ui

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image

class TileOnMap(region: TextureRegion) : Image(region) {
    override fun hit(x: Float, y: Float, touchable: Boolean): Actor? {
        if (touchable && this.touchable != Touchable.enabled) return null
        if (!isVisible) return null
        return if (x >= 0 && x < width && y >= 0 && y < height - 5) this else null
    }
}
