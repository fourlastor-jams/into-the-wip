package io.github.fourlastor.game.demo.round.step;

import com.badlogic.gdx.math.Interpolation;
import io.github.fourlastor.game.coordinates.Hex;
import io.github.fourlastor.game.demo.state.map.GraphMap;
import io.github.fourlastor.game.demo.state.map.Tile;
import io.github.fourlastor.game.demo.state.unit.Unit;
import javax.inject.Inject;

public class Steps {

    private final SearchTile.Factory searchTileFactory;
    private final SearchSmashTile.Factory searchSmashTileFactory;
    private final SearchUnit.Factory searchUnitFactory;
    private final MoveStep.Factory moveFactory;
    private final AttackMelee.Factory attackMeleeFactory;
    private final TileSmash.Factory tileSmashFactory;

    @Inject
    public Steps(
            SearchTile.Factory searchTileFactory,
            SearchSmashTile.Factory searchSmashTileFactory,
            SearchUnit.Factory searchUnitFactory,
            MoveStep.Factory moveFactory,
            AttackMelee.Factory attackMeleeFactory,
            TileSmash.Factory tileSmashFactory) {
        this.searchTileFactory = searchTileFactory;
        this.searchSmashTileFactory = searchSmashTileFactory;
        this.searchUnitFactory = searchUnitFactory;
        this.moveFactory = moveFactory;
        this.attackMeleeFactory = attackMeleeFactory;
        this.tileSmashFactory = tileSmashFactory;
    }

    public SearchTile searchTile(Hex hex, GraphMap.Filter filter) {
        return searchTileFactory.create(hex, filter);
    }

    public SearchSmashTile searchSmashTile(Hex hex, GraphMap.Filter filter) {
        return searchSmashTileFactory.create(hex, filter);
    }

    public SearchUnit searchUnit(Hex hex, GraphMap.Filter filter) {
        return searchUnitFactory.create(hex, filter);
    }

    public MoveStep move(Unit unit, Tile tile, GraphMap.Filter filter) {
        return move(unit, tile, filter, Interpolation.sine);
    }

    public MoveStep move(Unit unit, Tile tile, GraphMap.Filter filter, Interpolation interpolation) {
        return moveFactory.create(unit, tile, filter, interpolation);
    }

    public AttackMelee attackMelee(Unit source, Unit target) {
        return attackMeleeFactory.create(source, target);
    }

    public TileSmash tileSmash(Unit source, Tile target) {
        return tileSmashFactory.create(source, target);
    }
}
