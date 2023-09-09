package io.github.fourlastor.game.demo.round.ability

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.fourlastor.game.demo.round.Ability
import io.github.fourlastor.game.demo.round.StateRouter
import io.github.fourlastor.game.demo.round.UnitInRound
import io.github.fourlastor.game.demo.round.step.StepState
import io.github.fourlastor.game.demo.round.step.Steps
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.unit.Mon

class ChargeBeamAbility @AssistedInject constructor(
    @Assisted unitInRound: UnitInRound,
    router: StateRouter,
    stateFactory: StepState.Factory,
    private val steps: Steps,
) : Ability(unitInRound, router, stateFactory) {
    private val mon: Mon

    init {
        mon = unitInRound.mon
    }

    override fun createSteps(state: GameState): Builder<*> {
        return start(steps.chargeBeam(mon))
    }

    @AssistedFactory
    interface Factory {
        fun create(unitInRound: UnitInRound): ChargeBeamAbility
    }
}
