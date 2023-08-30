package io.github.fourlastor.game.demo.round.ability

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.fourlastor.game.demo.round.Ability
import io.github.fourlastor.game.demo.round.StateRouter
import io.github.fourlastor.game.demo.round.step.StepState
import io.github.fourlastor.game.demo.round.step.Steps
import io.github.fourlastor.game.demo.state.Filter
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.map.Tile
import io.github.fourlastor.game.demo.state.unit.Unit
import java.util.function.BiPredicate

class RangedAttackAbility
@AssistedInject
constructor(
    @Assisted private val unit: Unit,
    @Assisted cancel: Runnable,
    router: StateRouter,
    stateFactory: StepState.Factory,
    private val steps: Steps,
) : Ability(router, stateFactory, cancel) {
  override fun createSteps(state: GameState): Builder<*> {
    val movementLogic = Filter.all(Filter.maxDistance(unit.type.speed + 1), Filter.canTravel(unit))
    val searchLogic =
        Filter.all(
            Filter.canReach(state.tileAt(unit.hex), movementLogic),
            BiPredicate<GameState, Tile> { _, tile -> state.unitAt(tile.hex) != null })
    return start(steps.searchTile(searchLogic)).then { hex ->
      steps.attackRanged(unit, state.unitAt(hex)!!)
    }
  }

  @AssistedFactory
  interface Factory {
    fun create(unit: Unit, cancel: Runnable): RangedAttackAbility
  }
}
