package io.github.fourlastor.game.demo.round.ability

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.github.tommyettinger.ds.ObjectList
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
import io.github.fourlastor.game.demo.state.Filter.ofType
import io.github.fourlastor.game.demo.state.Filter.sameAxisAs
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.map.Tile
import io.github.fourlastor.game.demo.state.map.TileType
import io.github.fourlastor.game.demo.state.unit.Mon
import java.util.function.Predicate

class TileSmashAbility @AssistedInject constructor(
    @Assisted unitInRound: UnitInRound,
    router: StateRouter,
    stateFactory: StepState.Factory,
    private val steps: Steps,
    private val textureAtlas: TextureAtlas
) : Ability(unitInRound, router, stateFactory) {
    private val mon = unitInRound.mon

    companion object {
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

        private var MOVE_DURATION = 0.05f
        private val SCALE = Vector3(1f / 16f, 1f, 1f)
    }

    private fun setupAttackAnimation(
        source: Mon,
        target: Tile?,
        distance: Float,
        rotationDegrees: Float
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
            null,
            Runnable {
                if (target != null) {
                    val region: TextureRegion = textureAtlas.findRegion("tiles/white")
                    target.actor.setDrawable(TextureRegionDrawable(region))
                    target.actor.setSize(target.actor.getPrefWidth(), target.actor.getPrefHeight())
                }
            },
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

    private fun tileSmash(target: Tile, continuation: Runnable) {
        target.type = TileType.TERRAIN
        val originalPosition = Vector2(mon.actorPosition)
        val targetPosition: Vector2 = target.actorPosition
        // Distance between mon and target is used to scale the animation if needed.
        val distance = mon.actorPosition.dst(targetPosition)
        // Angle offset of target from mon.
        val rotationDegrees = calculateAngle(originalPosition, targetPosition)
        val attackAnimation = Actions.sequence(
            setupAttackAnimation(mon, target, distance, rotationDegrees),
            Actions.run { mon.hex.set(target.hex) }, // (sheerst) Note: model code, likely shouldn't happen here?
            Actions.run { mon.actorPosition = originalPosition },
            Actions.run(continuation)
        )
        mon.group.addAction(attackAnimation)
    }

    override fun createSteps(state: GameState): Builder<*> {
        val movementLogic = all(
            sameAxisAs(mon.hex),
            Predicate { step -> mon.canTravel(step.previous()) && step.vertex().type === TileType.SOLID }
        )
        val searchLogic = all(canReach(state.tileAt(mon.hex), movementLogic), ofType(TileType.SOLID))
        return start(steps.searchTile(searchLogic)).sequence { hex ->
            val path: List<Tile> = ObjectList(
                state.graph.path(
                    state.tileAt(mon.hex),
                    state.tileAt(hex),
                    movementLogic
                )
            )
            val target = state.tileAt(hex)
            if (path.size >= 2) {
                start(steps.move(mon, path[path.size - 2], path, Interpolation.linear))
                    .step { _, continuation ->
                        tileSmash(target, continuation)
                    }
            } else {
                step { tileSmash(target, it) }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(unitInRound: UnitInRound): TileSmashAbility
    }
}
