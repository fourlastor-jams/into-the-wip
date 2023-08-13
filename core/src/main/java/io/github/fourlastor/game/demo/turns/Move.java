package io.github.fourlastor.game.demo.turns;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.map.MapGraph;
import io.github.fourlastor.game.demo.state.map.Tile;
import io.github.fourlastor.game.demo.state.unit.Unit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Move extends TurnState {

    private final StateRouter router;

    private final Unit unit;

    @AssistedInject
    public Move(@Assisted Unit unit, StateRouter router) {
        this.router = router;
        this.unit = unit;
    }

    @Override
    public void enter(GameState entity) {
        for (Tile tile : tilesFromUnit(entity)) {
            tile.actor.addListener(new MoveListener(tile, entity));
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
        Move create(Unit unit);
    }

    private class MoveListener extends ClickListener {

        private final Tile tile;
        private final GameState state;

        private MoveListener(Tile tile, GameState state) {
            this.tile = tile;
            this.state = state;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            super.clicked(event, x, y);
            GraphPath<Tile> path = state.graph.calculatePath(state.graph.get(unit.position), tile);
            List<Action> actions = new ArrayList<>();
            for (int i = 1; i < path.getCount(); i++) {
                Tile pathTile = path.get(i);
                Vector2 position = unit.coordinates.toWorldAtCenter(
                        pathTile.coordinates.offset.x, pathTile.coordinates.offset.y, new Vector2());
                actions.add(Actions.moveToAligned(position.x, position.y, Align.bottom, 0.25f, Interpolation.sine));
            }
            SequenceAction steps = Actions.sequence(actions.toArray(new Action[0]));
            unit.actor.addAction(Actions.sequence(
                    steps,
                    Actions.run(() -> unit.position.set(tile.coordinates.offset)),
                    Actions.run(router::pickMonster)));
        }
    }
}
