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

    fun blobAbsorb(source: Mon, target: Mon) = blobAbsorbFactory.create(source, target)

    fun blobToss(source: Mon, targetMon: Mon, targetTile: Tile) =
        blobTossFactory.create(source, targetMon, targetTile)
}
