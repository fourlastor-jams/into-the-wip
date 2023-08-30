package io.github.fourlastor.game.ui

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.utils.Align

class UnitOnMap(region: TextureRegion) : WidgetGroup() {
  val image: Image

  init {
    image = Image(region)
    image.setAlign(Align.bottom)
    // Center the image horizontally.
    val imageX = width / 2 - image.width / 2
    image.setPosition(imageX, image.y)
    addActor(image)
  }
}
