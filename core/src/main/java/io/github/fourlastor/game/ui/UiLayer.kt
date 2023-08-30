package io.github.fourlastor.game.ui

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table

class UiLayer(atlas: TextureAtlas) : Table() {
  val meleeAttack: Image
  val move: Image
  val tileSmash: Image
  val rangedAttack: Image

  // TODO: Remove atlas from here
  init {
    setFillParent(true)
    defaults().bottom().expandY().pad(2f)
    meleeAttack = Image(atlas.findRegion("abilities/buffs/attack_boost"))
    add(meleeAttack)
    move = Image(atlas.findRegion("abilities/buffs/swiftness"))
    add(move)
    tileSmash = Image(atlas.findRegion("abilities/spells/tile_smash"))
    add(tileSmash)
    rangedAttack = Image(atlas.findRegion("abilities/spells/ranged_attack"))
    add(rangedAttack)
  }
}
