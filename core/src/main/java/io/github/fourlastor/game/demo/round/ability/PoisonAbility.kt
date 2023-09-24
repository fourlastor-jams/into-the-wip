package io.github.fourlastor.game.demo.round.ability

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions
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
import io.github.fourlastor.game.demo.state.unit.effect.PoisonEffect
import java.util.function.BiPredicate

class PoisonAbility @AssistedInject constructor(
    @Assisted unitInRound: UnitInRound,
    router: StateRouter,
    stateFactory: StepState.Factory,
    private val steps: Steps,
    private val poisonEffect: PoisonEffect
) : Ability(unitInRound, router, stateFactory) {
    private val mon: Mon = unitInRound.mon

    override fun createSteps(state: GameState): Builder<*> {
        val movementLogic = all(maxDistance(mon.type.speed + 1), canTravel(mon))
        val searchLogic = all(
            canReach(state.tileAt(mon.hex), movementLogic),
            BiPredicate { _, tile -> state.unitAt(tile.hex) != null }
        )
        return start(steps.searchTile(searchLogic)).step { hex, continuation ->
            val target = requireNotNull(state.unitAt(hex))
            target.group.image.addAction(
                Actions.sequence(
                    Actions.color(Color.OLIVE, 0.5f),
                    Actions.color(Color.GREEN, 0.2f),
                    Actions.color(Color.WHITE, 0.3f),
                    Actions.run { target.addEffect(poisonEffect) },
                    Actions.run(continuation)
                )
            )
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(unitInRound: UnitInRound): PoisonAbility
    }
}
