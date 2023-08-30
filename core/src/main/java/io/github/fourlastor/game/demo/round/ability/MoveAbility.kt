package io.github.fourlastor.game.demo.round.ability

import com.github.tommyettinger.ds.ObjectList
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.fourlastor.game.demo.round.Ability
import io.github.fourlastor.game.demo.round.StateRouter
import io.github.fourlastor.game.demo.round.step.StepState
import io.github.fourlastor.game.demo.round.step.Steps
import io.github.fourlastor.game.demo.state.Filter
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.unit.Unit

class MoveAbility
@AssistedInject
constructor(
    @param:Assisted private val unit: Unit,
    @Assisted cancel: Runnable,
    router: StateRouter,
    stateFactory: StepState.Factory,
    private val steps: Steps
) : Ability(router, stateFactory, cancel) {
  override fun createSteps(state: GameState): Builder<*> {
    val movementLogic = Filter.all(Filter.maxDistance(unit.type.speed), Filter.canTravel(unit))
    val searchLogic = Filter.canReach(state.tileAt(unit.hex), movementLogic)
    return start(steps.searchTile(searchLogic)).then { hex ->
      steps.move(
          unit,
          state.tileAt(hex),
          ObjectList(state.graph.path(state.tileAt(unit.hex), state.tileAt(hex), movementLogic)))
    }
  }

  @AssistedFactory
  interface Factory {
    fun create(unit: Unit, cancel: Runnable): MoveAbility
  }
}
