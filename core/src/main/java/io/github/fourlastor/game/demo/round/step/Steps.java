package io.github.fourlastor.game.demo.round.step;

import com.badlogic.gdx.ai.pfa.GraphPath;
import io.github.fourlastor.game.demo.state.map.Tile;
import io.github.fourlastor.game.demo.state.unit.Unit;
import javax.inject.Inject;

public class Steps {

    private final SearchStep.Factory searchFactory;
    private final MoveStep.Factory moveFactory;

    @Inject
    public Steps(SearchStep.Factory searchFactory, MoveStep.Factory moveFactory) {
        this.searchFactory = searchFactory;
        this.moveFactory = moveFactory;
    }

    public SearchStep search(Unit unit) {
        return searchFactory.create(unit);
    }

    public MoveStep move(Unit unit, Tile tile, GraphPath<Tile> path) {
        return moveFactory.create(unit, tile, path);
    }
}
