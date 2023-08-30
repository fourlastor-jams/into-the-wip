package io.github.fourlastor.game.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.utils.SnapshotArray

class YSort : WidgetGroup() {
  fun sortChildren() {
    val children = children
    val unitChildren = SnapshotArray<Actor>()
    val tileChildren = SnapshotArray<Actor>()
    for (child in children) {
      when (child) {
        is UnitOnMap -> unitChildren.add(child)
        is TileOnMap -> tileChildren.add(child)
        is Label -> unitChildren.add(child)
      }
    }
    unitChildren.sort(COMPARATOR)
    tileChildren.sort(COMPARATOR)
    children.clear()
    children.addAll(unitChildren)
    children.addAll(tileChildren)
  }

  companion object {
    private val COMPARATOR = Comparator.comparing({ actor: Actor -> -actor.y }, Float::compareTo)
  }
}
