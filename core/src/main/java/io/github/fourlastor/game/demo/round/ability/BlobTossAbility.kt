package io.github.fourlastor.game.demo.round.ability

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.fourlastor.game.coordinates.Hex
import io.github.fourlastor.game.demo.round.Ability
import io.github.fourlastor.game.demo.round.StateRouter
import io.github.fourlastor.game.demo.round.UnitInRound
import io.github.fourlastor.game.demo.round.step.StepState
import io.github.fourlastor.game.demo.round.step.Steps
import io.github.fourlastor.game.demo.state.Filter.all
import io.github.fourlastor.game.demo.state.Filter.canReach
import io.github.fourlastor.game.demo.state.Filter.maxDistance
import io.github.fourlastor.game.demo.state.Filter.ofType
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.map.TileType
import io.github.fourlastor.game.demo.state.unit.Mon

class BlobTossAbility @AssistedInject constructor(
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
        val movementLogic = maxDistance(mon.type.speed)
        val searchLogic = all(
            canReach(state.tileAt(mon.hex), movementLogic),
            ofType(TileType.SOLID).negate(),
            ofType(TileType.WATER).negate()
        )
        return start(steps.searchTile(searchLogic))
            .then { hex: Hex ->
                steps.blobToss(
                    mon,
                    state.unitAt { it.hex.equals(mon.hex) && it != mon }!!,
                    state.tileAt(hex)
                )
            }
    }

    @AssistedFactory
    interface Factory {
        fun create(unitInRound: UnitInRound): BlobTossAbility
    }
}
