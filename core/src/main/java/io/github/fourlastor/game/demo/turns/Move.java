package io.github.fourlastor.game.demo.turns;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.map.MapGraph;
import io.github.fourlastor.game.demo.state.map.Tile;
import io.github.fourlastor.game.demo.state.unit.Unit;
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
            tile.actor.addListener(new MoveListener(tile));
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

        private MoveListener(Tile tile) {
            this.tile = tile;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            super.clicked(event, x, y);
            Vector2 position = unit.coordinates.toWorldAtCenter(
                    tile.coordinates.offset.x, tile.coordinates.offset.y, new Vector2());
            unit.actor.addAction(Actions.sequence(
                    Actions.moveToAligned(position.x, position.y, Align.bottom, 0.25f, Interpolation.sine),
                    Actions.run(() -> unit.position.set(tile.coordinates.offset)),
                    Actions.run(router::pickMonster)));
        }
    }
}
