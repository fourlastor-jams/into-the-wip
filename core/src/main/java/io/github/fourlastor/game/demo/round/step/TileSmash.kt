package io.github.fourlastor.game.demo.round.step

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.fourlastor.game.demo.AttackAnimation.makeSequence
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.map.Tile
import io.github.fourlastor.game.demo.state.map.TileType
import io.github.fourlastor.game.demo.state.unit.Unit

class TileSmash @AssistedInject constructor(
    @Assisted("source") private val source: Unit,
    @Assisted("target") private val target: Tile?,
    private val textureAtlas: TextureAtlas
) : SimpleStep() {
    private var moveDuration = 0.05f

    // Amount to scale the animation by.
    private val scale = Vector3(1f / 16f, 1f, 1f)
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
        val scale = scale.cpy().scl(distance, 1f, 1f)
        return makeSequence(source.group, runnables, positions, moveDuration, rotationDegrees, scale)
    }

    override fun enter(state: GameState, continuation: Runnable) {
        target!!.type = TileType.TERRAIN
        val originalPosition = Vector2(source.actorPosition)
        val targetPosition: Vector2 = target.actorPosition
        // Distance between source and target is used to scale the animation if needed.
        val distance = source.actorPosition.dst(targetPosition)
        // Angle offset of target from source.
        val rotationDegrees = calculateAngle(originalPosition, targetPosition)
        val attackAnimation = Actions.sequence(
            setupAttackAnimation(distance, rotationDegrees),
            Actions.run { source.hex.set(target.hex) }, // (sheerst) Note: model code, likely shouldn't happen here?
            Actions.run { source.actorPosition = originalPosition },
            Actions.run(continuation)
        )
        source.group.addAction(attackAnimation)
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("source") source: Unit, @Assisted("target") target: Tile): TileSmash
    }

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
    }
}
