package io.github.fourlastor.game.demo.state.unit

import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Label
import io.github.fourlastor.game.coordinates.Hex
import io.github.fourlastor.game.coordinates.HexCoordinates
import io.github.fourlastor.game.demo.round.faction.Faction
import io.github.fourlastor.game.demo.state.map.Tile
import io.github.fourlastor.game.demo.state.map.TileType
import io.github.fourlastor.game.ui.UnitOnMap

class Unit(
    val faction: Faction,
    val group: UnitOnMap,
    private val hpLabel: Label,
    position: GridPoint2,
    val coordinates: HexCoordinates,
    val type: UnitType
) {
  val hex = Hex(position)
  private val maxHp = 20
  private var currentHp = 0

  init {
    setHp(maxHp)
  }

  fun canTravel(tile: Tile): Boolean {
    return if (tile.type == TileType.WATER && (type.canSwim || type.canFly)) {
      true
    } else {
      tile.type.allowWalking
    }
  }

  fun inLineOfSight(tile: Tile): Boolean {
    return if (tile.type == TileType.WATER && (type.canSwim || type.canFly)) {
      true
    } else {
      tile.type.allowWalking
    }
  }

  fun changeHp(changeAmount: Int) {
    setHp(currentHp + changeAmount)
  }

  fun refreshHpLabel() {
    hpLabel.setText("HP $currentHp")
  }

  private fun setHp(hpAmount: Int) {
    currentHp = hpAmount
    refreshHpLabel()
  }

  var actorPosition: Vector2
    get() = Vector2(group.x, group.y)
    set(targetPosition) {
      group.setPosition(targetPosition.x, targetPosition.y)
    }

  fun alignHpBar() {
    hpLabel.setPosition(group.x + group.width / 2, group.y + 40)
  }
}
