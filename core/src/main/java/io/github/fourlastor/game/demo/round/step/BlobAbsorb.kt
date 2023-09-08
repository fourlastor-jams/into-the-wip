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
import io.github.fourlastor.game.demo.state.unit.Mon
import io.github.fourlastor.game.demo.state.unit.effect.BlobAbsorbSourceEffect
import io.github.fourlastor.game.demo.state.unit.effect.BlobAbsorbTargetEffect
import io.github.fourlastor.game.extensions.Vector2s.calculateAngle

/**
 * Ideas/notes:
 * - Blobanera can only move one space while in absorb mode
 * - Option - could have blobanero move into the target unit instead of vice-versa.
 * - Might be a little more balanced.
 */
class BlobAbsorb @AssistedInject constructor(
    @Assisted("source") private val source: Mon,
    @Assisted("target") private val targetMon: Mon
) : SimpleStep() {
    private fun setupAttackAnimation(distance: Float, rotationDegrees: Float): Action {
        // Base animation goes left-to-right.
        val positions = arrayOf(
            Vector3(-.125f, 0f, 0f),
            Vector3(-.125f, 0f, 0f),
            Vector3(-.125f, 0f, 0f),
            Vector3(-.125f, 0f, 0f),
            Vector3(-.125f, 0f, 0f),
            Vector3(-.5f, 0f, 0f),
            Vector3(-.5f, 0f, 0f),
            Vector3(-.5f, 0f, 0f),
            Vector3(-1f, 0f, 0f),
            Vector3(-1f, 0f, 0f),
            Vector3(-1f, 0f, 0f),
            Vector3(-2f, 0f, 0f),
            Vector3(-8f, 0f, 0f)
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
            null,
            Runnable { targetMon.group.image.setColor(.5f, 1f, .5f, 1f) }
        )
        val scale = SCALE.cpy().scl(distance, 1f, 1f)
        return makeSequence(
            targetMon.group,
            runnables,
            positions,
            MOVE_DURATION,
            rotationDegrees,
            scale
        )
    }

    private fun doAttackAnimation(originalPosition: Vector2, targetPosition: Vector2, continuation: Runnable?) {
        // Distance between source and target is used to scale the animation if needed.
        val distance = source.actorPosition.dst(targetPosition)
        // Angle offset of target from source.
        val rotationDegrees = originalPosition.calculateAngle(targetPosition)
        val attackAnimation = Actions.sequence(
            setupAttackAnimation(distance, rotationDegrees), // Move the target unit to the source's Tile.
            Actions.run { targetMon.hex.set(source.hex) },
            Actions.run { targetMon.actorPosition = originalPosition }!!,
            Actions.run {
                // Apply the Blob Absorb effects on the source and target mons.
                source.addEffect(BlobAbsorbSourceEffect(targetMon), -1)
                targetMon.addEffect(BlobAbsorbTargetEffect(source), -1)
            },
            Actions.run(continuation)
        )
        targetMon.group.addAction(attackAnimation)
    }

    override fun enter(state: GameState, continuation: Runnable) {
        val originalPosition = source.actorPosition
        val targetPosition = targetMon.actorPosition
        doAttackAnimation(originalPosition, targetPosition, continuation)
    }

    override fun exit(state: GameState) {
        // optional cleanup
    }

    /**
     * Factory interface for creating instances of the AttackMelee class.
     */
    @AssistedFactory
    interface Factory {
        fun create(@Assisted("source") source: Mon, @Assisted("target") target: Mon): BlobAbsorb
    }

    companion object {
        private const val MOVE_DURATION = 0.05f
        private val SCALE = Vector3(1f / 16f, 1f, 1f)
    }
}
