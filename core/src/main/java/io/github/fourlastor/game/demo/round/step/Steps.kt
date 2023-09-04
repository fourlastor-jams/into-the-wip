package io.github.fourlastor.game.demo.round.step

import com.badlogic.gdx.math.Interpolation
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.map.Tile
import io.github.fourlastor.game.demo.state.unit.Mon
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
        mon: Mon,
        tile: Tile,
        filter: List<Tile>,
        interpolation: Interpolation = Interpolation.sine
    ) = moveFactory.create(mon, tile, filter, interpolation)

    fun attackMelee(source: Mon, target: Mon) = attackMeleeFactory.create(source, target)

    fun attackRanged(source: Mon, target: Mon) = attackRangedFactory.create(source, target)

    fun poison(target: Mon) = poisonFactory.create(target)

    fun summonMountain(source: Mon, target: Tile) = summonMountainFactory.create(source, target)

    fun tileSmash(source: Mon, target: Tile) = tileSmashFactory.create(source, target)

    fun blobAbsorb(source: Mon, target: Mon) = blobAbsorbFactory.create(source, target)

    fun blobToss(source: Mon, targetMon: Mon, targetTile: Tile) =
        blobTossFactory.create(source, targetMon, targetTile)
}
