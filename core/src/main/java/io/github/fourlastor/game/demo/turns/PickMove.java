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
        for (Tile tile : state.tiles) {
            if (localGraph.getIndex(tile) == -1) {
                continue;
            }
            GraphPath<Tile> path = localGraph.calculatePath(unit.position, tile);
            // path includes the tile at the unit's location, so it's 1 longer than expected
            if (path.getCount() <= 1 || path.getCount() > unit.type.speed + 1) {
                continue;
            }
            Unit tileUnit = state.unitAt(tile.coordinates);
            if (unit == tileUnit || tileUnit == null) {
                tile.actor.addListener(new MoveListener(tile, path));
                tile.actor.setColor(Color.CORAL);
            } else {
                tileUnit.image.addListener(new AttackListener(tileUnit));
                tileUnit.image.setColor(Color.CORAL);
            }
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
        for (Unit unit : entity.units) {
            for (EventListener listener : unit.image.getListeners()) {
                if (listener instanceof AttackListener) {
                    unit.image.removeListener(listener);
                }
                unit.image.setColor(Color.WHITE);
            }
        }
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

    private class AttackListener extends ClickListener {

        private final Unit target;

        private AttackListener(Unit target) {
            this.target = target;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            router.attackMelee(unit, target);
        }
    }
}
