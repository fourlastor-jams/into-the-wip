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
import io.github.fourlastor.game.extensions.Vector2s.calculateAngle

class AttackMelee @AssistedInject constructor(
    @Assisted("source") private val source: Mon,
    @Assisted("target") private val targetMon: Mon
) : SimpleStep() {
    private fun setupAttackAnimation(distance: Float, rotationDegrees: Float): Action {
        // Base animation goes left-to-right.
        val positions = arrayOf(
            Vector3(-.25f, 0f, 0f),
            Vector3(-.125f, 0f, 0f),
            Vector3(0f, 0f, 2f),
            Vector3(2f, 0f, 0f),
            Vector3(8f, 0f, 1f),
            Vector3(8f, 0f, 0f),
            Vector3(0f, 0f, 0f),
            Vector3(0f, 0f, -1f),
            Vector3(0f, 0f, 0f),
            Vector3(0f, 0f, -2f),
            Vector3(-3f, 0f, 16f),
            Vector3(-3f, 0f, 8f),
            Vector3(-3f, 0f, 0f),
            Vector3(-3f, 0f, -8f),
            Vector3(-3f, 0f, -16f)
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
            null, Runnable { targetMon.refreshHpLabel() },
            null,
            null,
            null,
            null,
            null
        )
        val scale = SCALE.cpy().scl(distance, 1f, 1f)
        return makeSequence(source.group, runnables, positions, MOVE_DURATION, rotationDegrees, scale)
    }

    private fun doAttackAnimation(originalPosition: Vector2, targetPosition: Vector2, continuation: Runnable?) {
        // Distance between source and target is used to scale the animation if needed.
        val distance = source.actorPosition.dst(targetPosition)
        // Angle offset of target from source.
        val rotationDegrees = originalPosition.calculateAngle(targetPosition)
        val attackAnimation = Actions.sequence(
            setupAttackAnimation(distance, rotationDegrees),
            Actions.run { source.actorPosition = originalPosition },
            Actions.run(continuation)
        )
        source.group.addAction(attackAnimation)
    }

    override fun enter(state: GameState, continuation: Runnable) {
        targetMon.changeHp(-DAMAGE)
        val originalPosition = Vector2(source.actorPosition)
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
        fun create(@Assisted("source") source: Mon, @Assisted("target") target: Mon): AttackMelee
    }

    companion object {
        private const val MOVE_DURATION = 0.05f
        private val SCALE = Vector3(1f / 16f, 1f, 1f)
        private const val DAMAGE = 2

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
