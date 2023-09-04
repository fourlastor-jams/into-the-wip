package io.github.fourlastor.game.demo.round.step

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
import io.github.fourlastor.game.demo.AttackAnimation.makeSequence
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.map.Tile
import io.github.fourlastor.game.demo.state.map.TileType
import io.github.fourlastor.game.demo.state.unit.Mon
import io.github.fourlastor.game.extensions.Vector2s.calculateAngle

class SummonMountain @AssistedInject constructor(
    @Assisted("source") private val source: Mon,
    @Assisted("target") private val target: Tile,
    var textureAtlas: TextureAtlas
) : SimpleStep() {
    var moveDuration = 0.025f

    // Amount to scale the animation by.
    private val scale = Vector3(1 / 8f, 1f, 1f)
    private var facingDirection: Hex.Direction? = null
    private var hitMon: Mon? = null
    private fun setupAttackAnimation(rotationDegrees: Float): Action {
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
                if (facingDirection === Hex.Direction.N) regionName = "tiles/summon_mountain_N_mockup"
                val region: TextureRegion = textureAtlas.findRegion(regionName)
                target.actor.setDrawable(TextureRegionDrawable(region))
                target.actor.setSize(target.actor.getPrefWidth(), target.actor.getPrefHeight())

                // Visually move the Unit to a new Tile.
                if (hitMon != null) {
                    // (sheerst) Note: I could see us wanting to trigger animations the visual part of other
                    // abilities here.
                    val position = hitMon!!.coordinates.toWorldAtCenter(hitMon!!.hex, Vector2())
                    hitMon!!.group.addAction(
                        Actions.moveToAligned(position.x, position.y, Align.bottom, 0.25f, Interpolation.sineOut)
                    )
                }
            }
        )
        return makeSequence(source.group, runnables, positions, moveDuration, rotationDegrees, scale)
    }

    override fun enter(state: GameState, continuation: Runnable) {
        target.type = TileType.SOLID // Model code
        val originalPosition = Vector2(source.group.x, source.group.y)
        val targetPosition = target.actorPosition
        // Angle offset of target from source.
        val rotationDegrees = originalPosition.calculateAngle(targetPosition)
        facingDirection = Hex.Direction.fromRotation(rotationDegrees.toInt())
        println(facingDirection)

        // Move units on target tile off of it.
        hitMon = state.unitAt(target.hex) // Model code
        if (hitMon != null) {
            hitMon!!.hex.set(hitMon!!.hex.offset(facingDirection!!, 1)) // Model code
        }
        val attackAnimation = Actions.sequence(
            setupAttackAnimation(rotationDegrees),
            Actions.run { source.actorPosition = originalPosition },
            Actions.run(continuation)
        )
        source.group.addAction(attackAnimation)
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("source") source: Mon, @Assisted("target") target: Tile): SummonMountain
    }
}
