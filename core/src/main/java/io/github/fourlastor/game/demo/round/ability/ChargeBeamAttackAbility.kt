package io.github.fourlastor.game.demo.round.ability

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.fourlastor.game.demo.round.Ability
import io.github.fourlastor.game.demo.round.StateRouter
import io.github.fourlastor.game.demo.round.UnitInRound
import io.github.fourlastor.game.demo.round.step.StepState
import io.github.fourlastor.game.demo.round.step.Steps
import io.github.fourlastor.game.demo.state.Filter
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.unit.Mon
import java.util.function.BiPredicate

class ChargeBeamAttackAbility @AssistedInject constructor(
    @Assisted unitInRound: UnitInRound,
    router: StateRouter,
    stateFactory: StepState.Factory,
    private val steps: Steps
) : Ability(unitInRound, router, stateFactory) {
    private val mon: Mon

    init {
        mon = unitInRound.mon
    }

    override fun createSteps(state: GameState): Builder<*> {
        val movementLogic = Filter.all(Filter.maxDistance(mon.type.speed + 1), Filter.canTravel(mon))
        val searchLogic = Filter.all(
            Filter.canReach(state.tileAt(mon.hex), movementLogic),
            BiPredicate { _, tile -> state.unitAt(tile.hex) != null }
        )

        return start(steps.searchTile(searchLogic)).then { hex ->
            steps.chargeBeamAttack(mon, state.unitAt(hex)!!)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(unitInRound: UnitInRound): ChargeBeamAttackAbility
    }
}
