package io.github.fourlastor.game.demo.round.ability

import com.badlogic.gdx.math.Interpolation
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
import io.github.fourlastor.game.demo.state.map.TileType
import io.github.fourlastor.game.demo.state.unit.Unit
import java.util.function.Predicate
import space.earlygrey.simplegraphs.algorithms.SearchStep

class TileSmashAbility
@AssistedInject
constructor(
    @Assisted private val unit: Unit,
    @Assisted cancel: Runnable,
    router: StateRouter,
    stateFactory: StepState.Factory,
    private val steps: Steps,
) : Ability(router, stateFactory, cancel) {
  override fun createSteps(state: GameState): Builder<*> {
    val movementLogic =
        Filter.all(
            Filter.sameAxisAs(unit.hex),
            Predicate<SearchStep<Tile>> { step: SearchStep<Tile> ->
              unit.canTravel(step.previous()) && step.vertex().type == TileType.SOLID
            })
    val searchLogic =
        Filter.all(
            Filter.canReach(state.tileAt(unit.hex), movementLogic), Filter.ofType(TileType.SOLID))
    return start(steps.searchTile(searchLogic)).sequence { hex ->
      val path = state.graph.path(state.tileAt(unit.hex), state.tileAt(hex), movementLogic).toList()
      if (path.size >= 2) {
        return@sequence start(steps.move(unit, path[path.size - 2], path, Interpolation.linear))
            .then(steps.tileSmash(unit, state.tileAt(hex)))
      } else {
        return@sequence start(steps.tileSmash(unit, state.tileAt(hex)))
      }
    }
  }

  @AssistedFactory
  interface Factory {
    fun create(unit: Unit, cancel: Runnable): TileSmashAbility
  }
}
