package io.github.fourlastor.game.demo.turns;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.map.MapGraph;
import io.github.fourlastor.game.demo.state.map.Tile;
import io.github.fourlastor.game.demo.state.unit.Unit;
import java.util.Set;

public class PickMove extends TurnState {

    private final StateRouter router;

    private final Unit unit;

    @AssistedInject
    public PickMove(@Assisted Unit unit, StateRouter router) {
        this.router = router;
        this.unit = unit;
    }

    @Override
    public void enter(GameState state) {
        MapGraph localGraph = state.graph.forUnit(unit);
        for (Tile tile : tilesFromUnit(state)) {
            if (localGraph.getIndex(tile) == -1) {
                continue;
            }
            GraphPath<Tile> path = localGraph.calculatePath(unit, tile);
            if (path.getCount() == 0) {
                continue;
            }
            tile.actor.addListener(new MoveListener(tile, path));
            tile.actor.setColor(Color.CORAL);
        }
    }

    @Override
    public void exit(GameState entity) {
        for (Tile tile : entity.tiles) {
            for (EventListener listener : tile.actor.getListeners()) {
                if (listener instanceof MoveListener) {
                    tile.actor.removeListener(listener);
                }
                tile.actor.setColor(Color.WHITE);
            }
        }
    }

    private Set<Tile> tilesFromUnit(GameState entity) {
        MapGraph graph = entity.graph;
        Tile unitTile = graph.get(unit.position);
        return graph.tilesAtDistance(unitTile, 2);
    }

    @AssistedFactory
    public interface Factory {
        PickMove create(Unit unit);
    }

    private class MoveListener extends ClickListener {

        private final Tile tile;
        private final GraphPath<Tile> path;

        private MoveListener(Tile tile, GraphPath<Tile> path) {
            this.tile = tile;
            this.path = path;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            router.move(unit, tile, path);
        }
    }
}
