package io.github.fourlastor.game.demo.round.step

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.map.Tile
import io.github.fourlastor.game.demo.state.unit.Unit

class MoveStep
@AssistedInject
constructor(
    @Assisted private val unit: Unit,
    @Assisted private val tile: Tile,
    @Assisted private val path: List<Tile>,
    @Assisted private val interpolation: Interpolation
) : SimpleStep() {
  override fun enter(state: GameState, continuation: () -> kotlin.Unit) {
    val actions: MutableList<Action> = ArrayList()
    for (pathTile in path) {
      val position = unit.coordinates.toWorldAtCenter(pathTile.hex, Vector2())
      actions.add(Actions.moveToAligned(position.x, position.y, Align.bottom, 0.25f, interpolation))
    }
    val finalTile = path[path.size - 1]
    val finalPosition = unit.coordinates.toWorldAtCenter(finalTile.hex, Vector2())
    finalPosition.x -= unit.group.width / 2f
    val steps = Actions.sequence(*actions.toTypedArray<Action>())
    unit.group.addAction(
        Actions.sequence(
            steps,
            Actions.run { unit.hex.set(tile.hex) },
            Actions.run { unit.actorPosition = finalPosition },
            Actions.run(continuation)))
  }

  @AssistedFactory
  interface Factory {
    fun create(unit: Unit, tile: Tile, path: List<Tile>, interpolation: Interpolation): MoveStep
  }
}
