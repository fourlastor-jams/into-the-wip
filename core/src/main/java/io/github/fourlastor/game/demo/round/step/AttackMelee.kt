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
import io.github.fourlastor.game.demo.AttackAnimation
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.map.Tile
import io.github.fourlastor.game.demo.state.map.TileType
import io.github.fourlastor.game.demo.state.unit.Unit

class AttackMelee
@AssistedInject
constructor(
    @param:Assisted("source") private val source: Unit,
    @param:Assisted("target") private val targetUnit: Unit,
    private val textureAtlas: TextureAtlas,
) : SimpleStep() {
  private val targetTile: Tile? = null
  private val moveDuration = 0.05f
  // Amount to scale the animation by.
  private val scale = Vector3(1f / 16f, 1f, 1f)
  private val damage = 2
  private fun setupAttackAnimation(distance: Float, rotationDegrees: Float): Action {
    // Base animation goes left-to-right.
    val positions =
        arrayOf(
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
            Vector3(-3f, 0f, -16f))
    val runnables =
        arrayOf(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            Runnable { targetUnit.refreshHpLabel() },
            null,
            null,
            null,
            null,
            null)
    val scale = scale.cpy().scl(distance, 1f, 1f)
    return AttackAnimation.makeSequence(
        source.group, runnables, positions, moveDuration, rotationDegrees, scale)
  }

  fun doAttackAnimation(
      originalPosition: Vector2,
      targetPosition: Vector2,
      continuation: Runnable
  ) {
    // Distance between source and target is used to scale the animation if needed.
    val distance = source.actorPosition.dst(targetPosition)
    // Angle offset of target from source.
    val rotationDegrees = calculateAngle(originalPosition, targetPosition)
    val attackAnimation =
        Actions.sequence(
            setupAttackAnimation(distance, rotationDegrees),
            Actions.run { source.actorPosition = originalPosition },
            Actions.run(continuation))
    source.group.addAction(attackAnimation)
  }

  fun attackUnit(continuation: Runnable) {
    targetUnit.changeHp(-damage)
    val originalPosition = Vector2(source.actorPosition)
    val targetPosition = targetUnit.actorPosition
    doAttackAnimation(originalPosition, targetPosition, continuation)
  }

  fun smashTile(continuation: Runnable) {
    targetTile!!.type = TileType.TERRAIN
    val region: TextureRegion = textureAtlas.findRegion("tiles/white")
    targetTile.actor.setDrawable(TextureRegionDrawable(region))
    targetTile.actor.setSize(targetTile.actor.getPrefWidth(), targetTile.actor.getPrefHeight())
    val originalPosition = Vector2(source.actorPosition)
    val targetPosition = targetTile.actorPosition
    doAttackAnimation(originalPosition, targetPosition, continuation)
  }

  override fun enter(state: GameState, continuation: () -> kotlin.Unit) {
    if (targetUnit != null) {
      attackUnit(continuation)
    } else if (targetTile != null) {
      smashTile(continuation)
    }
  }

  override fun exit(state: GameState) {
    // optional cleanup
  }

  /** Factory interface for creating instances of the AttackMelee class. */
  @AssistedFactory
  interface Factory {
    fun create(@Assisted("source") source: Unit, @Assisted("target") target: Unit): AttackMelee
  }

  companion object {
    /**
     * Calculate the angle in degrees between two 2D vectors.
     *
     * @param source The source vector.
     * @param target The target vector.
     * @return The angle in degrees between the vectors.
     */
    fun calculateAngle(source: Vector2?, target: Vector2?): Float {
      return target!!.cpy().sub(source).angleDeg()
    }
  }
}
