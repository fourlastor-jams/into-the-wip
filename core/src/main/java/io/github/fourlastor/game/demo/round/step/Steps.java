package io.github.fourlastor.game.demo.round.step;

import io.github.fourlastor.game.coordinates.Hex;
import io.github.fourlastor.game.demo.state.map.GraphMap;
import io.github.fourlastor.game.demo.state.map.Tile;
import io.github.fourlastor.game.demo.state.unit.Unit;
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

    public SearchTile searchTile(Hex hex, GraphMap.Filter filter) {
        return searchTileFactory.create(hex, filter);
    }

    public SearchUnit searchUnit(Hex hex, GraphMap.Filter filter) {
        return searchUnitFactory.create(hex, filter);
    }

    public MoveStep move(Unit unit, Tile tile, GraphMap.Filter filter) {
        return moveFactory.create(unit, tile, filter);
    }

    public AttackMelee attackMelee(Unit source, Unit target) {
        return attackMeleeFactory.create(source, target);
    }
}
