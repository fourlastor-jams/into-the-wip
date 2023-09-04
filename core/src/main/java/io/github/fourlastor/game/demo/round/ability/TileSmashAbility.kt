package io.github.fourlastor.game.demo.round.ability

import com.badlogic.gdx.math.Interpolation
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
import io.github.fourlastor.game.demo.state.Filter.ofType
import io.github.fourlastor.game.demo.state.Filter.sameAxisAs
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.map.Tile
import io.github.fourlastor.game.demo.state.map.TileType
import io.github.fourlastor.game.demo.state.unit.Unit
import java.util.function.Predicate

class TileSmashAbility @AssistedInject constructor(
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
        val movementLogic = all(
            sameAxisAs(unit.hex),
            Predicate { step -> unit.canTravel(step.previous()) && step.vertex().type === TileType.SOLID }
        )
        val searchLogic = all(canReach(state.tileAt(unit.hex), movementLogic), ofType(TileType.SOLID))
        return start(steps.searchTile(searchLogic)).sequence { hex ->
            val path: List<Tile> = ObjectList(
                state.graph.path(
                    state.tileAt(unit.hex),
                    state.tileAt(hex),
                    movementLogic
                )
            )
            if (path.size >= 2) {
                start(steps.move(unit, path[path.size - 2], path, Interpolation.linear))
                    .then(steps.tileSmash(unit, state.tileAt(hex)))
            } else {
                start(steps.tileSmash(unit, state.tileAt(hex)))
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(unitInRound: UnitInRound): TileSmashAbility
    }
}
