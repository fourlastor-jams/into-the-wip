package io.github.fourlastor.game.demo.round.step

import com.badlogic.gdx.math.Interpolation
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.map.Tile
import io.github.fourlastor.game.demo.state.unit.Unit
import java.util.function.BiPredicate
import javax.inject.Inject

class Steps @Inject constructor(
    private val searchTileFactory: SearchTile.Factory,
    private val searchUnitFactory: SearchUnit.Factory,
    private val moveFactory: MoveStep.Factory,
    private val attackMeleeFactory: AttackMelee.Factory,
    private val attackRangedFactory: AttackRanged.Factory,
    private val poisonFactory: Poison.Factory,
    private val summonMountainFactory: SummonMountain.Factory,
    private val tileSmashFactory: TileSmash.Factory,
    private val blobAbsorbFactory: BlobAbsorb.Factory,
    private val blobTossFactory: BlobToss.Factory
) {
    fun searchTile(filter: BiPredicate<GameState, Tile>) = searchTileFactory.create(filter)

    fun searchUnit(filter: BiPredicate<GameState, Tile>) = searchUnitFactory.create(filter)

    fun move(
        unit: Unit,
        tile: Tile,
        filter: List<Tile>,
        interpolation: Interpolation = Interpolation.sine
    ) = moveFactory.create(unit, tile, filter, interpolation)

    fun attackMelee(source: Unit, target: Unit) = attackMeleeFactory.create(source, target)

    fun attackRanged(source: Unit, target: Unit) = attackRangedFactory.create(source, target)

    fun poison(target: Unit) = poisonFactory.create(target)

    fun summonMountain(source: Unit, target: Tile) = summonMountainFactory.create(source, target)

    fun tileSmash(source: Unit, target: Tile) = tileSmashFactory.create(source, target)

    fun blobAbsorb(source: Unit, target: Unit) = blobAbsorbFactory.create(source, target)

    fun blobToss(source: Unit, targetUnit: Unit, targetTile: Tile) =
        blobTossFactory.create(source, targetUnit, targetTile)
}
