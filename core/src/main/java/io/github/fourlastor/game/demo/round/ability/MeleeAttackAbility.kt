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
import java.util.function.BiPredicate

class MeleeAttackAbility
@AssistedInject
constructor(
    @Assisted private val unit: Unit,
    @Assisted cancel: Runnable,
    router: StateRouter,
    stateFactory: StepState.Factory,
    private val steps: Steps,
) : Ability(router, stateFactory, cancel) {
  override fun createSteps(state: GameState): Builder<*> {
    val movementLogic = Filter.all(Filter.maxDistance(unit.type.speed), Filter.canTravel(unit))
    val searchLogic =
        Filter.all(
            Filter.canReach(state.tileAt(unit.hex), movementLogic),
            Filter.hasUnit(),
            BiPredicate { _, tile -> unit.hex != tile.hex })
    return start(steps.searchUnit(searchLogic)).sequence { hex ->
      val path =
          ObjectList(state.graph.path(state.tileAt(unit.hex), state.tileAt(hex), movementLogic))
      if (path.size >= 2) {
        val tileIndex = path.size - 1
        return@sequence start(steps.move(unit, path[tileIndex], path.subList(0, tileIndex)))
            .then(steps.attackMelee(unit, state.unitAt(hex)!!))
      } else {
        return@sequence start(steps.attackMelee(unit, state.unitAt(hex)!!))
      }
    }
  }

  @AssistedFactory
  interface Factory {
    fun create(unit: Unit, cancel: Runnable): MeleeAttackAbility
  }
}
