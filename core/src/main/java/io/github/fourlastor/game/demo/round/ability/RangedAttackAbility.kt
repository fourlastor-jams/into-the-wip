package io.github.fourlastor.game.demo.round.ability

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.fourlastor.game.demo.round.Ability
import io.github.fourlastor.game.demo.round.StateRouter
import io.github.fourlastor.game.demo.round.UnitInRound
import io.github.fourlastor.game.demo.round.step.StepState
import io.github.fourlastor.game.demo.round.step.Steps
import io.github.fourlastor.game.demo.state.Filter.all
import io.github.fourlastor.game.demo.state.Filter.canReach
import io.github.fourlastor.game.demo.state.Filter.canTravel
import io.github.fourlastor.game.demo.state.Filter.maxDistance
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.unit.Mon
import java.util.function.BiPredicate

class RangedAttackAbility @AssistedInject constructor(
    @Assisted unitInRound: UnitInRound,
    router: StateRouter,
    stateFactory: StepState.Factory,
    private val steps: Steps,
    private val textureAtlas: TextureAtlas,
    private val stage: Stage
) : Ability(unitInRound, router, stateFactory) {
    private val source: Mon = unitInRound.mon

    private fun attackRanged(target: Mon, continuation: Runnable) {
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

    override fun createSteps(state: GameState): Builder<*> {
        val movementLogic = all(maxDistance(source.type.speed + 1), canTravel(source))
        val searchLogic = all(
            canReach(state.tileAt(source.hex), movementLogic),
            BiPredicate { _, tile -> state.unitAt(tile.hex) != null }
        )
        return start(steps.searchTile(searchLogic)).step { hex, continuation ->
            attackRanged(requireNotNull(state.unitAt(hex)), continuation)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(unitInRound: UnitInRound): RangedAttackAbility
    }
}
