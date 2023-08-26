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
    private final TileSmash.Factory tileSmashFactory;

    @Inject
    public Steps(
            SearchTile.Factory searchTileFactory,
            SearchUnit.Factory searchUnitFactory,
            MoveStep.Factory moveFactory,
            AttackMelee.Factory attackMeleeFactory,
            TileSmash.Factory tileSmashFactory) {
        this.searchTileFactory = searchTileFactory;
        this.searchUnitFactory = searchUnitFactory;
        this.moveFactory = moveFactory;
        this.attackMeleeFactory = attackMeleeFactory;
        this.tileSmashFactory = tileSmashFactory;
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

    public TileSmash tileSmash(Unit source, Tile target) {
        return tileSmashFactory.create(source, target);
    }
}
