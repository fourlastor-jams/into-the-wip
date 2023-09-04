package io.github.fourlastor.game.demo.round.step

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.fourlastor.game.demo.AttackAnimation.makeSequence
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.map.Tile
import io.github.fourlastor.game.demo.state.unit.Unit
import java.util.function.Consumer

class BlobToss @AssistedInject constructor(
    @Assisted("source") private val source: Unit,
    @Assisted("target") private val targetUnit: Unit,
    @Assisted("tile") private val targetTile: Tile
) : SimpleStep() {
    private fun setupAttackAnimation(state: GameState, distance: Float, rotationDegrees: Float): Action {
        // Base animation goes left-to-right.
        val positions = arrayOf(
            Vector3(1f, 0f, -.5f),
            Vector3(1f, 0f, -.4f),
            Vector3(1f, 0f, -.3f),
            Vector3(1f, 0f, -.2f),
            Vector3(1f, 0f, -.1f),
            Vector3(1f, 0f, 0f),
            Vector3(1f, 0f, 0f),
            Vector3(1f, 0f, -.1f),
            Vector3(1f, 0f, -.2f),
            Vector3(1f, 0f, -.3f),
            Vector3(1f, 0f, -.4f),
            Vector3(1f, 0f, -.5f)
        )
        val runnables = arrayOf(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null, // Damage all mons on that tile.
            Runnable { state.units.forEach(Consumer { unit: Unit -> unit.changeHp(-2) }) }
        )
        val scale = SCALE.cpy().scl(distance, 1f, 1f)
        return makeSequence(
            targetUnit.group,
            runnables,
            positions,
            MOVE_DURATION,
            rotationDegrees,
            scale
        )
    }

    private fun doAttackAnimation(
        state: GameState,
        originalPosition: Vector2,
        targetPosition: Vector2,
        continuation: Runnable
    ) {
        // Distance between source and target is used to scale the animation if needed.
        val distance = source.actorPosition.dst(targetPosition)
        // Angle offset of target from source.
        val rotationDegrees = calculateAngle(originalPosition, targetPosition)
        val attackAnimation = Actions.sequence(
            setupAttackAnimation(state, distance, rotationDegrees), // Move the target unit to the source's Tile.
            Actions.run { targetUnit.hex.set(targetTile.hex) },
            Actions.run { targetUnit.actorPosition = targetPosition },
            Actions.run(continuation)
        )
        targetUnit.group.addAction(attackAnimation)
    }

    override fun enter(state: GameState, continuation: Runnable) {
        val originalPosition = source.actorPosition
        val targetPosition = targetUnit.actorPosition
        doAttackAnimation(state, originalPosition, targetPosition, continuation)
    }

    override fun exit(state: GameState) {
        // optional cleanup
    }

    /**
     * Factory interface for creating instances of the AttackMelee class.
     */
    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("source") source: Unit,
            @Assisted("target") targetUnit: Unit,
            @Assisted("tile") targetTile: Tile
        ): BlobToss
    }

    companion object {
        private const val MOVE_DURATION = 0.05f
        private val SCALE = Vector3(1f / 16f, 1f, 1f)

        /**
         * Calculate the angle in degrees between two 2D vectors.
         *
         * @param source The source vector.
         * @param target The target vector.
         * @return The angle in degrees between the vectors.
         */
        fun calculateAngle(source: Vector2, target: Vector2): Float {
            return target.cpy().sub(source).angleDeg()
        }
    }
}
