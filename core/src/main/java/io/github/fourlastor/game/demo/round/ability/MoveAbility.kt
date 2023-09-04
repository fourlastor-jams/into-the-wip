package io.github.fourlastor.game.demo.round.ability

import com.github.tommyettinger.ds.ObjectList
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

class MoveAbility @AssistedInject constructor(
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
        val searchLogic = canReach(state.tileAt(mon.hex), movementLogic)
        return start(steps.searchTile(searchLogic))
            .then { hex ->
                steps.move(
                    mon,
                    state.tileAt(hex),
                    ObjectList(
                        state.graph.path(
                            state.tileAt(mon.hex),
                            state.tileAt(
                                hex
                            ),
                            movementLogic
                        )
                    )
                )
            }
    }

    @AssistedFactory
    interface Factory {
        fun create(unitInRound: UnitInRound): MoveAbility
    }
}
