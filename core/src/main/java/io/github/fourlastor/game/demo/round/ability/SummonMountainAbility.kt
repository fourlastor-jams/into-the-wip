package io.github.fourlastor.game.demo.round.ability

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.fourlastor.game.coordinates.Hex
import io.github.fourlastor.game.demo.AttackAnimation
import io.github.fourlastor.game.demo.round.Ability
import io.github.fourlastor.game.demo.round.StateRouter
import io.github.fourlastor.game.demo.round.UnitInRound
import io.github.fourlastor.game.demo.round.step.StepState
import io.github.fourlastor.game.demo.round.step.Steps
import io.github.fourlastor.game.demo.state.Filter.all
import io.github.fourlastor.game.demo.state.Filter.canReach
import io.github.fourlastor.game.demo.state.Filter.maxDistance
import io.github.fourlastor.game.demo.state.Filter.sameAxisAs
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.map.Tile
import io.github.fourlastor.game.demo.state.map.TileType
import io.github.fourlastor.game.demo.state.unit.Mon
import io.github.fourlastor.game.extensions.Vector2s.calculateAngle
import java.util.function.BiPredicate

class SummonMountainAbility @AssistedInject constructor(
    @Assisted unitInRound: UnitInRound,
    router: StateRouter,
    stateFactory: StepState.Factory,
    private val steps: Steps,
    private val textureAtlas: TextureAtlas
) : Ability(unitInRound, router, stateFactory) {
    companion object {
        private const val MOVE_DURATION = 0.025f
        private val scale = Vector3(1 / 8f, 1f, 1f)
    }

    private val source = unitInRound.mon

    private fun setupAttackAnimation(
        source: Mon,
        target: Tile,
        direction: Hex.Direction?,
        hitMon: Mon?,
        rotationDegrees: Float
    ): Action {
        // Base animation goes left-to-right.
        val positions = arrayOf(
            Vector3(-.25f, 4f, 0f),
            Vector3(-.25f, 4f, 0f),
            Vector3(-.25f, 4f, 0f),
            Vector3(-.25f, 4f, 0f),
            Vector3(0f, 0f, 0f),
            Vector3(0f, 0f, 0f),
            Vector3(0f, 0f, 0f),
            Vector3(0f, 0f, 0f),
            Vector3(0f, 0f, 0f),
            Vector3(0f, 0f, 0f),
            Vector3(0f, 0f, 0f),
            Vector3(0f, 0f, 0f),
            Vector3(0f, 0f, 0f),
            Vector3(0f, 0f, 0f),
            Vector3(.5f, -8f, 0f),
            Vector3(.5f, -8f, 0f)
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
            null,
            null,
            null,
            null,
            Runnable {
                // (sheerst) Note: likely need to use N/S/E/W etc as keys in the texture atlas.
                // Something like .findRegion(tiles/summon_mountain_mockup/NW)
                // The texture atlas should return a default image if image doesn't exist(?)
                var regionName = "tiles/summon_mountain_NW_mockup"
                if (direction === Hex.Direction.N) regionName = "tiles/summon_mountain_N_mockup"
                val region: TextureRegion = textureAtlas.findRegion(regionName)
                target.actor.setDrawable(TextureRegionDrawable(region))
                target.actor.setSize(target.actor.getPrefWidth(), target.actor.getPrefHeight())

                // Visually move the Unit to a new Tile.
                if (hitMon != null) {
                    // (sheerst) Note: I could see us wanting to trigger animations the visual part of other
                    // abilities here.
                    val position = hitMon.coordinates.toWorldAtCenter(hitMon.hex, Vector2())
                    hitMon.group.addAction(
                        Actions.moveToAligned(position.x, position.y, Align.bottom, 0.25f, Interpolation.sineOut)
                    )
                }
            }
        )
        return AttackAnimation.makeSequence(
            source.group,
            runnables,
            positions,
            MOVE_DURATION,
            rotationDegrees,
            scale
        )
    }

    override fun createSteps(state: GameState): Builder<*> {
        val movementLogic = all(sameAxisAs(source.hex), maxDistance(source.type.speed))
        val searchLogic = all(
            canReach(state.tileAt(source.hex), movementLogic),
            BiPredicate { _, tile -> source.canTravel(tile) }
        )
        return start(steps.searchTile(searchLogic)).step { result, continuation ->
            val target = state.tileAt(result)
            target.type = TileType.SOLID // Model code
            val originalPosition = Vector2(source.group.x, source.group.y)
            val targetPosition = target.actorPosition
            // Angle offset of target from source.
            val rotationDegrees = originalPosition.calculateAngle(targetPosition)
            val facingDirection = Hex.Direction.fromRotation(rotationDegrees.toInt())

            // Move units on target tile off of it.
            val hitMon = state.unitAt(target.hex) // Model code
            hitMon?.hex?.set(hitMon.hex.offset(facingDirection, 1))
            val attackAnimation = Actions.sequence(
                setupAttackAnimation(source, target, facingDirection, hitMon, rotationDegrees),
                Actions.run { source.actorPosition = originalPosition },
                Actions.run(continuation)
            )
            source.group.addAction(attackAnimation)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(unitInRound: UnitInRound): SummonMountainAbility
    }
}
