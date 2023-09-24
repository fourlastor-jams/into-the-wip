package io.github.fourlastor.game.demo.round.ability

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.fourlastor.game.demo.AttackAnimation
import io.github.fourlastor.game.demo.round.Ability
import io.github.fourlastor.game.demo.round.StateRouter
import io.github.fourlastor.game.demo.round.UnitInRound
import io.github.fourlastor.game.demo.round.step.StepState
import io.github.fourlastor.game.demo.round.step.Steps
import io.github.fourlastor.game.demo.state.Filter.all
import io.github.fourlastor.game.demo.state.Filter.canReach
import io.github.fourlastor.game.demo.state.Filter.canTravel
import io.github.fourlastor.game.demo.state.Filter.hasUnit
import io.github.fourlastor.game.demo.state.Filter.maxDistance
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.unit.Mon
import java.util.function.BiPredicate

class MeleeAttackAbility @AssistedInject constructor(
    @Assisted unitInRound: UnitInRound,
    router: StateRouter,
    stateFactory: StepState.Factory,
    private val steps: Steps
) : Ability(unitInRound, router, stateFactory) {
    private val mon: Mon = unitInRound.mon

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

    private fun setupAttackAnimation(
        distance: Float,
        rotationDegrees: Float,
        target: Mon,
        source: Mon
    ): Action {
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
            null, Runnable { target.refreshHpLabel() },
            null,
            null,
            null,
            null,
            null
        )
        val scale = SCALE.cpy().scl(distance, 1f, 1f)
        return AttackAnimation.makeSequence(
            source.group,
            runnables,
            positions,
            MOVE_DURATION,
            rotationDegrees,
            scale
        )
    }

    private fun doAttackAnimation(
        originalPosition: Vector2,
        targetPosition: Vector2,
        continuation: Runnable,
        source: Mon,
        target: Mon
    ) {
        // Distance between source and target is used to scale the animation if needed.
        val distance = source.actorPosition.dst(targetPosition)
        // Angle offset of target from source.
        val rotationDegrees = calculateAngle(originalPosition, targetPosition)
        val attackAnimation = Actions.sequence(
            setupAttackAnimation(distance, rotationDegrees, target, source),
            Actions.run { source.actorPosition = originalPosition },
            Actions.run(continuation)
        )
        source.group.addAction(attackAnimation)
    }

    private fun attackMelee(target: Mon, continuation: Runnable) {
        target.changeHp(-DAMAGE)
        val originalPosition = Vector2(mon.actorPosition)
        val targetPosition = target.actorPosition
        doAttackAnimation(originalPosition, targetPosition, continuation, mon, target)
    }

    override fun createSteps(state: GameState): Builder<*> {
        val movementLogic = all(maxDistance(mon.type.speed), canTravel(mon))
        val searchLogic = all(
            canReach(state.tileAt(mon.hex), movementLogic),
            hasUnit(),
            BiPredicate { _, tile -> mon.hex != tile.hex }
        )
        return start(steps.searchUnit(searchLogic)).sequence { hex ->
            val path = state.graph.path(
                state.tileAt(mon.hex),
                state.tileAt(
                    hex
                ),
                movementLogic
            ).toList()
            val target = requireNotNull(state.unitAt(hex))
            if (path.size >= 2) {
                val tileIndex = path.size - 2
                start(steps.move(mon, path[tileIndex], path.subList(0, tileIndex + 1)))
                    .step { _, continuation -> attackMelee(target, continuation) }
            } else {
                step { attackMelee(target, it) }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(unitInRound: UnitInRound): MeleeAttackAbility
    }
}
