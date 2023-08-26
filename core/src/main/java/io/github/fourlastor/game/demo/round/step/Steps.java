package io.github.fourlastor.game.demo.round.step;

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

    @Inject
    public Steps(
            SearchTile.Factory searchTileFactory,
            SearchUnit.Factory searchUnitFactory,
            MoveStep.Factory moveFactory,
            AttackMelee.Factory attackMeleeFactory) {
        this.searchTileFactory = searchTileFactory;
        this.searchUnitFactory = searchUnitFactory;
        this.moveFactory = moveFactory;
        this.attackMeleeFactory = attackMeleeFactory;
    }

    public SearchTile searchTile(BiPredicate<GameState, Tile> filter) {
        return searchTileFactory.create(filter);
    }

    public SearchUnit searchUnit(BiPredicate<GameState, Tile> filter) {
        return searchUnitFactory.create(filter);
    }

    public MoveStep move(Unit unit, Tile tile, List<Tile> path) {
        return moveFactory.create(unit, tile, path);
    }

    public AttackMelee attackMelee(Unit source, Unit target) {
        return attackMeleeFactory.create(source, target);
    }
}
