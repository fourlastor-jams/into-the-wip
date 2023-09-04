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
import io.github.fourlastor.game.demo.state.unit.Unit

class MoveAbility @AssistedInject constructor(
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
        val movementLogic = all(maxDistance(unit.type.speed), canTravel(unit))
        val searchLogic = canReach(state.tileAt(unit.hex), movementLogic)
        return start(steps.searchTile(searchLogic))
            .then { hex ->
                steps.move(
                    unit,
                    state.tileAt(hex),
                    ObjectList(
                        state.graph.path(
                            state.tileAt(unit.hex),
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
