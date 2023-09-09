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
import io.github.fourlastor.game.demo.state.unit.Mon
import io.github.fourlastor.game.demo.state.unit.effect.BlobAbsorbSourceEffect
import io.github.fourlastor.game.demo.state.unit.effect.BlobAbsorbTargetEffect

class MoveStep @AssistedInject constructor(
    @Assisted private val mon: Mon,
    @Assisted private val tile: Tile,
    @Assisted private val path: List<Tile>,
    @Assisted private val interpolation: Interpolation,
) : SimpleStep() {
    override fun enter(state: GameState, continuation: Runnable) {
        val actions: MutableList<Action> = ArrayList()
        for (pathTile in path) {
            val position = mon.coordinates.toWorldAtCenter(pathTile.hex, Vector2())
            actions.add(Actions.moveToAligned(position.x, position.y, Align.bottom, 0.25f, interpolation))
        }
        val finalTile = path[path.size - 1]
        val finalPosition = mon.coordinates.toWorldAtCenter(finalTile.hex, Vector2())
        finalPosition.x -= mon.group.width / 2f
        val steps = Actions.sequence(*actions.toTypedArray<Action>())
        val moveAction = Actions.sequence(
            steps,
            Actions.run { mon.hex.set(tile.hex) }, // (sheerst) Note: model code, likely shouldn't happen here?
            Actions.run { mon.actorPosition = finalPosition },
            Actions.run(continuation)
        )
        mon.group.addAction(moveAction)

        // Check if this mon is involved with a Blob Absorb effect.
        // Move the source or target Mon if true.
        for (effect in mon.getEffects().keys()) {
            if (effect is BlobAbsorbSourceEffect) {
                effect.targetMon.group.addAction(moveAction)
            }
            if (effect is BlobAbsorbTargetEffect) {
                effect.targetMon.group.addAction(moveAction)
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(mon: Mon, tile: Tile, path: List<Tile>, interpolation: Interpolation): MoveStep
    }
}
