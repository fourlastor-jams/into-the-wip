package io.github.fourlastor.game.demo.round.step

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.unit.Unit

class AttackRanged @AssistedInject constructor(
    @Assisted("source") private val source: Unit,
    @Assisted("target") private val target: Unit,
    private val textureAtlas: TextureAtlas,
    private val stage: Stage
) : SimpleStep() {
    override fun enter(state: GameState, continuation: Runnable) {
        // (sheerst) Note: this is model code, does it go here?
        target.changeHp(-1)

        // Distance between source and target is used to scale the animation if needed.
        val distance = source.actorPosition.dst(target.actorPosition)

        // Create a projectile. Add an action that will animate it to the target.
        val sourcePos = source.actorPosition.add(source.group.width / 2, source.group.height / 2)
        val targetPos = target.actorPosition.add(target.group.width / 2, target.group.height / 2)
        val projectile = Image(
            textureAtlas.findRegion("ball1")
        )
        projectile.setPosition(sourcePos.x, sourcePos.y)
        val moveAnimation = Actions.sequence()
        moveAnimation.addAction(Actions.moveTo(targetPos.x, targetPos.y, distance / 400))
        moveAnimation.addAction(Actions.run { target.refreshHpLabel() })
        moveAnimation.addAction(Actions.run(continuation))
        moveAnimation.addAction(Actions.run { projectile.remove() })
        projectile.addAction(moveAnimation)
        stage.addActor(projectile)
    }

    /**
     * Factory interface for creating instances of the AttackRanged class.
     */
    @AssistedFactory
    interface Factory {
        fun create(@Assisted("source") source: Unit, @Assisted("target") target: Unit): AttackRanged
    }
}
