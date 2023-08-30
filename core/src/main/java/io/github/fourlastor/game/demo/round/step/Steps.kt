package io.github.fourlastor.game.demo.round.step

import com.badlogic.gdx.math.Interpolation
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.map.Tile
import io.github.fourlastor.game.demo.state.unit.Unit
import java.util.function.BiPredicate
import javax.inject.Inject

class Steps
@Inject
constructor(
    private val searchTileFactory: SearchTile.Factory,
    private val searchUnitFactory: SearchUnit.Factory,
    private val moveFactory: MoveStep.Factory,
    private val attackMeleeFactory: AttackMelee.Factory,
    private val attackRangedFactory: AttackRanged.Factory,
    private val tileSmashFactory: TileSmash.Factory
) {
  fun searchTile(filter: BiPredicate<GameState, Tile>): SearchTile {
    return searchTileFactory.create(filter)
  }

  fun searchUnit(filter: BiPredicate<GameState, Tile>): SearchUnit {
    return searchUnitFactory.create(filter)
  }

  @JvmOverloads
  fun move(
      unit: Unit,
      tile: Tile,
      filter: List<Tile>,
      interpolation: Interpolation = Interpolation.sine
  ): MoveStep {
    return moveFactory.create(unit, tile, filter, interpolation)
  }

  fun attackMelee(source: Unit, target: Unit): AttackMelee {
    return attackMeleeFactory.create(source, target)
  }

  fun attackRanged(source: Unit, target: Unit): AttackRanged {
    return attackRangedFactory.create(source, target)
  }

  fun tileSmash(source: Unit, target: Tile): TileSmash {
    return tileSmashFactory.create(source, target)
  }
}
