package io.github.fourlastor.game.demo.round.ability

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
import io.github.fourlastor.game.demo.state.Filter.maxDistance
import io.github.fourlastor.game.demo.state.Filter.sameAxisAs
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.unit.Unit
import java.util.function.BiPredicate

class SummonMountainAbility @AssistedInject constructor(
    @Assisted unitInRound: UnitInRound,
    router: StateRouter,
    stateFactory: StepState.Factory,
    private val steps: Steps
) : Ability(unitInRound, router, stateFactory) {
    private val unit: Unit

    init {
        unit = unitInRound.unit
    }

    override fun createSteps(state: GameState): Builder<*> {
        val movementLogic = all(sameAxisAs(unit.hex), maxDistance(unit.type.speed))
        val searchLogic = all(
            canReach(state.tileAt(unit.hex), movementLogic),
            BiPredicate { _, tile -> unit.canTravel(tile) }
        )
        return start(steps.searchTile(searchLogic)).then { hex ->
            steps.summonMountain(unit, state.tileAt(hex))
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(unitInRound: UnitInRound): SummonMountainAbility
    }
}