package io.github.fourlastor.game.demo.round.step;

import com.badlogic.gdx.math.Interpolation;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.map.Tile;
import io.github.fourlastor.game.demo.state.unit.Unit;
import java.util.List;
import java.util.function.BiPredicate;
import javax.inject.Inject;

public class Steps {

    private final SearchTile.Factory searchTileFactory;
    private final SearchUnit.Factory searchUnitFactory;
    private final MoveStep.Factory moveFactory;
    private final AttackMelee.Factory attackMeleeFactory;
    private final AttackRanged.Factory attackRangedFactory;
    private final TileSmash.Factory tileSmashFactory;
    private final BlobAbsorb.Factory blobAbsorbFactory;
    private final BlobToss.Factory blobTossFactory;

    @Inject
    public Steps(
            SearchTile.Factory searchTileFactory,
            SearchUnit.Factory searchUnitFactory,
            MoveStep.Factory moveFactory,
            AttackMelee.Factory attackMeleeFactory,
            AttackRanged.Factory attackRangedFactory,
            TileSmash.Factory tileSmashFactory,
            BlobAbsorb.Factory blobAbsorbFactory,
            BlobToss.Factory blobTossFactory) {
        this.searchTileFactory = searchTileFactory;
        this.searchUnitFactory = searchUnitFactory;
        this.moveFactory = moveFactory;
        this.attackMeleeFactory = attackMeleeFactory;
        this.attackRangedFactory = attackRangedFactory;
        this.tileSmashFactory = tileSmashFactory;
        this.blobAbsorbFactory = blobAbsorbFactory;
        this.blobTossFactory = blobTossFactory;
    }

    public SearchTile searchTile(BiPredicate<GameState, Tile> filter) {
        return searchTileFactory.create(filter);
    }

    public SearchUnit searchUnit(BiPredicate<GameState, Tile> filter) {
        return searchUnitFactory.create(filter);
    }

    public MoveStep move(Unit unit, Tile tile, List<Tile> filter) {
        return move(unit, tile, filter, Interpolation.sine);
    }

    public MoveStep move(Unit unit, Tile tile, List<Tile> filter, Interpolation interpolation) {
        return moveFactory.create(unit, tile, filter, interpolation);
    }

    public AttackMelee attackMelee(Unit source, Unit target) {
        return attackMeleeFactory.create(source, target);
    }

    public AttackRanged attackRanged(Unit source, Unit target) {
        return attackRangedFactory.create(source, target);
    }

    public TileSmash tileSmash(Unit source, Tile target) {
        return tileSmashFactory.create(source, target);
    }

    public BlobAbsorb blobAbsorb(Unit source, Unit target) {
        return blobAbsorbFactory.create(source, target);
    }

    public BlobToss blobToss(Unit source, Unit targetUnit, Tile targetTile) {
        return blobTossFactory.create(source, targetUnit, targetTile);
    }
}
