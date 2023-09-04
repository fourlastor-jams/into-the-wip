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
import io.github.fourlastor.game.demo.state.Filter.canTravel
import io.github.fourlastor.game.demo.state.Filter.hasUnit
import io.github.fourlastor.game.demo.state.Filter.maxDistance
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.unit.Mon
import java.util.function.BiPredicate

class MeleeAttackAbility @AssistedInject constructor(
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
        val movementLogic = all(maxDistance(mon.type.speed), canTravel(mon))
        val searchLogic = all(
            canReach(state.tileAt(mon.hex), movementLogic),
            hasUnit(),
            BiPredicate { _, tile -> mon.hex != tile.hex }
        )
        return start(steps.searchUnit(searchLogic)).sequence { hex ->
            val path = state.graph.path(
                state.tileAt(mon.hex),
                state.tileAt(
                    hex
                ),
                movementLogic
            ).toList()
            if (path.size >= 2) {
                val tileIndex = path.size - 2
                start(steps.move(mon, path[tileIndex], path.subList(0, tileIndex + 1)))
                    .then(steps.attackMelee(mon, state.unitAt(hex)!!))
            } else {
                start(steps.attackMelee(mon, state.unitAt(hex)!!))
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(unitInRound: UnitInRound): MeleeAttackAbility
    }
}
